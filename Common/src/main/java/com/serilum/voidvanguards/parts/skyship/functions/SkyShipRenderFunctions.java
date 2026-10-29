package com.serilum.voidvanguards.parts.skyship.functions;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import com.serilum.voidvanguards.data.ClientConstants;
import com.serilum.voidvanguards.parts.skyship.data.SkyShipColours;
import com.serilum.voidvanguards.parts.skyship.data.SkyShipPatterns;
import com.serilum.voidvanguards.parts.skyship.data.SkyShipVariables;
import com.serilum.voidvanguards.parts.skyship.util.SkyShipUtil;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.OptionalDouble;

public class SkyShipRenderFunctions {
	private static final ByteBufferBuilder skyShipByteBufferBuilder = new ByteBufferBuilder(2048);
	private static @Nullable GpuBuffer skyShipVertexBuffer = null;

	public static void renderOrbitingShip(Matrix4f matrix) {
		if (ClientConstants.mc.level == null) {
			return;
		}

		if (!SkyShipVariables.skyShipVisible) {
			return;
		}

		long time = ClientConstants.mc.level.getGameTime();
		float partialTicks = ClientConstants.mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
		double animationTime = SkyShipUtil.getAnimationTime(ClientConstants.mc, partialTicks);

		float sunAngle = SkyShipUtil.getShipSunAngle(ClientConstants.mc, partialTicks);

		Vector3f sunPos = new Vector3f(
				(float) Math.sin(sunAngle),
				(float) Math.cos(sunAngle),
				0.0F
		).normalize();

		double orbitAngle = animationTime * SkyShipVariables.skyShipOrbitSpeed;

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

				renderExplosionFrame(matrix, animationTime, skyShipPos, offset, explosionFrame);
				return;
			} else {
				// Explosion finished
				SkyShipVariables.skyShipVisible = false;
				SkyShipVariables.explosionStartTime = 0;
				return;
			}
		}

		renderNormalShip(matrix, animationTime, skyShipPos, offset);
	}

	private static void renderNormalShip(Matrix4f matrix, double time, Vector3f skyShipPos, Vector3f offset) {
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
		float basePixelSize = SkyShipVariables.skyShipBaseSize * flicker * SkyShipVariables.skyShipRenderDistance;
		float pixelWidth = basePixelSize / (cols / 3.0f);  // Normalize to level 0 width (3 pixels)
		float pixelHeight = basePixelSize / (rows / 8.0f); // Normalize to level 0 height (8 pixels)

		skyShipPos.y += SkyShipVariables.skyShipHoverAmount * (float) Math.sin(time * 0.08);
		Vector3f elong = new Vector3f(offset).normalize().mul(SkyShipVariables.skyShipElongationFactor * flicker);
		skyShipPos.add(elong);

		Vector3f skyShipDirection = new Vector3f(skyShipPos).normalize();
		skyShipPos.set(skyShipDirection).mul(SkyShipVariables.skyShipRenderDistance);

		Quaternionf faceCamera = new Quaternionf().rotateTo(new Vector3f(0, 0, -1), skyShipDirection);

		// Render skyship
		BufferBuilder buffer = new BufferBuilder(skyShipByteBufferBuilder, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);

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

		drawSkyQuads(buffer);
	}

	private static void renderExplosionFrame(Matrix4f matrix, double time, Vector3f skyShipPos, Vector3f offset, int explosionFrame) {
		// Use larger base size for explosion
		float basePixelSize = SkyShipVariables.skyShipBaseSize * 1.5f * SkyShipVariables.skyShipRenderDistance;

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

		Vector3f skyShipDirection = new Vector3f(skyShipPos).normalize();
		skyShipPos.set(skyShipDirection).mul(SkyShipVariables.skyShipRenderDistance);

		Quaternionf faceCamera = new Quaternionf().rotateTo(new Vector3f(0, 0, -1), skyShipDirection);

		// Render explosion
		BufferBuilder buffer = new BufferBuilder(skyShipByteBufferBuilder, PrimitiveTopology.QUADS, DefaultVertexFormat.POSITION_COLOR);

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

		drawSkyQuads(buffer);
	}

	private static void drawSkyQuads(BufferBuilder buffer) {
		MeshData meshData = buffer.build();
		if (meshData == null) {
			return;
		}

		int indexCount = meshData.drawState().indexCount();
		PrimitiveTopology primitiveTopology = meshData.drawState().primitiveTopology();
		uploadSkyShipVertices(meshData.vertexBuffer());
		meshData.close();

		RenderSystem.AutoStorageIndexBuffer autoIndices = RenderSystem.getSequentialBuffer(primitiveTopology);
		GpuBuffer indexBuffer = autoIndices.getBuffer(indexCount);

		GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().writeTransform(new Matrix4f());
		RenderTarget mainRenderTarget = ClientConstants.mc.gameRenderer.mainRenderTarget();

		try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Void Vanguards sky ship", mainRenderTarget.getColorTextureView(), Optional.empty(), mainRenderTarget.getDepthTextureView(), OptionalDouble.empty())) {
			renderPass.setPipeline(RenderSystem.getCompiledPipeline(RenderPipelines.DEBUG_QUADS));
			RenderSystem.bindDefaultUniforms(renderPass);
			renderPass.setUniform("DynamicTransforms", dynamicTransforms);
			renderPass.setIndexBuffer(indexBuffer, autoIndices.type());
			renderPass.setVertexBuffer(0, skyShipVertexBuffer.slice());
			renderPass.drawIndexed(indexCount, 1, 0, 0, 0);
		}
	}

	private static void uploadSkyShipVertices(ByteBuffer vertexData) {
		GpuDevice device = RenderSystem.getDevice();
		if (skyShipVertexBuffer == null || skyShipVertexBuffer.size() < vertexData.remaining()) {
			if (skyShipVertexBuffer != null) {
				skyShipVertexBuffer.close();
			}

			skyShipVertexBuffer = device.createBuffer(() -> "Void Vanguards Sky Ship Vertex Buffer", GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST, vertexData.remaining());
		}

		device.createCommandEncoder().writeToBuffer(skyShipVertexBuffer.slice(), vertexData);
	}
}
