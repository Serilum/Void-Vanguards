package com.natamus.voidvanguards.events;

import com.natamus.voidvanguards.data.ClientConstants;
import com.natamus.voidvanguards.data.ClientVariables;
import com.natamus.voidvanguards.parts.skyship.data.SkyShipVariables;
import com.natamus.voidvanguards.parts.skyship.functions.SkyShipFunctions;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

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
					ClientConstants.mc.player.displayClientMessage(
							Component.translatable("voidvanguards.message.skyship.lost_focus").withStyle(ChatFormatting.RED), true
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


	public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
		if (ClientConstants.mc.level == null) {
			return;
		}

		float partialTick = deltaTracker.getGameTimeDeltaPartialTick(false);
		float targetAlpha = 0.0F;

		if (ClientVariables.flickerTicksLeft > 0) {
			targetAlpha = ClientVariables.flickerVisible ? 1.0F : 0.0F;
		} else if (ClientVariables.lastFlickerAlpha > 0.01F) {
			targetAlpha = 0.0F;
		}

		ClientVariables.lastFlickerAlpha = Mth.lerp(0.25F * partialTick, ClientVariables.lastFlickerAlpha, targetAlpha);
	}

	public static void onClientLogout() {
		SkyShipFunctions.resetSkyShipVariables(false);
	}
}
