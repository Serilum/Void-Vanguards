package com.serilum.voidvanguards.networking.packets;

import com.natamus.collective.implementations.networking.data.PacketContext;
import com.natamus.collective.implementations.networking.data.Side;
import com.serilum.voidvanguards.events.VoidClientEvents;
import com.serilum.voidvanguards.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;

public class ToClientSkyShipLeavesOrbitPacket {
	public static final Identifier CHANNEL = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "to_client_sky_ship_leaves_orbit_packet");

	public ToClientSkyShipLeavesOrbitPacket() {
	}

	public static ToClientSkyShipLeavesOrbitPacket decode(FriendlyByteBuf buf) {
		return new ToClientSkyShipLeavesOrbitPacket();
	}

	public void encode(FriendlyByteBuf buf) {
	}

	public static void handle(PacketContext<ToClientSkyShipLeavesOrbitPacket> ctx) {
		if (ctx.side().equals(Side.CLIENT)) {
			VoidClientEvents.onSkyShipLeavesOrbit();
		}
	}
}