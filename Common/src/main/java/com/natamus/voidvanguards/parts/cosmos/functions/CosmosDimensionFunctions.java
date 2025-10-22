package com.natamus.voidvanguards.parts.cosmos.functions;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.collective.functions.TaskFunctions;
import com.natamus.collective.schematic.ParseSchematicFile;
import com.natamus.collective.schematic.ParsedSchematicObject;
import com.natamus.collective.services.Services;
import com.natamus.voidvanguards.data.Constants;
import com.natamus.voidvanguards.data.ServerSaveData;
import com.natamus.voidvanguards.parts.cosmos.util.CosmosDimensionUtil;
import com.natamus.voidvanguards.parts.vanguard.functions.VanguardFunctions;
import com.natamus.voidvanguards.parts.voidborn.functions.VoidbornFunctions;
import com.natamus.voidvanguards.parts.voidborn.util.VoidbornUtil;
import com.natamus.voidvanguards.registry.identifier.CosmosLocation;
import com.natamus.voidvanguards.util.SaveLoadUtils;
import com.natamus.voidvanguards.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

import java.io.InputStream;
import java.util.UUID;

public class CosmosDimensionFunctions {
	public static void teleportPlayerToVanguardStation(ServerLevel serverLevel, ServerPlayer serverPlayer, boolean fromSkyShipTrigger) {
		MinecraftServer minecraftServer = serverLevel.getServer();
		UUID playerUUID = serverPlayer.getUUID();

		ServerSaveData.get().originalPlayerPositions.put(playerUUID, Pair.of(serverLevel.dimension(), serverPlayer.blockPosition()));
		SaveLoadUtils.savePlayerData(serverPlayer);

		ServerLevel cosmosLevel = Util.getCosmosLevel(serverLevel.getServer());

		BlockPos vanguardStationPos = CosmosDimensionUtil.getUniqueBlockPositionFromUUID(serverPlayer.getUUID(), CosmosLocation.VANGUARD_STATION);

		BlockPos spawnPos;
		if (!ServerSaveData.get().vanguardStationGeneratedPlayerUUIDS.contains(playerUUID) || ServerSaveData.get().gaveVoidbornLeaderRadioPlayerUUIDS.contains(playerUUID)) {
			prepareCosmosSpawnArea(cosmosLevel, serverPlayer, vanguardStationPos);

			spawnPos = vanguardStationPos.offset(0, 51, -10);
		} else {
			spawnPos = vanguardStationPos;
		}

		TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
			Services.TELEPORT.teleportEntity(serverPlayer, cosmosLevel, spawnPos);

			if (fromSkyShipTrigger) {
				TaskFunctions.enqueueCollectiveServerTask(serverLevel.getServer(), () -> {
					VanguardFunctions.spawnInitialVanguardsAroundPlayer(cosmosLevel, serverPlayer, spawnPos);
				}, 50);
			}
			else {
				MessageFunctions.sendMessage(serverPlayer, Component.translatable("item.voidvanguards.orbital_transponder.useSuccess").withStyle(ChatFormatting.DARK_GREEN));
			}
		}, 10);
	}

	private static void prepareCosmosSpawnArea(ServerLevel cosmosLevel, ServerPlayer serverPlayer, BlockPos middlePos) {
		MinecraftServer minecraftServer = cosmosLevel.getServer();

		InputStream schematicInputstream = Util.getSchematicsInputStream(minecraftServer, "vanguard_station");
		if (schematicInputstream == null) {
			System.out.println("Error generating vanguard station: inputstream is null.");
			return;
		}

		ParsedSchematicObject parsedSchematicObject = ParseSchematicFile.getParsedSchematicObject(schematicInputstream, cosmosLevel, middlePos, -73, false);

		if (!parsedSchematicObject.parsedCorrectly) {
			System.out.println("Error generating vanguard station: schematic object didn't parse.");
			return;
		}

		TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
			for (Pair<BlockPos, BlockState> blockPair : parsedSchematicObject.blocks) {
				cosmosLevel.setBlock(blockPair.getFirst(), blockPair.getSecond(), 3);
			}

			ServerSaveData.get().vanguardStationGeneratedPlayerUUIDS.add(serverPlayer.getUUID());
			SaveLoadUtils.savePlayerData(serverPlayer);
		}, 0);
	}

	public static void teleportPlayerToVoidbornBase(ServerLevel serverLevel, ServerPlayer serverPlayer) {
		UUID playerUUID = serverPlayer.getUUID();

		VoidbornFunctions.skipCheck.put(playerUUID, 5);

		ServerLevel cosmosLevel = Util.getCosmosLevel(serverLevel.getServer());
		BlockPos voidbornCosmosBasePos = CosmosDimensionUtil.getUniqueBlockPositionFromUUID(serverPlayer.getUUID(), CosmosLocation.VOIDBORN_BASE);


		BlockPos spawnPos = VoidbornUtil.getVoidbornBaseSpawnPos(serverPlayer, voidbornCosmosBasePos);

		boolean firstWarp = !ServerSaveData.get().cosmosVoidbornBaseGeneratedPlayerUUIDS.contains(playerUUID);
		if (firstWarp) {
			VoidbornFunctions.prepareCosmosVoidbornBase(cosmosLevel, serverPlayer, voidbornCosmosBasePos, spawnPos);
		}

		if (firstWarp) {
			MessageFunctions.sendMessage(serverPlayer, Component.translatable("voidvanguards.message.voidborn.cosmos_gate.enterSuccess.first").withStyle(ChatFormatting.DARK_PURPLE), true);
		}
		else {
			MessageFunctions.sendMessage(serverPlayer, Component.translatable("voidvanguards.message.voidborn.cosmos_gate.enterSuccess.again").withStyle(ChatFormatting.DARK_PURPLE), true);
		}

		TaskFunctions.enqueueCollectiveServerTask(serverLevel.getServer(), () -> {
			if (!firstWarp) {
				Services.TELEPORT.teleportEntity(serverPlayer, cosmosLevel, spawnPos);

				Util.removeTag(serverPlayer, Constants.triggeredEntityInsideTag);
			}
		}, 10);
	}
}
