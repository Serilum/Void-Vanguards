package com.natamus.voidvanguards.parts.voidborn.functions;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.collective.functions.TaskFunctions;
import com.natamus.collective.functions.TileEntityFunctions;
import com.natamus.collective.schematic.ParseSchematicFile;
import com.natamus.collective.schematic.ParsedSchematicObject;
import com.natamus.collective.services.Services;
import com.natamus.voidvanguards.data.Constants;
import com.natamus.voidvanguards.data.ServerSaveData;
import com.natamus.voidvanguards.parts.story.functions.StoryFunctions;
import com.natamus.voidvanguards.parts.vanguard.functions.VanguardFunctions;
import com.natamus.voidvanguards.parts.voidborn.data.VoidbornConstants;
import com.natamus.voidvanguards.parts.voidborn.util.VoidbornUtil;
import com.natamus.voidvanguards.util.SaveLoadUtils;
import com.natamus.voidvanguards.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class VoidbornFunctions {
	public static void generateVoidbornCosmosGate(ServerLevel serverLevel, ServerPlayer serverPlayer, BlockPos middlePos) {
		MinecraftServer minecraftServer = serverLevel.getServer();
		ServerLevel overworldLevel = minecraftServer.overworld();

		InputStream schematicInputstream = Util.getSchematicsInputStream(minecraftServer, "cosmos_gate");
		if (schematicInputstream == null) {
			return;
		}

		ParsedSchematicObject parsedSchematicObject = ParseSchematicFile.getParsedSchematicObject(schematicInputstream, overworldLevel, middlePos, -1, false);

		if (!parsedSchematicObject.parsedCorrectly) {
			return;
		}

		TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
			for (Pair<BlockPos, BlockState> blockPair : parsedSchematicObject.blocks) {
				overworldLevel.setBlock(blockPair.getFirst(), blockPair.getSecond(), 3);
			}

			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				Registry<Enchantment> enchantmentRegistry = overworldLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT);

				for (BlockPos blockEntityPos : parsedSchematicObject.blockEntityPositions) {
					BlockEntity blockEntity = overworldLevel.getBlockEntity(blockEntityPos);
					if (blockEntity != null) {
						BlockState blockState = overworldLevel.getBlockState(blockEntityPos);
						Block block = blockState.getBlock();
						if (block.equals(Blocks.CRIMSON_SIGN)) {
							VoidbornUtil.replaceSignWithVoidbornMob(overworldLevel, blockEntityPos, enchantmentRegistry);
						}
						else if (block.equals(Blocks.CHEST)) {
							VoidbornUtil.fillChestWithItems(overworldLevel, blockEntity, blockEntityPos, blockState, enchantmentRegistry);
						}
					}
				}

				UUID playerUUID = serverPlayer.getUUID();

				ServerSaveData.get().voidbornGateGeneratedPlayerUUIDS.add(playerUUID);
				ServerSaveData.get().voidbornCosmosGatePositionPlayerUUIDS.put(playerUUID, Pair.of(overworldLevel.dimension(), middlePos));

				SaveLoadUtils.savePlayerData(serverPlayer);
			}, 0);
		}, 0);
	}

	public static void prepareCosmosVoidbornBase(ServerLevel cosmosLevel, ServerPlayer serverPlayer, BlockPos voidbornCosmosBasePos, BlockPos spawnPos) {

		MinecraftServer minecraftServer = cosmosLevel.getServer();

		InputStream schematicInputstream = Util.getSchematicsInputStream(minecraftServer, "enemy_base_orbit");
		if (schematicInputstream == null) {
			return;
		}

		ParsedSchematicObject parsedSchematicObject = ParseSchematicFile.getParsedSchematicObject(schematicInputstream, cosmosLevel, voidbornCosmosBasePos, -54, false);

		if (!parsedSchematicObject.parsedCorrectly) {
			return;
		}

		TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
			for (Pair<BlockPos, BlockState> blockPair : parsedSchematicObject.blocks) {
				cosmosLevel.setBlock(blockPair.getFirst(), blockPair.getSecond(), 3);
			}

			generateRadarStructure(cosmosLevel, serverPlayer, voidbornCosmosBasePos);

			BlockPos teleportBackButtonPos = VoidbornUtil.getTeleportBackButtonPosition(serverPlayer);
			cosmosLevel.setBlock(teleportBackButtonPos, Blocks.WARPED_BUTTON.defaultBlockState().setValue(ButtonBlock.FACING, Direction.WEST), 3);

			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				Registry<Enchantment> enchantmentRegistry = cosmosLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
				for (BlockPos entityBlockPos : parsedSchematicObject.blockEntityPositions) {
					if (cosmosLevel.getBlockState(entityBlockPos).getBlock().equals(Blocks.CRIMSON_SIGN)) {
						VoidbornUtil.replaceSignWithVoidbornMob(cosmosLevel, entityBlockPos, enchantmentRegistry);
					}
				}

				ServerSaveData.get().cosmosVoidbornBaseGeneratedPlayerUUIDS.add(serverPlayer.getUUID());
				SaveLoadUtils.savePlayerData(serverPlayer);

				Services.TELEPORT.teleportEntity(serverPlayer, cosmosLevel, spawnPos);

				Util.removeTag(serverPlayer, Constants.triggeredEntityInsideTag);

				TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
					for (ItemEntity itemEntity : cosmosLevel.getEntitiesOfClass(ItemEntity.class,new AABB(spawnPos).inflate(120),e -> true)) {
						itemEntity.discard();
					}
				}, 10);
			}, 0);
		}, 0);
	}

	public static void generateRadarStructure(ServerLevel serverLevel, ServerPlayer serverPlayer, BlockPos voidbornCosmosBasePos) {
		MinecraftServer minecraftServer = serverLevel.getServer();

		BlockPos voidbornBaseBlockPos = Util.getRANDOMVoidbornCosmosGateCoordinates(serverLevel, serverPlayer);


		BlockPos radarPos = voidbornCosmosBasePos.offset(-26, 7, -37);

		InputStream schematicInputstream = Util.getSchematicsInputStream(minecraftServer, "radar");
		if (schematicInputstream == null) {
			return;
		}

		ParsedSchematicObject parsedSchematicObject = ParseSchematicFile.getParsedSchematicObject(schematicInputstream, serverLevel, radarPos, 0, true);

		if (!parsedSchematicObject.parsedCorrectly) {
			return;
		}

		TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
			for (Pair<BlockPos, BlockState> blockPair : parsedSchematicObject.blocks) {
				serverLevel.setBlock(blockPair.getFirst(), blockPair.getSecond(), 3);
			}

			BlockPos signPos = radarPos.offset(-2, 2, 0);

			serverLevel.setBlockAndUpdate(signPos, Blocks.WARPED_WALL_SIGN.defaultBlockState().setValue(WallSignBlock.FACING, Direction.WEST));
			if (serverLevel.getBlockEntity(signPos) instanceof SignBlockEntity signBlockEntity) {
				SignText signText = signBlockEntity.getFrontText();
				signText = signText.setMessage(0, Component.translatable("voidvanguards.message.voidborn.base.radarSign.text.0").withStyle(ChatFormatting.WHITE));
				signText = signText.setMessage(1, Component.translatable("voidvanguards.message.voidborn.base.radarSign.text.1").withStyle(ChatFormatting.WHITE));
				signText = signText.setMessage(2, Component.translatable("voidvanguards.message.voidborn.base.radarSign.text.2").withStyle(ChatFormatting.WHITE));
				signText = signText.setMessage(3, Component.translatable("voidvanguards.message.voidborn.base.radarSign.text.3").withStyle(ChatFormatting.WHITE));

				signBlockEntity.setText(signText, true);

				TileEntityFunctions.updateTileEntity(serverLevel, signPos, signBlockEntity);
			}

			BlockPos leverPos = signPos.offset(0, -1, 0);
			serverLevel.setBlock(leverPos, Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACING, Direction.WEST), 3);
		}, 0);
	}

	private static final HashMap<UUID, Integer> skipCheck = new HashMap<>();
	public static void checkVoidbornLeftForStoryTrigger(ServerLevel cosmosLevel, ServerPlayer serverPlayer) {
		if (cosmosLevel.getDifficulty().equals(Difficulty.PEACEFUL)) {
			return;
		}

		if (!Util.isInCosmosDimension(serverPlayer)) {
			return;
		}

		UUID playerUUID = serverPlayer.getUUID();
		if (ServerSaveData.get().sabotagedVoidbornRadarPlayerUUIDS.contains(playerUUID)) {
			return;
		}

		if (ServerSaveData.get().triggeredVoidbornLeaderPlayerUUIDS.contains(playerUUID)) {
			return;
		}

		if (skipCheck.containsKey(playerUUID)) {
			int playerSkipCheckCount = skipCheck.get(playerUUID);
			if (playerSkipCheckCount > 0) {
				skipCheck.put(playerUUID, playerSkipCheckCount - 1);
				return;
			}
		}
		else {
			skipCheck.put(playerUUID, 5);
			return;
		}

		List<Mob> nearbyEntities = cosmosLevel.getEntitiesOfClass(
			Mob.class,
			serverPlayer.getBoundingBox().inflate(120.0D),
			e -> e.getTags().contains(VoidbornConstants.voidbornTag)
		);

		int voidbornCount = nearbyEntities.size();
		if (voidbornCount > VoidbornConstants.voidbornBaseMobCountStoryTrigger || voidbornCount < 5) {
			return;
		}

		BlockPos basePos = serverPlayer.blockPosition();
		Direction facing = serverPlayer.getDirection();

		BlockPos voidbornLeaderPos = null;

		for (int i = 1; i <= 3; i++) {
			BlockPos checkPos = basePos.relative(facing, i);

			boolean isClear =
				cosmosLevel.getBlockState(checkPos).isAir() &&
				cosmosLevel.getBlockState(checkPos.above()).isAir();

			if (isClear) {
				voidbornLeaderPos = checkPos;
				break;
			}
		}

		if (voidbornLeaderPos == null) {
			voidbornLeaderPos = basePos.relative(facing, 2);
		}

		Mob voidbornLeader = VoidbornUtil.spawnVoidbornLeader(cosmosLevel, voidbornLeaderPos);

		SaveLoadUtils.setForcedPositionTag(serverPlayer, 5, basePos);

		TaskFunctions.enqueueCollectiveServerTask(cosmosLevel.getServer(), () -> {
			StoryFunctions.processInitialVoidbornLeaderEncounter(cosmosLevel, serverPlayer, voidbornLeader);
		}, 10);
	}

	public static void voidbornRadarLeverWasTriggered(ServerLevel serverLevel, ServerPlayer serverPlayer) {
		UUID playerUUID = serverPlayer.getUUID();

		VoidbornUtil.spawnLightningAtRadarEndRods(serverPlayer);

		MessageFunctions.sendMessage(serverPlayer, Component.translatable("voidvanguards.message.voidborn.base.radarLever.flip").withStyle(ChatFormatting.RED));

		ServerSaveData.get().sabotagedVoidbornRadarPlayerUUIDS.add(playerUUID);
		SaveLoadUtils.savePlayerData(serverPlayer);

		TaskFunctions.enqueueCollectiveServerTask(serverLevel.getServer(), () -> {
			VanguardFunctions.processVanguardTreeTeleportVoidbase(serverLevel, serverPlayer);
		}, 100);
	}
}
