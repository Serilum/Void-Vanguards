package com.natamus.voidvanguards.parts.skyship.functions;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.natamus.voidvanguards.data.ClientConstants;
import com.natamus.voidvanguards.parts.skyship.data.SkyShipColours;
import com.natamus.voidvanguards.parts.skyship.data.SkyShipPatterns;
import com.natamus.voidvanguards.parts.skyship.data.SkyShipVariables;
import com.natamus.voidvanguards.parts.skyship.util.SkyShipUtil;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

public class SkyShipRenderFunctions {
	public static void renderOrbitingShip(Matrix4f matrix) {
		if (ClientConstants.mc.level == null) {
			return;
		}

		if (!SkyShipVariables.skyShipVisible) {
			return;
		}

		long time = ClientConstants.mc.level.getGameTime();
		float partialTicks = ClientConstants.mc.getFrameTimeNs();

		float sunAngle = SkyShipUtil.getShipSunAngle(ClientConstants.mc, partialTicks);

		Vector3f sunPos = new Vector3f(
				(float) Math.sin(sunAngle),
				(float) Math.cos(sunAngle),
				0.0F
		).normalize();

		float orbitAngle = time * SkyShipVariables.skyShipOrbitSpeed;

		Vector3f offset = new Vector3f(
				(float) Math.cos(orbitAngle) * SkyShipVariables.skyShipOrbitRadius,
				(float) Math.sin(orbitAngle) * SkyShipVariables.skyShipOrbitRadius,
				90.0F
		);

		Quaternionf rotToSun = new Quaternionf().rotateTo(new Vector3f(0, 1, 0), sunPos);
		offset.rotate(rotToSun);

		Vector3f skyShipPos = new Vector3f(sunPos).mul(100.0F).add(offset);

		if (SkyShipVariables.triggeredSkyShipEvent) {
			// Use explosion patterns instead of particles
			if (SkyShipVariables.explosionStartTime == 0) {
				SkyShipVariables.explosionStartTime = time;
			}

			long explosionAge = time - SkyShipVariables.explosionStartTime;

			// Show explosion for ticksNeededSkyShipExplosion ticks
			if (explosionAge < SkyShipVariables.ticksNeededSkyShipExplosion) {
				// Calculate frame duration (4 frames total)
				int frameDuration = SkyShipVariables.ticksNeededSkyShipExplosion / 4;

				// Determine which explosion frame to show
				int explosionFrame;
				if (explosionAge < frameDuration) {
					explosionFrame = 6; // First explosion frame
				} else if (explosionAge < frameDuration * 2) {
					explosionFrame = 7; // Second explosion frame
				} else if (explosionAge < frameDuration * 3) {
					explosionFrame = 8; // Third explosion frame
				} else {
					explosionFrame = 9; // Final explosion frame
				}

				renderExplosionFrame(matrix, time, skyShipPos, offset, explosionFrame);
				return;
			} else {
				// Explosion finished
				SkyShipVariables.skyShipVisible = false;
				SkyShipVariables.explosionStartTime = 0;
				return;
			}
		}

		renderNormalShip(matrix, time, skyShipPos, offset);
	}

