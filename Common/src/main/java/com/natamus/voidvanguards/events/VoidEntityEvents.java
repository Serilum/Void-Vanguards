package com.natamus.voidvanguards.events;

import com.natamus.collective.functions.MessageFunctions;
import com.natamus.collective.functions.TaskFunctions;
import com.natamus.collective.implementations.networking.api.Dispatcher;
import com.natamus.voidvanguards.data.ServerSaveData;
import com.natamus.voidvanguards.networking.packets.ToClientSkyShipEntersOrbitPacket;
import com.natamus.voidvanguards.parts.story.functions.StoryFunctions;
import com.natamus.voidvanguards.parts.voidborn.data.VoidbornConstants;
import com.natamus.voidvanguards.registry.item.OrbitalRadioItem;
import com.natamus.voidvanguards.util.Reference;
import com.natamus.voidvanguards.util.SaveLoadUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Set;
import java.util.UUID;

public class VoidEntityEvents {
	public static void onEntityJoin(Level level, Entity entity) {
	}

	public static void onEntityLeave(Level level, Entity entity) {
	}

	public static void onPlayerLogin(Level level, Player player) {
		if (level.isClientSide) {
			return;
		}

		SaveLoadUtils.loadPlayerData(player);

		UUID playerUUID = player.getUUID();
		if (ServerSaveData.get().skyShipTriggeredPlayerUUIDS.contains(playerUUID) || ServerSaveData.get().voidVanguardsCompletedPlayerUUIDS.contains(playerUUID)) {
			return;
		}

		TaskFunctions.enqueueCollectiveServerTask(level.getServer(), () -> {
			if (!ServerSaveData.get().skyShipTriggeredPlayerUUIDS.contains(player.getUUID())) {
				Dispatcher.sendToClient(new ToClientSkyShipEntersOrbitPacket(), (ServerPlayer) player); // TODO improve when it's spawned
			}
		}, 0);
	}

	public static void onPlayerLogout(Level world, Player player) {
		SaveLoadUtils.savePlayerData(player);
	}

	public static void onItemPickup(Level level, Player player, ItemStack itemStack) {
		if (level.isClientSide) {
			return;
		}

		if (itemStack.getItem() instanceof OrbitalRadioItem) {
			UUID playerUUID = player.getUUID();
			if (!ServerSaveData.get().vanguardStationGeneratedPlayerUUIDS.contains(playerUUID)) {
				return;
			}

			if (ServerSaveData.get().answeredRadioPlayerUUIDS.contains(playerUUID)) {
				return;
			}

			MessageFunctions.sendMessage(player, Component.translatable("item.voidvanguards.orbital_radio.pickedup").withStyle(ChatFormatting.RED), true);
		}
	}

	public static InteractionResult onEntityInteract(Player player, Level level, InteractionHand interactionHand, Entity target, EntityHitResult hitResult) {
		if (level.isClientSide()) {
			return InteractionResult.PASS;
		}

		ItemStack handStack = player.getItemInHand(interactionHand);
		Item handItem = handStack.getItem();
		if (!(handItem instanceof OrbitalRadioItem)) {
			return InteractionResult.PASS;
		}

		if (!(target instanceof Mob)) {
			return InteractionResult.PASS;
		}

		Set<String> targetTags = target.getTags();
		if (!(targetTags.contains(VoidbornConstants.voidbornLeaderTag))) {
			return InteractionResult.PASS;
		}

		if (!targetTags.contains(Reference.MOD_ID + ".vulnerable")) {
			return InteractionResult.PASS;
		}

		StoryFunctions.processVoidbornLeaderGiveRadio((ServerLevel)level, (ServerPlayer)player, (Mob)target, handStack);
		return InteractionResult.SUCCESS;
	}
}
