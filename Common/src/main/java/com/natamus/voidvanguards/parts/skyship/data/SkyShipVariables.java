package com.natamus.voidvanguards.parts.skyship.data;

import com.natamus.voidvanguards.config.ConfigHandler;

public class SkyShipVariables {
	public static boolean skyShipVisible = false;
	public static boolean triggeredSkyShipEvent = false;
	public static boolean sentFocusMessage = false;
	public static boolean focusedOnSkyShip = false;

	public static int ticksNeededSkyShipExplosion = 40;
	public static int ticksLeftForMoreDetail = ConfigHandler.ticksNeededForMoreSkyShipDetail;

	public static int skyShipDetailLevel = 0;
	public static long explosionStartTime = 0;

	public static final float skyShipOrbitRadius = 15.0F;
	public static final float skyShipOrbitSpeed = 0.001F;
	public static final float skyShipBaseSize = 0.006F;
	public static final float skyShipHoverAmount = 0.1F;
	public static final float skyShipElongationFactor = 0.02F;
	public static final float skyShipHaloSizeFactor = 0.3F;
	public static final float skyShipFlickerSizeAmount = 0.1F;
	public static final float skyShipFlickerSpeed = 0.001F;
	public static final int skyShipHaloAlpha = 60;
}
