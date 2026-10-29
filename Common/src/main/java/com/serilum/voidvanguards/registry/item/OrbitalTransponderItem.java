package com.serilum.voidvanguards.registry.item;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.collective.services.Services;
import com.serilum.voidvanguards.data.ServerSaveData;
import com.serilum.voidvanguards.parts.cosmos.functions.CosmosDimensionFunctions;
import com.serilum.voidvanguards.util.SaveLoadUtils;
import com.serilum.voidvanguards.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class OrbitalTransponderItem extends Item {
	public OrbitalTransponderItem(Item.Properties properties) {
		super(properties);
	}

	public boolean releaseUsing(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity livingEntity, int n) {
		if (livingEntity instanceof ServerPlayer serverPlayer) {
			int useDuration = this.getUseDuration(itemStack, livingEntity) - n;
			if (useDuration >= 25 && !level.isClientSide()) {
				UUID playerUUID = serverPlayer.getUUID();
				if (ServerSaveData.get().voidVanguardsCompletedPlayerUUIDS.contains(playerUUID)) {
					MinecraftServer minecraftServer = level.getServer();


					if (!Util.isInCosmosDimension(serverPlayer)) {
						ServerSaveData.get().originalPlayerPositions.put(playerUUID, Pair.of(serverPlayer.level().dimension(), serverPlayer.blockPosition()));
						SaveLoadUtils.savePlayerData(serverPlayer);

						CosmosDimensionFunctions.teleportPlayerToVanguardStation((ServerLevel) level, serverPlayer, false);
					}
					else {
						Pair<ResourceKey<Level>, BlockPos> originalPositionPair = ServerSaveData.get().originalPlayerPositions.get(playerUUID);
						Services.TELEPORT.teleportEntity(serverPlayer, originalPositionPair.getFirst(), originalPositionPair.getSecond());

						MessageFunctions.sendMessage(serverPlayer, Component.translatable("item.voidvanguards.orbital_transponder.leaveStation").withStyle(ChatFormatting.DARK_GREEN));
					}
				}
				else {
					MessageFunctions.sendMessage(serverPlayer, Component.translatable("item.voidvanguards.orbital_transponder.useUnable").withStyle(ChatFormatting.DARK_GRAY));
				}
			}
		}
		return false;
	}

	public int getUseDuration(@NotNull ItemStack itemStack, @NotNull LivingEntity livingEntity) {
		return 72000;
	}

	public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack itemStack) {
		return ItemUseAnimation.BOW;
	}

	public @NotNull InteractionResult use(@NotNull Level level, Player player, @NotNull InteractionHand interactionHand) {
		player.startUsingItem(interactionHand);
		return InteractionResult.CONSUME;
	}
}
