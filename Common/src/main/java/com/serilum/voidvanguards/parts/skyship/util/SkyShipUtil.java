package com.serilum.voidvanguards.parts.skyship.util;

import com.serilum.voidvanguards.parts.skyship.data.SkyShipVariables;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;

public class SkyShipUtil {
	public static float getShipSunAngle(Minecraft mc, float partialTicks) {
		DimensionType dimensionType = mc.level.dimensionType();
		float previousTimeOfDay = dimensionType.timeOfDay(SkyShipVariables.previousDayTime);
		float currentTimeOfDay = dimensionType.timeOfDay(SkyShipVariables.currentDayTime);
		if (currentTimeOfDay < previousTimeOfDay) {
			currentTimeOfDay += 1.0F;
		}

		float timeOfDay = Mth.lerp(partialTicks, previousTimeOfDay, currentTimeOfDay);
		return timeOfDay * 2.0F * (float) Math.PI;
	}

	public static void trackDayTime(Minecraft mc) {
		long dayTime = mc.level.getDayTime();
		long ticksPassed = dayTime - SkyShipVariables.currentDayTime;

		if (ticksPassed < 0 || ticksPassed > 20) {
			SkyShipVariables.previousDayTime = dayTime;
		}
		else {
			SkyShipVariables.previousDayTime = SkyShipVariables.currentDayTime;
		}

		SkyShipVariables.currentDayTime = dayTime;
	}

	public static double getAnimationTime(Minecraft mc, float partialTicks) {
		return mc.level.getGameTime() + (double) partialTicks;
	}
}
