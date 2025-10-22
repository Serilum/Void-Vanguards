package com.natamus.voidvanguards.parts.vanguard.util;

import com.natamus.collective.functions.EntityFunctions;
import com.natamus.collective.functions.TaskFunctions;
import com.natamus.voidvanguards.data.HeadData;
import com.natamus.voidvanguards.parts.vanguard.data.VanguardConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class VanguardUtil {
	public static List<LivingEntity> getVanguardsToSummon(Level level, int amount) {
		List<LivingEntity> vanguardsToSummon = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			Villager vanguardVillager = EntityType.VILLAGER.create(level);
			vanguardVillager.setUUID(UUID.randomUUID());

			ItemStack headStack;
			if (vanguardsToSummon.isEmpty()) {
				headStack = HeadData.getGeneratedHead(HeadData.HeadType.VANGUARD_LEADER);
				vanguardVillager.addTag(VanguardConstants.vanguardLeaderTag);
				vanguardVillager.setCustomName(Component.translatable("voidvanguards.vanguard.name.leader").withStyle(ChatFormatting.DARK_RED));

				vanguardVillager.setVillagerData(vanguardVillager.getVillagerData().setType(VillagerType.SAVANNA));
			} else {
				headStack = HeadData.getGeneratedHead(HeadData.HeadType.VANGUARD);
				vanguardVillager.addTag(VanguardConstants.vanguardRecruitTag);
				vanguardVillager.setCustomName(Component.translatable("voidvanguards.vanguard.name.recruit"));

				vanguardVillager.setVillagerData(vanguardVillager.getVillagerData().setType(VillagerType.SNOW));
			}
			vanguardVillager.setItemSlot(EquipmentSlot.HEAD, headStack);

			EntityFunctions.getGoalSelector(vanguardVillager).removeAllGoals(goal -> true);
			EntityFunctions.getTargetSelector(vanguardVillager).removeAllGoals(goal -> true);

			vanguardVillager.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0D);

			vanguardVillager.getTags().add(VanguardConstants.vanguardTag);

			vanguardVillager.setPersistenceRequired();
			vanguardVillager.setCanPickUpLoot(false);

			vanguardsToSummon.add(vanguardVillager);
		}

		return vanguardsToSummon;
	}

	public static List<BlockPos> getVanguardPositions(BlockPos middlePos, int amount) {
		List<BlockPos> positions;
		if (amount == 1) {
			positions = new ArrayList<>(Arrays.asList(middlePos.north(4)));
		}
		else if (amount == 4) {
			positions = new ArrayList<>(Arrays.asList(middlePos.north(4), middlePos.east(4), middlePos.south(4), middlePos.west(4)));
		}
		else if (amount == 8) {
			positions = positions = new ArrayList<>(Arrays.asList(middlePos.north(4), middlePos.north(3).east(3), middlePos.east(4), middlePos.south(3).east(3), middlePos.south(4), middlePos.south(3).west(3), middlePos.west(4), middlePos.north(3).west(3)));
		}
		else {
			return new ArrayList<>();
		}

		return positions;
	}

	public static void spawnGenerationParticles(ServerLevel serverLevel, BlockPos blockPos) {
		for (BlockPos aroundPos : BlockPos.betweenClosed(blockPos.offset(-1, -1, -1), blockPos.offset(1, 1, 1))) {
			spawnParticles(serverLevel, aroundPos, ParticleTypes.END_ROD, 5);
		}
	}
	public static void spawnTeleportParticles(ServerLevel serverLevel, BlockPos blockPos) {
		spawnParticles(serverLevel, blockPos, ParticleTypes.DRAGON_BREATH, 15);
	}
	public static void spawnParticles(ServerLevel serverLevel, BlockPos blockPos, SimpleParticleType simpleParticleType, int amount) {
		double x = blockPos.getX() + 0.5;
		double y = blockPos.getY() + 0.5;
		double z = blockPos.getZ() + 0.5;

		for (int j = 0; j < amount; j++) {
			double offsetX = (serverLevel.random.nextDouble() - 0.5) * 2.0;
			double offsetY = serverLevel.random.nextDouble() * 1.5;
			double offsetZ = (serverLevel.random.nextDouble() - 0.5) * 2.0;
			serverLevel.sendParticles(simpleParticleType, x + offsetX, y + offsetY, z + offsetZ, 1, 0, 0, 0, 0);
		}
	}

	public static List<BlockPos> getTorchLocations(BlockPos middlePos) {
		return new ArrayList<>(Arrays.asList(middlePos.north(4).east(2), middlePos.north(4).west(2), middlePos.east(4).north(2), middlePos.east(4).south(2), middlePos.south(4).east(2), middlePos.south(4).west(2), middlePos.west(4).north(2), middlePos.west(4).south(2)));
	}

	public static void removeGrassBetweenVanguards(Level level, List<BlockPos> vanguardPositions) {
		if (vanguardPositions.isEmpty()) {
			return;
		}

		int minX = vanguardPositions.stream().mapToInt(BlockPos::getX).min().orElse(0);
		int maxX = vanguardPositions.stream().mapToInt(BlockPos::getX).max().orElse(0);
		int minZ = vanguardPositions.stream().mapToInt(BlockPos::getZ).min().orElse(0);
		int maxZ = vanguardPositions.stream().mapToInt(BlockPos::getZ).max().orElse(0);
		int y = vanguardPositions.get(0).getY();

		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				BlockPos pos = new BlockPos(x, y, z);
				BlockState state = level.getBlockState(pos);

				if (state.is(Blocks.SHORT_GRASS) || state.is(Blocks.TALL_GRASS)) {
					level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				}
			}
		}
	}

	public static void keepPlayersWithinVanguardCircle(ServerPlayer serverPlayer, String forcedTag) {
		String rawPositionPart = forcedTag.substring(
			forcedTag.indexOf("forcedPlayerPosition..") + "forcedPlayerPosition..".length(),
			forcedTag.indexOf("--radius..")
		);
		String rawRadiusPart = forcedTag.substring(forcedTag.indexOf("--radius..") + "--radius..".length());

		String[] rawCoordinates = rawPositionPart.split("_");

		int x, y, z, radius;
		try {
			x = Integer.parseInt(rawCoordinates[0]);
			y = Integer.parseInt(rawCoordinates[1]);
			z = Integer.parseInt(rawCoordinates[2]);
			radius = Integer.parseInt(rawRadiusPart);
		} catch (NumberFormatException ex) {
			return;
		}

		BlockPos centerPos = new BlockPos(x, y, z);

		double dx = serverPlayer.getX() - centerPos.getX();
		double dz = serverPlayer.getZ() - centerPos.getZ();
		double distanceSquared = dx * dx + dz * dz;

		if (distanceSquared > (radius * radius)) {
			serverPlayer.teleportTo(centerPos.getX() + 0.5, serverPlayer.getY(), centerPos.getZ() + 0.5);
			spawnTeleportParticles(serverPlayer.serverLevel(), centerPos);
		}
	}


	public static void oblibirateTreeAbovePosition(ServerLevel serverLevel, BlockPos blockPos, int height, int radius) {
		MinecraftServer minecraftServer = serverLevel.getServer();

		for (int i = 0; i < height; i++) {
			int finalI = i;
			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				for (BlockPos aroundPos : BlockPos.betweenClosed(blockPos.offset(-radius, finalI, -radius), blockPos.offset(radius, finalI, radius))) {
					BlockState aroundBlockState = serverLevel.getBlockState(aroundPos);
					Block aroundBlock = aroundBlockState.getBlock();

					if (aroundBlock instanceof RotatedPillarBlock || aroundBlock instanceof LeavesBlock) {
						serverLevel.setBlock(aroundPos, Blocks.AIR.defaultBlockState(), 3);

						VanguardUtil.spawnTeleportParticles(serverLevel, aroundPos);
					}
				}
			}, finalI * 10);
		}
	}
}
