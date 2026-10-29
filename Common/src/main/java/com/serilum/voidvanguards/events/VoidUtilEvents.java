package com.serilum.voidvanguards.events;

import com.serilum.voidvanguards.data.ServerSaveData;
import com.serilum.voidvanguards.util.SaveLoadUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

public class VoidUtilEvents {
	public static void onServerShuttingdown(MinecraftServer server) {
		for (Player player : server.getPlayerList().getPlayers()) {
			SaveLoadUtils.savePlayerData(player);
		}

		ServerSaveData.reset();
	}
}
