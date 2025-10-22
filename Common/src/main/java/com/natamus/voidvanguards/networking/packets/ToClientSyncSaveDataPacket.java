package com.natamus.voidvanguards.networking.packets;

import com.natamus.collective.implementations.networking.data.PacketContext;
import com.natamus.collective.implementations.networking.data.Side;
import com.natamus.voidvanguards.util.Reference;
import com.natamus.voidvanguards.util.SaveLoadUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ToClientSyncSaveDataPacket {
	public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "to_client_sync_save_data_packet");

	private final String saveDataTag;

	public ToClientSyncSaveDataPacket(String saveDataTagIn) {
		this.saveDataTag = saveDataTagIn;
	}

	public static ToClientSyncSaveDataPacket decode(FriendlyByteBuf buf) {
		String saveDataTagIn = buf.readUtf();

		return new ToClientSyncSaveDataPacket(saveDataTagIn);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(saveDataTag);
	}

	public static void handle(PacketContext<ToClientSyncSaveDataPacket> ctx) {
		if (ctx.side().equals(Side.CLIENT)) {
			ToClientSyncSaveDataPacket packet = ctx.message();

			SaveLoadUtils.clientSyncPlayerData(packet.saveDataTag);
		}
	}
}