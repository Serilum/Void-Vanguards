package com.serilum.voidvanguards.networking.packets;

import com.natamus.collective.implementations.networking.data.PacketContext;
import com.natamus.collective.implementations.networking.data.Side;
import com.serilum.voidvanguards.data.ServerSaveData;
import com.serilum.voidvanguards.parts.vanguard.functions.VanguardFunctions;
import com.serilum.voidvanguards.util.Reference;
import com.serilum.voidvanguards.util.SaveLoadUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class ToServerTriggerSkyShipEventPacket {
	public static final ResourceLocation CHANNEL = new ResourceLocation(Reference.MOD_ID, "to_server_trigger_sky_ship_event_packet");

	public ToServerTriggerSkyShipEventPacket() {
	}

	public static ToServerTriggerSkyShipEventPacket decode(FriendlyByteBuf buf) {
		return new ToServerTriggerSkyShipEventPacket();
	}

	public void encode(FriendlyByteBuf buf) {
	}

	public static void handle(PacketContext<ToServerTriggerSkyShipEventPacket> ctx) {
		if (ctx.side().equals(Side.SERVER)) {
			ToServerTriggerSkyShipEventPacket packet = ctx.message();

			ServerPlayer serverPlayer = (ServerPlayer) ctx.sender();
			UUID playerUUID = serverPlayer.getUUID();

			if (!ServerSaveData.get().skyShipTriggeredPlayerUUIDS.contains(playerUUID)) {
				ServerSaveData.get().skyShipTriggeredPlayerUUIDS.add(playerUUID);

				SaveLoadUtils.savePlayerData(serverPlayer);
			}

			VanguardFunctions.initialPostSkyShipEventTrigger(serverPlayer.serverLevel(), serverPlayer);
		}
	}
}
