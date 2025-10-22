package com.natamus.voidvanguards.util;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.implementations.networking.api.Dispatcher;
import com.natamus.voidvanguards.data.ClientSaveData;
import com.natamus.voidvanguards.data.Constants;
import com.natamus.voidvanguards.data.ServerSaveData;
import com.natamus.voidvanguards.networking.packets.ToClientSyncSaveDataPacket;
import com.natamus.voidvanguards.parts.skyship.data.SkyShipVariables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class SaveLoadUtils {
	public static String savePlayerData(Player player) {
		String newTag = getPlayerDataTag(player);

		removeOldSaveTag(player);

		player.addTag(newTag);

		return newTag;
	}

	public static void loadPlayerData(Player player) {
		if (player.level().isClientSide()) {
			return;
		}

		String saveDataTag = "";

		Set<String> tags = player.getTags();
		for (String tag : tags) {
			if (tag.startsWith(Constants.saveDataTagPrefix)) {
				saveDataTag = tag;
				break;
			}
		}

		if (saveDataTag.isEmpty()) {
			saveDataTag = savePlayerData(player);
		}
		else if (!saveDataTagUUIDMatches(player, saveDataTag)) {
			ServerSaveData.removeUUID(player.getUUID());
			saveDataTag = savePlayerData(player);
		}

		ServerPlayer serverPlayer = (ServerPlayer) player;

		serverSyncPlayerData(serverPlayer, saveDataTag);
		Dispatcher.sendToClient(new ToClientSyncSaveDataPacket(saveDataTag), serverPlayer);
	}

	@SuppressWarnings("RedundantCollectionOperation")
	public static void serverSyncPlayerData(ServerPlayer serverPlayer, String saveDataTag) {
		UUID playerUUID = serverPlayer.getUUID();

		ServerLevel serverLevel = serverPlayer.serverLevel();
		Registry<Level> levelRegistry = serverLevel.registryAccess().registryOrThrow(Registries.DIMENSION);

		int x, y, z;
		for (String saveData : saveDataTag.split(Constants.saveDataTagMainDelimiter)) {
			if (!saveData.contains(Constants.saveDataTagSubDelimiter)) {
				continue;
			}

			String[] keyValue = saveData.split(Pattern.quote(Constants.saveDataTagSubDelimiter));

			switch (keyValue[0]) {
				case "skyShipTriggered":
					if (keyValue[1].equals("true")) {
						if (!ServerSaveData.get().skyShipTriggeredPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().skyShipTriggeredPlayerUUIDS.add(playerUUID);
						}
					} else {
						if (ServerSaveData.get().skyShipTriggeredPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().skyShipTriggeredPlayerUUIDS.remove(playerUUID);
						}
					}
					break;
				case "vanguardStationGenerated":
					if (keyValue[1].equals("true")) {
						if (!ServerSaveData.get().vanguardStationGeneratedPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().vanguardStationGeneratedPlayerUUIDS.add(playerUUID);
						}
					} else {
						if (ServerSaveData.get().vanguardStationGeneratedPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().vanguardStationGeneratedPlayerUUIDS.remove(playerUUID);
						}
					}
					break;
				case "answeredRadio":
					if (keyValue[1].equals("true")) {
						if (!ServerSaveData.get().answeredRadioPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().answeredRadioPlayerUUIDS.add(playerUUID);
						}
					} else {
						if (ServerSaveData.get().answeredRadioPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().answeredRadioPlayerUUIDS.remove(playerUUID);
						}
					}
					break;
				case "voidbornGateGenerated":
					if (keyValue[1].equals("true")) {
						if (!ServerSaveData.get().voidbornGateGeneratedPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().voidbornGateGeneratedPlayerUUIDS.add(playerUUID);
						}
					} else {
						if (ServerSaveData.get().voidbornGateGeneratedPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().voidbornGateGeneratedPlayerUUIDS.remove(playerUUID);
						}
					}
					break;
				case "cosmosVoidbornBaseGenerated":
					if (keyValue[1].equals("true")) {
						if (!ServerSaveData.get().cosmosVoidbornBaseGeneratedPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().cosmosVoidbornBaseGeneratedPlayerUUIDS.add(playerUUID);
						}
					} else {
						if (ServerSaveData.get().cosmosVoidbornBaseGeneratedPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().cosmosVoidbornBaseGeneratedPlayerUUIDS.remove(playerUUID);
						}
					}
					break;
				case "triggeredVoidbornBaseStory":
					if (keyValue[1].equals("true")) {
						if (!ServerSaveData.get().triggeredVoidbornBaseStoryPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().triggeredVoidbornBaseStoryPlayerUUIDS.add(playerUUID);
						}
					} else {
						if (ServerSaveData.get().triggeredVoidbornBaseStoryPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().triggeredVoidbornBaseStoryPlayerUUIDS.remove(playerUUID);
						}
					}
					break;
				case "triggeredVoidbornLeader":
					if (keyValue[1].equals("true")) {
						if (!ServerSaveData.get().triggeredVoidbornLeaderPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().triggeredVoidbornLeaderPlayerUUIDS.add(playerUUID);
						}
					} else {
						if (ServerSaveData.get().triggeredVoidbornLeaderPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().triggeredVoidbornLeaderPlayerUUIDS.remove(playerUUID);
						}
					}
					break;
				case "gaveVoidbornLeaderRadio":
					if (keyValue[1].equals("true")) {
						if (!ServerSaveData.get().gaveVoidbornLeaderRadioPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().gaveVoidbornLeaderRadioPlayerUUIDS.add(playerUUID);
						}
					} else {
						if (ServerSaveData.get().gaveVoidbornLeaderRadioPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().gaveVoidbornLeaderRadioPlayerUUIDS.remove(playerUUID);
						}
					}
					break;
				case "sabotagedVoidbornRadar":
					if (keyValue[1].equals("true")) {
						if (!ServerSaveData.get().sabotagedVoidbornRadarPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().sabotagedVoidbornRadarPlayerUUIDS.add(playerUUID);
						}
					} else {
						if (ServerSaveData.get().sabotagedVoidbornRadarPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().sabotagedVoidbornRadarPlayerUUIDS.remove(playerUUID);
						}
					}
					break;
				case "voidVanguardsCompleted":
					if (keyValue[1].equals("true")) {
						if (!ServerSaveData.get().voidVanguardsCompletedPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().voidVanguardsCompletedPlayerUUIDS.add(playerUUID);
						}
					} else {
						if (ServerSaveData.get().voidVanguardsCompletedPlayerUUIDS.contains(playerUUID)) {
							ServerSaveData.get().voidVanguardsCompletedPlayerUUIDS.remove(playerUUID);
						}
					}
					break;
				case "originalPlayerPosition":
					String[] rawOriginalPosition = keyValue[1].split("__");
					String rawOriginalLevelResourceKey = rawOriginalPosition[0];
					String[] rawOriginalCoordinates = rawOriginalPosition[1].split("_");

					ResourceLocation originalLevelResourceLocation = ResourceLocation.parse(rawOriginalLevelResourceKey.replace("+", ":"));
					ResourceKey<Level> originalLevelResourceKey = ResourceKey.create(Registries.DIMENSION, originalLevelResourceLocation);

					Level originalLevel = serverLevel.getServer().getLevel(originalLevelResourceKey);
					if (originalLevel == null) {
						continue;
					}

					try {
						x = Integer.parseInt(rawOriginalCoordinates[0]);
						y = Integer.parseInt(rawOriginalCoordinates[1]);
						z = Integer.parseInt(rawOriginalCoordinates[2]);
					} catch (NumberFormatException ex) {
						System.out.println(" !! NumberFormatException for: " + rawOriginalPosition[1]);
						continue;
					}

					BlockPos originalPos = new BlockPos(x, y, z);

					ServerSaveData.get().originalPlayerPositions.put(playerUUID, Pair.of(originalLevel.dimension(), originalPos));
					break;
				case "enemyCosmosGatePosition":
					String[] rawVoidbornBasePosition = keyValue[1].split("__");
					String rawVoidbornBaseLevelResourceKey = rawVoidbornBasePosition[0];
					String[] rawVoidbornBaseCoordinates = rawVoidbornBasePosition[1].split("_");

					ResourceLocation voidbornBaseLevelResourceLocation = ResourceLocation.parse(rawVoidbornBaseLevelResourceKey.replace("+", ":"));
					ResourceKey<Level> voidbornBaseLevelResourceKey = ResourceKey.create(Registries.DIMENSION, voidbornBaseLevelResourceLocation);

					Level voidbornBaseLevel = serverLevel.getServer().getLevel(voidbornBaseLevelResourceKey);
					if (voidbornBaseLevel == null) {
						continue;
					}

					try {
						x = Integer.parseInt(rawVoidbornBaseCoordinates[0]);
						y = Integer.parseInt(rawVoidbornBaseCoordinates[1]);
						z = Integer.parseInt(rawVoidbornBaseCoordinates[2]);
					} catch (NumberFormatException ex) {
						continue;
					}

					BlockPos voidbornBasePos = new BlockPos(x, y, z);

					ServerSaveData.get().voidbornCosmosGatePositionPlayerUUIDS.put(playerUUID, Pair.of(voidbornBaseLevel.dimension(), voidbornBasePos));
					break;
				default:
					break;
			}
		}
	}

	public static void clientSyncPlayerData(String saveDataTag) {
		for (String saveData : saveDataTag.split(Constants.saveDataTagMainDelimiter)) {
			if (!saveData.contains(Constants.saveDataTagSubDelimiter)) {
				continue;
			}

			String[] keyValue = saveData.split(Pattern.quote(Constants.saveDataTagSubDelimiter));

			switch (keyValue[0]) {
				case "skyShipTriggered":
					if (keyValue[1].equals("true")) SkyShipVariables.triggeredSkyShipEvent = true;
					break;
				case "voidVanguardsCompleted":
					if (keyValue[1].equals("true")) ClientSaveData.voidVanguardsCompleted = true;
					break;
				default:
					break;
			}
		}
	}

	public static String getPlayerDataTag(Player player) {
		UUID playerUUID = player.getUUID();

		String dataTag = Constants.saveDataTagPrefix;

		dataTag += Constants.saveDataTagMainDelimiter + "playerUUID" + Constants.saveDataTagSubDelimiter + playerUUID;
		dataTag += Constants.saveDataTagMainDelimiter + "skyShipTriggered" + Constants.saveDataTagSubDelimiter + ServerSaveData.get().skyShipTriggeredPlayerUUIDS.contains(playerUUID);
		dataTag += Constants.saveDataTagMainDelimiter + "vanguardStationGenerated" + Constants.saveDataTagSubDelimiter + ServerSaveData.get().vanguardStationGeneratedPlayerUUIDS.contains(playerUUID);
		dataTag += Constants.saveDataTagMainDelimiter + "answeredRadio" + Constants.saveDataTagSubDelimiter + ServerSaveData.get().answeredRadioPlayerUUIDS.contains(playerUUID);
		dataTag += Constants.saveDataTagMainDelimiter + "voidbornGateGenerated" + Constants.saveDataTagSubDelimiter + ServerSaveData.get().voidbornGateGeneratedPlayerUUIDS.contains(playerUUID);
		dataTag += Constants.saveDataTagMainDelimiter + "cosmosVoidbornBaseGenerated" + Constants.saveDataTagSubDelimiter + ServerSaveData.get().cosmosVoidbornBaseGeneratedPlayerUUIDS.contains(playerUUID);
		dataTag += Constants.saveDataTagMainDelimiter + "triggeredVoidbornBaseStory" + Constants.saveDataTagSubDelimiter + ServerSaveData.get().triggeredVoidbornBaseStoryPlayerUUIDS.contains(playerUUID);
		dataTag += Constants.saveDataTagMainDelimiter + "triggeredVoidbornLeader" + Constants.saveDataTagSubDelimiter + ServerSaveData.get().triggeredVoidbornLeaderPlayerUUIDS.contains(playerUUID);
		dataTag += Constants.saveDataTagMainDelimiter + "gaveVoidbornLeaderRadio" + Constants.saveDataTagSubDelimiter + ServerSaveData.get().gaveVoidbornLeaderRadioPlayerUUIDS.contains(playerUUID);
		dataTag += Constants.saveDataTagMainDelimiter + "sabotagedVoidbornRadar" + Constants.saveDataTagSubDelimiter + ServerSaveData.get().sabotagedVoidbornRadarPlayerUUIDS.contains(playerUUID);
		dataTag += Constants.saveDataTagMainDelimiter + "voidVanguardsCompleted" + Constants.saveDataTagSubDelimiter + ServerSaveData.get().voidVanguardsCompletedPlayerUUIDS.contains(playerUUID);

		Pair<ResourceKey<Level>, BlockPos> originalPositionPair = ServerSaveData.get().originalPlayerPositions.get(playerUUID);
		if (originalPositionPair != null) {
			BlockPos originalPositionPos = originalPositionPair.getSecond();

			dataTag += Constants.saveDataTagMainDelimiter + "originalPlayerPosition" + Constants.saveDataTagSubDelimiter + originalPositionPair.getFirst().location().toString().replace(":", "+") + "__" + originalPositionPos.getX() + "_" + originalPositionPos.getY() + "_" + originalPositionPos.getZ();
		}

		Pair<ResourceKey<Level>, BlockPos> voidbornCosmosGateCoordinatePair = ServerSaveData.get().voidbornCosmosGatePositionPlayerUUIDS.get(playerUUID);
		if (voidbornCosmosGateCoordinatePair != null) {
			BlockPos voidbornCosmosGateCoordinatePos = voidbornCosmosGateCoordinatePair.getSecond();

			dataTag += Constants.saveDataTagMainDelimiter + "enemyCosmosGatePosition" + Constants.saveDataTagSubDelimiter + voidbornCosmosGateCoordinatePair.getFirst().location().toString().replace(":", "+") + "__" + voidbornCosmosGateCoordinatePos.getX() + "_" + voidbornCosmosGateCoordinatePos.getY() + "_" + voidbornCosmosGateCoordinatePos.getZ();
		}

		return dataTag;
	}

	public static void removeOldSaveTag(Player player) {
		List<String> tagsToRemove = new ArrayList<>();

		Set<String> tags = player.getTags();
		for (String tag : tags) {
			if (tag.startsWith(Constants.saveDataTagPrefix)) {
				tagsToRemove.add(tag);
			}
		}

		for (String tag : tagsToRemove) {
			player.getTags().remove(tag);
		}
	}

	private static boolean saveDataTagUUIDMatches(Player player, String saveDataTag) {
		for (String saveData : saveDataTag.split(Constants.saveDataTagMainDelimiter)) {
			if (!saveData.contains(Constants.saveDataTagSubDelimiter)) {
				continue;
			}

			String[] keyValue = saveData.split(Pattern.quote(Constants.saveDataTagSubDelimiter));

			if (keyValue[0].equals("playerUUID")) {
				return keyValue[1].equals(player.getUUID().toString());
			}
		}

		return false;
	}

	// Forced Position
	public static void setForcedPositionTag(ServerPlayer serverPlayer, int maxRadius) {
		setForcedPositionTag(serverPlayer, maxRadius, serverPlayer.blockPosition());
	}
	public static void setForcedPositionTag(ServerPlayer serverPlayer, int maxRadius, BlockPos middlePos) {
		String forcedPositionTag = Constants.forcedPositionTagPrefix + Constants.saveDataTagMainDelimiter + "forcedPlayerPosition" + Constants.saveDataTagSubDelimiter + middlePos.getX() + "_" + middlePos.getY() + "_" + middlePos.getZ() + Constants.saveDataTagMainDelimiter + "radius" + Constants.saveDataTagSubDelimiter + maxRadius;

		removeForcedPositionTag(serverPlayer);
		serverPlayer.getTags().add(forcedPositionTag);
	}
	public static void removeForcedPositionTag(ServerPlayer serverPlayer) {
		List<String> tagsToRemove = new ArrayList<>();

		Set<String> tags = serverPlayer.getTags();
		for (String tag : tags) {
			if (tag.startsWith(Constants.forcedPositionTagPrefix)) {
				tagsToRemove.add(tag);
			}
		}

		for (String tag : tagsToRemove) {
			serverPlayer.getTags().remove(tag);
		}
	}
}
