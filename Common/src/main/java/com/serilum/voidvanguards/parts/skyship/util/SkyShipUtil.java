package com.serilum.voidvanguards.parts.skyship.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.attribute.EnvironmentAttributes;

public class SkyShipUtil {
	public static float getShipSunAngle(Minecraft mc, float partialTicks) {
		return mc.gameRenderer.mainCamera().attributeProbe().getValue(EnvironmentAttributes.SUN_ANGLE, partialTicks) * (float) (Math.PI / 180.0);
	}

	public static double getAnimationTime(Minecraft mc, float partialTicks) {
		return mc.level.getGameTime() + (double) partialTicks;
	}
}
