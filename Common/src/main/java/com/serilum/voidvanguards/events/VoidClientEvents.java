package com.serilum.voidvanguards.events;

import com.natamus.collective.functions.MessageFunctions;
import com.serilum.voidvanguards.config.ConfigHandler;
import com.serilum.voidvanguards.data.ClientConstants;
import com.serilum.voidvanguards.data.ClientVariables;
import com.serilum.voidvanguards.parts.skyship.data.SkyShipVariables;
import com.serilum.voidvanguards.parts.skyship.functions.SkyShipFunctions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class VoidClientEvents {
	public static void onClientTick() {
		if (ClientConstants.mc.player == null || ClientConstants.mc.level == null) {
			return;
		}

		if (SkyShipVariables.skyShipVisible) {
			if (SkyShipFunctions.isLookingAtSkyShip()) {
				SkyShipFunctions.onLookingAtSkyShip();
			} else if (!SkyShipVariables.triggeredSkyShipEvent && SkyShipVariables.focusedOnSkyShip) {
				if (SkyShipVariables.sentFocusMessage) {
					ClientConstants.mc.player.sendOverlayMessage(
							Component.translatable("collective.voidvanguards.message.skyship.lost_focus").withStyle(ChatFormatting.RED)
					);
				}
				SkyShipFunctions.resetSkyShipVariables(true);
			}
		}

		if (ClientVariables.flickerTicksLeft > 0) {
			ClientVariables.flickerTicksLeft--;
			ClientVariables.ticksLeftUntilFlickerChange--;

			if (ClientVariables.ticksLeftUntilFlickerChange <= 0) {
				ClientVariables.ticksLeftUntilFlickerChange = 20;
				ClientVariables.flickerVisible = !ClientVariables.flickerVisible;
			}
		} else if (ClientVariables.lastFlickerAlpha < 0.01F) {
			ClientVariables.lastFlickerAlpha = 0.0F;
			ClientVariables.flickerVisible = false;
		}
	}


	public static void renderOverlay(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
		if (ClientConstants.mc.level == null) {
			return;
		}

		float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
		float targetAlpha = 0.0F;

		if (ClientVariables.flickerTicksLeft > 0) {
			if (ClientVariables.flickerVisible) {
				targetAlpha = 1.0F;
			}
		} else if (ClientVariables.lastFlickerAlpha > 0.01F) {
			targetAlpha = 0.0F;
		}

		ClientVariables.lastFlickerAlpha = Mth.lerp(0.25F * partialTick, ClientVariables.lastFlickerAlpha, targetAlpha);
	}

	public static void onClientLogout() {
		SkyShipFunctions.resetSkyShipVariables(false);
	}

	public static void onSkyShipEntersOrbit() {
		Player player = ClientConstants.mc.player;
		if (player == null) {
			return;
		}

		if (SkyShipVariables.triggeredSkyShipEvent || SkyShipVariables.skyShipVisible) {
			return;
		}

		SkyShipVariables.skyShipVisible = true;
		SkyShipVariables.ticksLeftForMoreDetail = ConfigHandler.ticksNeededForMoreSkyShipDetail;

		MessageFunctions.sendClientMessage(player, Component.literal(""));
		MessageFunctions.sendClientMessage(player, Component.translatable("collective.voidvanguards.message.skyship.entered_orbit.1").withStyle(ChatFormatting.DARK_PURPLE));
		MessageFunctions.sendClientMessage(player, Component.translatable("collective.voidvanguards.message.skyship.entered_orbit.2").withStyle(ChatFormatting.DARK_GRAY));
	}

	public static void onSkyShipLeavesOrbit() {
		if (SkyShipVariables.triggeredSkyShipEvent || !SkyShipVariables.skyShipVisible) {
			return;
		}

		SkyShipFunctions.resetSkyShipVariables(false);

		Player player = ClientConstants.mc.player;
		if (player == null) {
			return;
		}

		MessageFunctions.sendClientMessage(player, Component.literal(""));
		MessageFunctions.sendClientMessage(player, Component.translatable("collective.voidvanguards.message.skyship.left_orbit").withStyle(ChatFormatting.DARK_GREEN));
	}

	public static void onResetSkyShip() {
		SkyShipFunctions.resetSkyShipVariables(true);

		Player player = ClientConstants.mc.player;
		if (player == null) {
			return;
		}

		MessageFunctions.sendClientMessage(player, Component.translatable("collective.voidvanguards.message.skyship.reset").withStyle(ChatFormatting.DARK_GREEN));
	}
}
