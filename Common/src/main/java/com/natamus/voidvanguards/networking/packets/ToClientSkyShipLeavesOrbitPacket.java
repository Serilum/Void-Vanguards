package com.natamus.voidvanguards.networking.packets;

import com.natamus.collective.functions.MessageFunctions;
import com.natamus.collective.implementations.networking.data.PacketContext;
import com.natamus.collective.implementations.networking.data.Side;
import com.natamus.voidvanguards.data.ClientConstants;
import com.natamus.voidvanguards.parts.skyship.data.SkyShipVariables;
import com.natamus.voidvanguards.parts.skyship.functions.SkyShipFunctions;
import com.natamus.voidvanguards.util.Reference;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class ToClientSkyShipLeavesOrbitPacket {
	public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "to_client_sky_ship_leaves_orbit_packet");

	public ToClientSkyShipLeavesOrbitPacket() {
	}

	public static ToClientSkyShipLeavesOrbitPacket decode(FriendlyByteBuf buf) {
		return new ToClientSkyShipLeavesOrbitPacket();
	}

	public void encode(FriendlyByteBuf buf) {
	}

	public static void handle(PacketContext<ToClientSkyShipLeavesOrbitPacket> ctx) {
		if (ctx.side().equals(Side.CLIENT)) {
			ToClientSkyShipLeavesOrbitPacket packet = ctx.message();

			if (SkyShipVariables.triggeredSkyShipEvent || !SkyShipVariables.skyShipVisible) {
				return;
			}

			SkyShipFunctions.resetSkyShipVariables(false);

			Player player = ClientConstants.mc.player;
			if (player == null) {
				return;
			}

			MessageFunctions.sendMessage(player, Component.translatable("voidvanguards.message.skyship.left_orbit").withStyle(ChatFormatting.DARK_GREEN), true);
		}
	}
}