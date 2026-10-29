package com.serilum.voidvanguards.networking.packets;

import com.natamus.collective.implementations.networking.data.PacketContext;
import com.natamus.collective.implementations.networking.data.Side;
import com.serilum.voidvanguards.events.VoidClientEvents;
import com.serilum.voidvanguards.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ToClientResetSkyShipPacket {
	public static final ResourceLocation CHANNEL = new ResourceLocation(Reference.MOD_ID, "to_client_reset_sky_ship_packet");

	public ToClientResetSkyShipPacket() {
	}

	public static ToClientResetSkyShipPacket decode(FriendlyByteBuf buf) {
		return new ToClientResetSkyShipPacket();
	}

	public void encode(FriendlyByteBuf buf) {
	}

	public static void handle(PacketContext<ToClientResetSkyShipPacket> ctx) {
		if (ctx.side().equals(Side.CLIENT)) {
			VoidClientEvents.onResetSkyShip();
		}
	}
}
