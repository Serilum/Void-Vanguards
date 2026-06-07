package com.natamus.voidvanguards.parts.skyship.functions;

import com.natamus.collective.implementations.networking.api.Dispatcher;
import com.natamus.voidvanguards.config.ConfigHandler;
import com.natamus.voidvanguards.data.ClientConstants;
import com.natamus.voidvanguards.networking.packets.ToServerTriggerSkyShipEventPacket;
import com.natamus.voidvanguards.parts.skyship.data.SkyShipPatterns;
import com.natamus.voidvanguards.parts.skyship.data.SkyShipVariables;
import com.natamus.voidvanguards.parts.skyship.util.SkyShipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SkyShipFunctions {

	public static boolean isLookingAtSkyShip() {
		Player player = ClientConstants.mc.player;
		if (!player.isUsingItem() || player.getUseItem().getItem() != Items.SPYGLASS) {
			return false;
		}

		long time = ClientConstants.mc.level.getGameTime();
		float partialTicks = ClientConstants.mc.getFrameTimeNs();

		float orbitAngle = time * SkyShipVariables.skyShipOrbitSpeed;
		float sunAngle = SkyShipUtil.getShipSunAngle(ClientConstants.mc, partialTicks);

		Vector3f sunPos = new Vector3f(
				(float) Math.sin(sunAngle),
				(float) Math.cos(sunAngle),
				0.0F
		).normalize();

		// Calculate ship position (same as rendering)
		Vector3f offset = new Vector3f(
				(float) Math.cos(orbitAngle) * SkyShipVariables.skyShipOrbitRadius,
				(float) Math.sin(orbitAngle) * SkyShipVariables.skyShipOrbitRadius,
				90.0F
		);

		Quaternionf rotToSun = new Quaternionf().rotateTo(new Vector3f(0, 1, 0), sunPos);
		offset.rotate(rotToSun);

		Vector3f shipPos = new Vector3f(sunPos).mul(100.0F).add(offset);

		shipPos.y += 0.1F * (float) Math.sin(time * 0.08);

		Vector3f shipDirection = new Vector3f(shipPos).normalize();

		return isShipVisible(shipDirection, player, partialTicks);
	}

	private static boolean isShipVisible(Vector3f shipDirection, Player player, float partialTicks) {
		Vec3 lookDir = player.getViewVector(partialTicks);
		Vector3f playerLookDir = new Vector3f((float) lookDir.x, (float) lookDir.y, (float) lookDir.z);

		float dot = shipDirection.dot(playerLookDir);
		float angle = (float) Math.acos(dot);
		float angleDegrees = (float) Math.toDegrees(angle);

		float maxAngleDegrees = 2.0f;

		return angleDegrees <= maxAngleDegrees;
	}

	public static void onLookingAtSkyShip() {
		Player player = ClientConstants.mc.player;
		if (player == null) {
			return;
		}

		if (SkyShipVariables.triggeredSkyShipEvent) {
			return;
		}

		SkyShipVariables.focusedOnSkyShip = true;

		SkyShipVariables.ticksLeftForMoreDetail -= 1;
		if (SkyShipVariables.ticksLeftForMoreDetail == 0) {
			if (SkyShipVariables.skyShipDetailLevel == SkyShipPatterns.SHIP_PATTERNS.length - 1) {
				triggerSkyShipEvent(player);
				return;
			}

			SkyShipVariables.skyShipDetailLevel += 1;
			SkyShipVariables.ticksLeftForMoreDetail = ConfigHandler.ticksNeededForMoreSkyShipDetail;

			player.displayClientMessage(Component.translatable("collective.voidvanguards.message.skyship.more_detailed").withStyle(ChatFormatting.GREEN), true);
			SkyShipVariables.sentFocusMessage = true;
		}
	}

	public static void triggerSkyShipEvent(Player player) {
		if (SkyShipVariables.triggeredSkyShipEvent) {
			return;
		}

		SkyShipVariables.triggeredSkyShipEvent = true;

		player.displayClientMessage(Component.translatable("collective.voidvanguards.message.skyship.explosion").withStyle(ChatFormatting.BLUE), true);

		Dispatcher.sendToServer(new ToServerTriggerSkyShipEventPacket());
	}

	public static void resetSkyShipVariables(boolean shipVisible) {
		SkyShipVariables.skyShipVisible = shipVisible;

		SkyShipVariables.triggeredSkyShipEvent = false;
		SkyShipVariables.focusedOnSkyShip = false;
		SkyShipVariables.sentFocusMessage = false;

		SkyShipVariables.ticksLeftForMoreDetail = ConfigHandler.ticksNeededForMoreSkyShipDetail;
		SkyShipVariables.skyShipDetailLevel = 0;
	}
}