	private static void renderNormalShip(Matrix4f matrix, long time, Vector3f skyShipPos, Vector3f offset) {
		float flicker = 1.0F - SkyShipVariables.skyShipFlickerSizeAmount + SkyShipVariables.skyShipFlickerSizeAmount * (float) Math.sin(time * SkyShipVariables.skyShipFlickerSpeed);

		// Get current ship detail level
		int detailLevel = Math.min(Math.max(SkyShipVariables.skyShipDetailLevel, 0), SkyShipPatterns.SHIP_PATTERNS.length - 1);

		// Get current pattern and colors
		int[][] shipPattern = SkyShipPatterns.SHIP_PATTERNS[detailLevel];
		int[][] shipColors = SkyShipColours.SHIP_COLORS[detailLevel];

		int rows = shipPattern.length;
		int cols = shipPattern[0].length;

		// SIMPLE FIX: Use the same base size for all levels, but divide by pixel dimensions
		// This ensures more pixels = smaller individual pixels = same overall ship size
		float basePixelSize = SkyShipVariables.skyShipBaseSize * flicker;
		float pixelWidth = basePixelSize / (cols / 3.0f);  // Normalize to level 0 width (3 pixels)
		float pixelHeight = basePixelSize / (rows / 8.0f); // Normalize to level 0 height (8 pixels)

		skyShipPos.y += SkyShipVariables.skyShipHoverAmount * (float) Math.sin(time * 0.08);
		Vector3f elong = new Vector3f(offset).normalize().mul(SkyShipVariables.skyShipElongationFactor * flicker);
		skyShipPos.add(elong);

		Quaternionf faceCamera = new Quaternionf().rotateTo(new Vector3f(0, 0, -1), skyShipPos.normalize());

		// Render skyship
		boolean depthTestEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
		boolean depthMaskEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);

		if (!depthTestEnabled) {
			// RenderSystem.enableDepthTest();
		}
		if (!depthMaskEnabled) {
			RenderSystem.depthMask(true);
		}
		// RenderSystem.depthFunc(GL11.GL_LEQUAL);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		Tesselator tess = Tesselator.getInstance();
		BufferBuilder buffer = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				int colorIndex = shipPattern[y][x];
				if (colorIndex == 0) continue;

				// Color indices are 1-based in the pattern
				int[] col = shipColors[Math.min(colorIndex - 1, shipColors.length - 1)];
				int pxR = col[0], pxG = col[1], pxB = col[2];
				int pxA = 255;

				float pxX = (x - cols / 2f) * pixelWidth;
				float pxY = (rows / 2f - y) * pixelHeight;

				Vector3f v1 = new Vector3f(pxX + pixelWidth, pxY - pixelHeight, 0).rotate(faceCamera);
				Vector3f v2 = new Vector3f(pxX + pixelWidth, pxY + pixelHeight, 0).rotate(faceCamera);
				Vector3f v3 = new Vector3f(pxX - pixelWidth, pxY + pixelHeight, 0).rotate(faceCamera);
				Vector3f v4 = new Vector3f(pxX - pixelWidth, pxY - pixelHeight, 0).rotate(faceCamera);

