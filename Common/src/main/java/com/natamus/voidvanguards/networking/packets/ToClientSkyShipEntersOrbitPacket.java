package com.natamus.voidvanguards.networking.packets;

import com.natamus.collective.functions.MessageFunctions;
import com.natamus.collective.implementations.networking.data.PacketContext;
import com.natamus.collective.implementations.networking.data.Side;
import com.natamus.voidvanguards.config.ConfigHandler;
import com.natamus.voidvanguards.data.ClientConstants;
import com.natamus.voidvanguards.parts.skyship.data.SkyShipVariables;
import com.natamus.voidvanguards.util.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ToClientSkyShipEntersOrbitPacket {
	public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "to_client_sky_ship_enters_orbit_packet");

	public ToClientSkyShipEntersOrbitPacket() {
	}

	public static ToClientSkyShipEntersOrbitPacket decode(FriendlyByteBuf buf) {
		return new ToClientSkyShipEntersOrbitPacket();
	}

	public void encode(FriendlyByteBuf buf) {
	}

	public static void handle(PacketContext<ToClientSkyShipEntersOrbitPacket> ctx) {
		if (ctx.side().equals(Side.CLIENT)) {
			ToClientSkyShipEntersOrbitPacket packet = ctx.message();

			Player player = ClientConstants.mc.player;
			if (player == null) {
				return;
			}

			if (SkyShipVariables.triggeredSkyShipEvent || SkyShipVariables.skyShipVisible) {
				return;
			}

			SkyShipVariables.skyShipVisible = true;
			SkyShipVariables.ticksLeftForMoreDetail = ConfigHandler.ticksNeededForMoreSkyShipDetail;

			MessageFunctions.sendMessage(player, Component.translatable("collective.voidvanguards.message.skyship.entered_orbit.1").withStyle(ChatFormatting.DARK_PURPLE), true);
			MessageFunctions.sendMessage(player, Component.translatable("collective.voidvanguards.message.skyship.entered_orbit.2").withStyle(ChatFormatting.DARK_GRAY));
		}
	}
}