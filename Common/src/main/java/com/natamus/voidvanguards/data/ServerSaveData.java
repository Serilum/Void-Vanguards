package com.natamus.voidvanguards.data;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Consumer;

public class ServerSaveData {
	private static Data data = new Data();

	public static class Data {
		private final List<Consumer<UUID>> uuidRemoveConsumers = new ArrayList<>();

		public final List<UUID> skyShipTriggeredPlayerUUIDS = register(new ArrayList<>());
		public final List<UUID> vanguardStationGeneratedPlayerUUIDS = register(new ArrayList<>());
		public final List<UUID> answeredRadioPlayerUUIDS = register(new ArrayList<>());
		public final List<UUID> voidbornGateGeneratedPlayerUUIDS = register(new ArrayList<>());
		public final List<UUID> cosmosVoidbornBaseGeneratedPlayerUUIDS = register(new ArrayList<>());
		public final List<UUID> triggeredVoidbornBaseStoryPlayerUUIDS = register(new ArrayList<>());
		public final List<UUID> triggeredVoidbornLeaderPlayerUUIDS = register(new ArrayList<>());
		public final List<UUID> gaveVoidbornLeaderRadioPlayerUUIDS = register(new ArrayList<>());
		public final List<UUID> sabotagedVoidbornRadarPlayerUUIDS = register(new ArrayList<>());
		public final List<UUID> voidVanguardsCompletedPlayerUUIDS = register(new ArrayList<>());

		public final HashMap<UUID, Pair<ResourceKey<Level>, BlockPos>> originalPlayerPositions = register(new HashMap<>());
		public final HashMap<UUID, Pair<ResourceKey<Level>, BlockPos>> voidbornCosmosGatePositionPlayerUUIDS = register(new HashMap<>());

		private <T> T register(T field) {
			if (field instanceof Collection<?> collection) {
				uuidRemoveConsumers.add(uuid -> collection.remove(uuid));
			} else if (field instanceof Map<?, ?> map) {
				uuidRemoveConsumers.add(map::remove);
			}
			return field;
		}

		void removeUUID(UUID uuid) {
			for (Consumer<UUID> remover : uuidRemoveConsumers) {
				remover.accept(uuid);
			}
		}
	}

	public static Data get() {
		return data;
	}

	public static void reset() {
		data = new Data();
	}

	public static void removeUUID(UUID uuid) {
		data.removeUUID(uuid);
	}
}
