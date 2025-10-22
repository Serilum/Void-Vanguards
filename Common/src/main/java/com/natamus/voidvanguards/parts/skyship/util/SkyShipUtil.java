package com.natamus.voidvanguards.parts.skyship.util;

import net.minecraft.client.Minecraft;

public class SkyShipUtil {
	public static float getShipSunAngle(Minecraft mc, float partialTicks) {
		float celestialAngle = mc.level.getSunAngle(partialTicks);
		return celestialAngle * 2.0F * (float) Math.PI;
	}
}