				buffer.addVertex(matrix, skyShipPos.x + v1.x, skyShipPos.y + v1.y, skyShipPos.z + v1.z).setColor(pxR, pxG, pxB, pxA);
				buffer.addVertex(matrix, skyShipPos.x + v2.x, skyShipPos.y + v2.y, skyShipPos.z + v2.z).setColor(pxR, pxG, pxB, pxA);
				buffer.addVertex(matrix, skyShipPos.x + v3.x, skyShipPos.y + v3.y, skyShipPos.z + v3.z).setColor(pxR, pxG, pxB, pxA);
				buffer.addVertex(matrix, skyShipPos.x + v4.x, skyShipPos.y + v4.y, skyShipPos.z + v4.z).setColor(pxR, pxG, pxB, pxA);
			}
		}

		// Halo uses average of width and height for size
		float haloSize = (pixelWidth + pixelHeight) * 0.5f * SkyShipVariables.skyShipHaloSizeFactor;
		int[] haloColor = shipColors[1]; // Green is typically at index 1

		for (int i = 0; i < 4; i++) {
			double angle = i * Math.PI / 2 + time * 0.05;
			Vector3f haloOffset = new Vector3f(
					(float) Math.cos(angle) * haloSize,
					(float) Math.sin(angle) * haloSize,
					0
			);
			haloOffset.rotate(faceCamera);

			buffer.addVertex(matrix, skyShipPos.x + haloOffset.x, skyShipPos.y + haloOffset.y, skyShipPos.z + haloOffset.z)
					.setColor(haloColor[0], haloColor[1], haloColor[2], SkyShipVariables.skyShipHaloAlpha);
		}

		BufferUploader.drawWithShader(buffer.build());

		//

		RenderSystem.disableBlend();

		if (!depthTestEnabled) {
			// RenderSystem.disableDepthTest();
		}
		if (!depthMaskEnabled) {
			RenderSystem.depthMask(false);
		}
	}

	private static void renderExplosionFrame(Matrix4f matrix, long time, Vector3f skyShipPos, Vector3f offset, int explosionFrame) {
		// Use larger base size for explosion
		float basePixelSize = SkyShipVariables.skyShipBaseSize * 1.5f;

		// Get explosion pattern and colors
		int[][] explosionPattern = SkyShipPatterns.EXPLOSION_PATTERNS[explosionFrame - 6]; // Convert to 0-based index
		int[][] explosionColors = SkyShipColours.EXPLOSION_COLORS[explosionFrame - 6];

		int rows = explosionPattern.length;
		int cols = explosionPattern[0].length;

		float pixelWidth = basePixelSize / (cols / 3.0f);
		float pixelHeight = basePixelSize / (rows / 8.0f);

		// Add some random movement to explosion position for effect
		skyShipPos.y += (float) Math.sin(time * 0.3) * 2.0f;
		skyShipPos.x += (float) Math.cos(time * 0.2) * 1.5f;

		Quaternionf faceCamera = new Quaternionf().rotateTo(new Vector3f(0, 0, -1), skyShipPos.normalize());

		// Render explosion
		boolean depthTestEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
		boolean depthMaskEnabled = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);

		if (!depthTestEnabled) {
			// RenderSystem.enableDepthTest();
		}
		if (!depthMaskEnabled) {
			RenderSystem.depthMask(true);
		}
		// RenderSystem.depthFunc(GL11.GL_LEQUAL);

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);

		Tesselator tess = Tesselator.getInstance();
		BufferBuilder buffer = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				int colorIndex = explosionPattern[y][x];
				if (colorIndex == 0) continue;

				int[] col = explosionColors[Math.min(colorIndex - 1, explosionColors.length - 1)];
				int pxR = col[0], pxG = col[1], pxB = col[2];
				int pxA = 255;

				float pxX = (x - cols / 2f) * pixelWidth;
				float pxY = (rows / 2f - y) * pixelHeight;

				Vector3f v1 = new Vector3f(pxX + pixelWidth, pxY - pixelHeight, 0).rotate(faceCamera);
				Vector3f v2 = new Vector3f(pxX + pixelWidth, pxY + pixelHeight, 0).rotate(faceCamera);
				Vector3f v3 = new Vector3f(pxX - pixelWidth, pxY + pixelHeight, 0).rotate(faceCamera);
				Vector3f v4 = new Vector3f(pxX - pixelWidth, pxY - pixelHeight, 0).rotate(faceCamera);

				buffer.addVertex(matrix, skyShipPos.x + v1.x, skyShipPos.y + v1.y, skyShipPos.z + v1.z).setColor(pxR, pxG, pxB, pxA);
				buffer.addVertex(matrix, skyShipPos.x + v2.x, skyShipPos.y + v2.y, skyShipPos.z + v2.z).setColor(pxR, pxG, pxB, pxA);
				buffer.addVertex(matrix, skyShipPos.x + v3.x, skyShipPos.y + v3.y, skyShipPos.z + v3.z).setColor(pxR, pxG, pxB, pxA);
				buffer.addVertex(matrix, skyShipPos.x + v4.x, skyShipPos.y + v4.y, skyShipPos.z + v4.z).setColor(pxR, pxG, pxB, pxA);
			}
		}

		BufferUploader.drawWithShader(buffer.build());

		RenderSystem.disableBlend();

		if (!depthTestEnabled) {
			// RenderSystem.disableDepthTest();
		}
		if (!depthMaskEnabled) {
			RenderSystem.depthMask(false);
		}
	}
}