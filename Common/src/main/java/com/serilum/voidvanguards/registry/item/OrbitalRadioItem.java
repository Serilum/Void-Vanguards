package com.serilum.voidvanguards.registry.item;

import com.natamus.collective.functions.MessageFunctions;
import com.serilum.voidvanguards.data.ServerSaveData;
import com.serilum.voidvanguards.parts.story.functions.StoryFunctions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

public class OrbitalRadioItem extends Item {
	public OrbitalRadioItem(Properties properties) {
		super(properties);
	}

	public boolean releaseUsing(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull LivingEntity livingEntity, int n) {
		if (livingEntity instanceof Player player) {
			int useDuration = this.getUseDuration(itemStack, livingEntity) - n;
			if (useDuration >= 25 && !level.isClientSide()) {
				if (ServerSaveData.get().vanguardStationGeneratedPlayerUUIDS.contains(player.getUUID())) {
					MessageFunctions.sendMessage(player, Component.translatable("item.voidvanguards.orbital_radio.useSuccess").withStyle(ChatFormatting.DARK_GRAY));

					StoryFunctions.processUsingOrbitalRadio((ServerLevel)level, (ServerPlayer)player);
				}
				else {
					MessageFunctions.sendMessage(player, Component.translatable("item.voidvanguards.orbital_radio.useUnable").withStyle(ChatFormatting.DARK_GRAY));
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
