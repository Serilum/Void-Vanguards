package com.natamus.voidvanguards.parts.vanguard.functions;

import com.mojang.authlib.GameProfile;
import com.natamus.collective.functions.*;
import com.natamus.voidvanguards.data.HeadData;
import com.natamus.voidvanguards.data.ServerSaveData;
import com.natamus.voidvanguards.parts.cosmos.functions.CosmosDimensionFunctions;
import com.natamus.voidvanguards.parts.story.functions.StoryFunctions;
import com.natamus.voidvanguards.parts.vanguard.data.VanguardVariables;
import com.natamus.voidvanguards.parts.vanguard.util.VanguardUtil;
import com.natamus.voidvanguards.parts.voidborn.data.VoidbornConstants;
import com.natamus.voidvanguards.parts.voidborn.util.VoidbornUtil;
import com.natamus.voidvanguards.registry.objects.VanguardItems;
import com.natamus.voidvanguards.util.Reference;
import com.natamus.voidvanguards.util.SaveLoadUtils;
import com.natamus.voidvanguards.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PlayerHeadBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class VanguardFunctions {

	public static void initialPostSkyShipEventTrigger(ServerLevel serverLevel, ServerPlayer serverPlayer) {
		MessageFunctions.broadcastMessage(serverLevel, Component.translatable("voidvanguards.message.skyship.server_trigger", serverPlayer.getName().getString()).withStyle(ChatFormatting.GOLD));

		TaskFunctions.enqueueCollectiveServerTask(serverLevel.getServer(), () -> {
			spawnInitialVanguardsAroundPlayer(serverLevel, serverPlayer);
		}, 100);
	}

	public static void spawnInitialVanguardsAroundPlayer(ServerLevel serverLevel, ServerPlayer serverPlayer) {
		spawnInitialVanguardsAroundPlayer(serverLevel, serverPlayer, serverPlayer.blockPosition());
	}

	public static void spawnInitialVanguardsAroundPlayer(ServerLevel serverLevel, ServerPlayer serverPlayer, BlockPos spawnPos) {
		int vanguardCount = 8;

		List<BlockPos> vanguardPositions = VanguardUtil.getVanguardPositions(spawnPos, vanguardCount);
		List<LivingEntity> vanguards = VanguardUtil.getVanguardsToSummon(serverLevel, vanguardCount);

		spawnVanguardsAroundPlayer(serverLevel, serverPlayer, spawnPos, vanguardCount, true, vanguardPositions, vanguards);

		int i = 0;
		for (LivingEntity vanguard : vanguards) {
			BlockPos rawSpawnPos = vanguardPositions.get(i);
			BlockPos vanguardSpawnPos = BlockPosFunctions.getSurfaceBlockPos(serverLevel, rawSpawnPos.getX(), rawSpawnPos.getZ());

			vanguard.setPos(vanguardSpawnPos.getX() + 0.5, vanguardSpawnPos.getY(), vanguardSpawnPos.getZ() + 0.5);

			if (vanguard instanceof Mob) {
				((Mob) vanguard).setCanPickUpLoot(false);
			}

			serverLevel.addFreshEntity(vanguard);
			vanguard.lookAt(EntityAnchorArgument.Anchor.EYES, serverPlayer.position());

			VanguardUtil.spawnTeleportParticles(serverLevel, vanguardSpawnPos);

			i += 1;
		}

		for (BlockPos torchPos : VanguardUtil.getTorchLocations(spawnPos)) {
			if (serverLevel.getBlockState(torchPos).isAir()) {
				if (serverLevel.getBlockState(torchPos.below()).isAir()) {
					serverLevel.setBlock(torchPos.below(), Blocks.SOUL_TORCH.defaultBlockState(), 3);
					continue;
				}

				serverLevel.setBlock(torchPos, Blocks.SOUL_TORCH.defaultBlockState(), 3);
			}
		}

		SaveLoadUtils.setForcedPositionTag(serverPlayer, 4);

		if (!Util.isCosmosDimension(serverLevel)) {
			TaskFunctions.enqueueCollectiveServerTask(serverLevel.getServer(), () -> {
				SaveLoadUtils.removeForcedPositionTag(serverPlayer);

				CosmosDimensionFunctions.teleportPlayerToVanguardStation(serverLevel, serverPlayer, true);

				TaskFunctions.enqueueCollectiveServerTask(serverLevel.getServer(), () -> {
					for (LivingEntity vanguard : vanguards) {
						vanguard.remove(Entity.RemovalReason.DISCARDED);
					}
				}, 10);
			}, 100);
		} else {
			VanguardUtil.removeGrassBetweenVanguards(serverLevel, vanguardPositions);

			StoryFunctions.processVanguardInitialCosmos(serverLevel, serverPlayer, spawnPos, vanguards);
		}
	}

	public static void spawnVanguardsAroundPlayer(ServerLevel serverLevel, ServerPlayer serverPlayer, BlockPos spawnPos, int amount, boolean onSurface) {
		List<BlockPos> vanguardPositions = VanguardUtil.getVanguardPositions(spawnPos, amount);
		List<LivingEntity> vanguards = VanguardUtil.getVanguardsToSummon(serverLevel, amount);

		spawnVanguardsAroundPlayer(serverLevel, serverPlayer, spawnPos, amount, onSurface, vanguardPositions, vanguards);
	}
	public static void spawnVanguardsAroundPlayer(ServerLevel serverLevel, ServerPlayer serverPlayer, BlockPos spawnPos, int amount, boolean onSurface, List<BlockPos> vanguardPositions, List<LivingEntity> vanguards) {
		int i = 0;
		for (LivingEntity vanguard : vanguards) {
			BlockPos rawSpawnPos = vanguardPositions.get(i);

			BlockPos vanguardSpawnPos;
			if (onSurface) {
				vanguardSpawnPos = BlockPosFunctions.getSurfaceBlockPos(serverLevel, rawSpawnPos.getX(), rawSpawnPos.getZ());
			}
			else {
				vanguardSpawnPos = rawSpawnPos.immutable();
			}

			vanguard.setPos(vanguardSpawnPos.getX() + 0.5, vanguardSpawnPos.getY(), vanguardSpawnPos.getZ() + 0.5);

			serverLevel.addFreshEntity(vanguard);
			vanguard.lookAt(EntityAnchorArgument.Anchor.EYES, serverPlayer.position());

			VanguardUtil.spawnTeleportParticles(serverLevel, vanguardSpawnPos);

			i += 1;
		}
	}

	public static void createGravesAfterInitialEncounter(ServerLevel serverLevel, ServerPlayer serverPlayer, BlockPos spawnPos, List<LivingEntity> vanguards) {
		for (LivingEntity vanguard : vanguards) {
			vanguard.remove(Entity.RemovalReason.DISCARDED);
		}

		// First grave position (headstone): x: -1, z: -16
		BlockPos firstGravePos = spawnPos.offset(-1, 0, -16);

		List<BlockPos> gravePositions = new ArrayList<>();
		for (int i = 0; i <= 3; i++) {
			gravePositions.add(firstGravePos.offset(2 * i, 0, 0));
		}

		int n = 1;
		for (BlockPos gravePos : gravePositions) {
			for (BlockPos aroundPos : BlockPos.betweenClosed(gravePos.offset(-1, 0, -1), gravePos.offset(1, 0, 3))) {
				serverLevel.setBlock(aroundPos, Blocks.AIR.defaultBlockState(), 3);
			}

			serverLevel.setBlock(gravePos, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH), 3);

			BlockPos signPos = gravePos.south();
			serverLevel.setBlockAndUpdate(signPos, Blocks.WARPED_WALL_SIGN.defaultBlockState().setValue(WallSignBlock.FACING, Direction.SOUTH));
			if (serverLevel.getBlockEntity(signPos) instanceof SignBlockEntity signBlockEntity) {
				SignText signText = signBlockEntity.getFrontText();
				signText = signText.setMessage(1, Component.literal(VanguardVariables.vanguardNames.get(n)));
				signText = signText.setMessage(2, Component.literal(VanguardVariables.vanguardBirthYears.get(n) + " - " + Util.getCurrentYearString()));

				signBlockEntity.setText(signText, true);
				TileEntityFunctions.updateTileEntity(serverLevel, signPos, signBlockEntity);
			}


			// Vanguard Helmet
			BlockPos playerHeadPos = gravePos.south(2);

			serverLevel.setBlock(playerHeadPos, Blocks.PLAYER_HEAD.defaultBlockState().setValue(PlayerHeadBlock.ROTATION, 8), 3);
			if (serverLevel.getBlockEntity(playerHeadPos) instanceof SkullBlockEntity skullBlockEntity) {
				GameProfile headGameProfile = HeadData.getHeadGameProfile(HeadData.HeadType.VANGUARD);

				if (headGameProfile != null) {
					EntityFunctions.setSkullBlockOwner(skullBlockEntity, new ResolvableProfile(headGameProfile));
				}
			}


			serverLevel.setBlock(gravePos.below(), Blocks.POLISHED_BLACKSTONE_BRICKS.defaultBlockState(), 3);
			serverLevel.setBlock(gravePos.below().south(), Blocks.PODZOL.defaultBlockState(), 3);
			serverLevel.setBlock(gravePos.below().south(2), Blocks.PODZOL.defaultBlockState(), 3);

			n += 1;
		}
	}

	public static void generateRadioAtPlayerPosition(ServerPlayer serverPlayer) {
		generateRadioAtPlayerPosition(serverPlayer, 2);
	}
	public static void generateRadioAtPlayerPosition(ServerPlayer serverPlayer, int n) {
		ServerLevel serverLevel = serverPlayer.serverLevel();

		Vec3 eyePos = serverPlayer.getEyePosition();
		Vec3 lookVec = serverPlayer.getLookAngle();
		Direction facing = serverPlayer.getDirection();

		for (int i = n; i <= 2; i++) {
			BlockPos checkPos = BlockPos.containing(eyePos.add(lookVec.scale(i)));
			if (serverLevel.isEmptyBlock(checkPos)) {
				GlowItemFrame frame = new GlowItemFrame(serverLevel, checkPos, facing.getOpposite()); // face toward player
				frame.setItem(new ItemStack(VanguardItems.ORBITAL_RADIO));
				frame.setInvisible(true);

				frame.addTag(Reference.MOD_ID + ".tempframe");

				if (facing == Direction.UP || facing == Direction.DOWN) {
					float yaw = Mth.wrapDegrees(serverPlayer.getYRot());
					int rotationIndex = Mth.floor((yaw + 180F) / 45F) & 7;
					frame.setRotation(rotationIndex);
				}

				serverLevel.addFreshEntity(frame);
				VanguardUtil.spawnGenerationParticles(serverLevel, checkPos);

				if (n > 0 || i == 0) {
					MessageFunctions.sendMessage(serverPlayer, Component.translatable("item.voidvanguards.orbital_radio.spawned.front").withStyle(ChatFormatting.BLUE), true);
				}
				else {
					MessageFunctions.sendMessage(serverPlayer, Component.translatable("item.voidvanguards.orbital_radio.spawned.behind").withStyle(ChatFormatting.BLUE), true);
				}
				return;
			}
		}

		if (n > 0) { // Try again behind player
			generateRadioAtPlayerPosition(serverPlayer, -2);
		}
	}

	public static void processVanguardTreeTeleportVoidbase(ServerLevel cosmosLevel, ServerPlayer serverPlayer) {
		MinecraftServer minecraftServer = cosmosLevel.getServer();

		BlockPos bottomTreePos = VoidbornUtil.getVoidbornBaseSpawnPos(serverPlayer).offset(4, 0, 0);

		cosmosLevel.setBlock(bottomTreePos, Blocks.AIR.defaultBlockState(), 3);
		cosmosLevel.setBlock(bottomTreePos.above(1), Blocks.AIR.defaultBlockState(), 3);
		cosmosLevel.setBlock(bottomTreePos.above(2), Blocks.AIR.defaultBlockState(), 3);

		serverPlayer.teleportTo(bottomTreePos.getX() + 0.5, bottomTreePos.getY(), bottomTreePos.getZ() + 0.5);

		List<AbstractIllager> nearbyVoidborn = cosmosLevel.getEntitiesOfClass(
				AbstractIllager.class,
				serverPlayer.getBoundingBox().inflate(32.0D),
				illager -> illager.getTags().contains(VoidbornConstants.voidbornTag)
		);

		for (AbstractIllager voidborn : nearbyVoidborn) {
			Util.spawnLightning(cosmosLevel, voidborn.blockPosition(), serverPlayer);

			voidborn.remove(Entity.RemovalReason.KILLED);
		}

		TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
			List<BlockPos> vanguardPositions = VanguardUtil.getVanguardPositions(bottomTreePos, 4);
			List<LivingEntity> vanguards = VanguardUtil.getVanguardsToSummon(cosmosLevel, 4);

			for (BlockPos vanguardPos : vanguardPositions) {
				for (BlockPos nearbyPos : BlockPos.betweenClosed(vanguardPos.offset(-1, -1, -1), vanguardPos.offset(1, 1, 1))) {
					if (cosmosLevel.getBlockState(nearbyPos).is(Blocks.FIRE)) {
						cosmosLevel.removeBlock(nearbyPos, false);
					}
				}
			}

			spawnVanguardsAroundPlayer(cosmosLevel, serverPlayer, bottomTreePos, 4, false, vanguardPositions, vanguards);

			SaveLoadUtils.setForcedPositionTag(serverPlayer, 4);

			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				VanguardUtil.oblibirateTreeAbovePosition(cosmosLevel, bottomTreePos, 10, 2);
			}, 10);

			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				StoryFunctions.processVanguardsInVoidbornBase(serverPlayer.serverLevel(), serverPlayer, vanguards);
			}, 20);
		}, 20);
	}


	public static void resetVanguardVariables(Player player) {
		SaveLoadUtils.removeOldSaveTag(player);

		ServerSaveData.removeUUID(player.getUUID());
	}
}
