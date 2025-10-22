package com.natamus.voidvanguards.events;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.collective.services.Services;
import com.natamus.voidvanguards.data.ServerSaveData;
import com.natamus.voidvanguards.parts.voidborn.functions.VoidbornFunctions;
import com.natamus.voidvanguards.parts.voidborn.util.VoidbornUtil;
import com.natamus.voidvanguards.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class VoidBlockEvents {
	public static boolean onBlockBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
		if (level.isClientSide()) {
			return true;
		}

		if (player.isCreative()) {
			return true;
		}

		if (!Util.isInCosmosDimension((ServerPlayer)player)) {
			return true;
		}

		return ServerSaveData.get().voidVanguardsCompletedPlayerUUIDS.contains(player.getUUID());
	}

	public static boolean onBlockPlace(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (level.isClientSide()) {
			return true;
		}

		if (!(livingEntity instanceof ServerPlayer serverPlayer)) {
			return true;
		}

		if (serverPlayer.isCreative()) {
			return true;
		}

		if (!Util.isInCosmosDimension(serverPlayer)) {
			return true;
		}

		return ServerSaveData.get().voidVanguardsCompletedPlayerUUIDS.contains(serverPlayer.getUUID());
	}

	public static InteractionResult onRightClickBlock(Player player, Level level, InteractionHand interactionHand, BlockHitResult blockHitResult) {
		if (level.isClientSide()) {
			return InteractionResult.PASS;
		}


		ServerPlayer serverPlayer = (ServerPlayer)player;
		if (!Util.isInCosmosDimension(serverPlayer)) {
			return InteractionResult.PASS;
		}

		BlockPos blockPos = blockHitResult.getBlockPos();
		BlockState blockState = level.getBlockState(blockPos);
		Block block = blockState.getBlock();

		if (block instanceof ButtonBlock) {
			if (!VoidbornUtil.isBlockPosTeleportBackButtonPosition(serverPlayer, blockPos)) {
				return InteractionResult.PASS;
			}

			ServerLevel overworldLevel = level.getServer().overworld();
			Pair<ResourceKey<Level>, BlockPos> cosmosGatePair = ServerSaveData.get().voidbornCosmosGatePositionPlayerUUIDS.get(serverPlayer.getUUID());

			BlockPos gatePos;
			if (cosmosGatePair == null) {
				gatePos = Util.getRANDOMVoidbornCosmosGateCoordinates(overworldLevel, serverPlayer);
			}
			else {
				gatePos = cosmosGatePair.getSecond();
			}

			Services.TELEPORT.teleportEntity(serverPlayer, overworldLevel, gatePos);
			MessageFunctions.sendMessage(serverPlayer, Component.translatable("voidvanguards.message.voidborn.cosmos_gate.enterSuccess.again").withStyle(ChatFormatting.DARK_PURPLE), true);
		}
		else if (block instanceof LeverBlock) {
			if (!VoidbornUtil.isBlockPosRadarLeverPosition(serverPlayer, blockPos)) {
				return InteractionResult.PASS;
			}

			if (ServerSaveData.get().sabotagedVoidbornRadarPlayerUUIDS.contains(serverPlayer.getUUID())) {
				MessageFunctions.sendMessage(serverPlayer, Component.translatable("voidvanguards.message.voidborn.base.radarLever.flip.unable").withStyle(ChatFormatting.DARK_GRAY));
				return InteractionResult.FAIL;
			}

			VoidbornFunctions.voidbornRadarLeverWasTriggered(serverPlayer.serverLevel(), serverPlayer);
		}

		return InteractionResult.PASS;
	}
}
