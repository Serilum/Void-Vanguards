package com.natamus.voidvanguards.parts.story.functions;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.ItemFunctions;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.collective.functions.TaskFunctions;
import com.natamus.collective.implementations.networking.api.Dispatcher;
import com.natamus.collective.services.Services;
import com.natamus.voidvanguards.data.ServerSaveData;
import com.natamus.voidvanguards.networking.packets.ToClientQueueScreenFlickerEffectPacket;
import com.natamus.voidvanguards.parts.cosmos.functions.CosmosDimensionFunctions;
import com.natamus.voidvanguards.parts.story.util.StoryUtil;
import com.natamus.voidvanguards.parts.vanguard.data.VanguardVariables;
import com.natamus.voidvanguards.parts.vanguard.functions.VanguardFunctions;
import com.natamus.voidvanguards.parts.vanguard.util.VanguardUtil;
import com.natamus.voidvanguards.parts.voidborn.data.VoidbornConstants;
import com.natamus.voidvanguards.parts.voidborn.functions.VoidbornFunctions;
import com.natamus.voidvanguards.parts.voidborn.util.VoidbornUtil;
import com.natamus.voidvanguards.registry.block.CosmosPortalBlock;
import com.natamus.voidvanguards.registry.objects.VanguardItems;
import com.natamus.voidvanguards.util.Reference;
import com.natamus.voidvanguards.util.SaveLoadUtils;
import com.natamus.voidvanguards.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class StoryFunctions {
	public static void processVanguardInitialCosmos(ServerLevel serverLevel, ServerPlayer serverPlayer, BlockPos spawnPos, List<LivingEntity> vanguards) {
		String baseKey = "voidvanguards.message.vanguards.cosmos.initial.";
		MutableComponent speakerComponent = StoryUtil.getSpeakerComponent("voidvanguards.vanguard.name.leader", ChatFormatting.DARK_RED, true);

		int flickerDelay = -1;

		int totalDelay = 20;
		int lastDelay = 0;
		for (int i = 1; i <= 12; i++) {
			MutableComponent messageTextComponent = Component.translatable(baseKey + i);
			String rawMessage = messageTextComponent.getString();
			if (rawMessage.contains("%s")) {
				if (i == 8) {
					messageTextComponent = Component.translatable(baseKey + i, Component.translatable("voidvanguards.voidborn.name").withStyle(ChatFormatting.DARK_PURPLE));
				}
				else {
					messageTextComponent = Component.translatable(baseKey + i, serverPlayer.getName());
				}
				rawMessage = messageTextComponent.getString();
			}

			ChatFormatting messageColour = StoryUtil.getMessageColour(rawMessage, "...", ChatFormatting.WHITE, ChatFormatting.RED);
			if (flickerDelay <= 0 && messageColour.equals(ChatFormatting.RED)) {
				flickerDelay = totalDelay;
			}

			MutableComponent messageComponent = speakerComponent.copy().append(messageTextComponent.withStyle(messageColour));
			int delay = StoryUtil.getSentenceTickDelay(messageComponent);

			TaskFunctions.enqueueCollectiveServerTask(serverLevel.getServer(), () -> {
				MessageFunctions.sendMessage(serverPlayer, messageComponent);
			}, totalDelay);

			totalDelay += delay;
			lastDelay = delay;
		}

		Dispatcher.sendToClient(new ToClientQueueScreenFlickerEffectPacket(flickerDelay, totalDelay - lastDelay - flickerDelay), serverPlayer);

		TaskFunctions.enqueueCollectiveServerTask(serverLevel.getServer(), () -> {
			SaveLoadUtils.removeForcedPositionTag(serverPlayer);

			if (Util.teleportPlayerToOriginalPosition(serverPlayer)) {
				MinecraftServer minecraftServer = serverLevel.getServer();

				TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
					VanguardFunctions.createGravesAfterInitialEncounter(serverLevel, serverPlayer, spawnPos, vanguards);
				}, 1);

				TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
					VanguardFunctions.generateRadioAtPlayerPosition(serverPlayer);
				}, 100);

				MessageFunctions.sendMessage(serverPlayer, Component.translatable("voidvanguards.message.vanguards.home.initial.1").withStyle(ChatFormatting.GOLD), true);
			}
		}, totalDelay - lastDelay);
	}

	public static void processUsingOrbitalRadio(ServerLevel serverLevel, ServerPlayer serverPlayer) {
		MinecraftServer minecraftServer = serverLevel.getServer();

		UUID playerUUID = serverPlayer.getUUID();

		String baseKey = "voidvanguards.message.vanguards.radio.initial.";
		MutableComponent speakerComponent = StoryUtil.getSpeakerComponent("item.voidvanguards.orbital_radio", ChatFormatting.AQUA, true);

		if (!ServerSaveData.get().voidbornCosmosGatePositionPlayerUUIDS.containsKey(playerUUID)) {
			BlockPos voidbornBaseBlockPos = Util.getRANDOMVoidbornCosmosGateCoordinates(serverLevel, serverPlayer);

			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				VoidbornFunctions.generateVoidbornCosmosGate(serverLevel, serverPlayer, voidbornBaseBlockPos);
			},0);

			int totalDelay = 20;
			for (int i = 1; i <= 14; i++) {
				MutableComponent messageTextComponent = Component.translatable(baseKey + i);
				String rawMessage = messageTextComponent.getString();
				if (rawMessage.contains("%s")) {
					if (i == 8) {
						messageTextComponent = Component.translatable(baseKey + i, Component.translatable("voidvanguards.voidborn.name").withStyle(ChatFormatting.DARK_PURPLE));
					} else if (i == 11) {
						messageTextComponent = Component.translatable(baseKey + i, Component.literal(voidbornBaseBlockPos.getX() + ", " + voidbornBaseBlockPos.getY() + ", " + voidbornBaseBlockPos.getZ()).withStyle(ChatFormatting.GOLD));
					} else {
						messageTextComponent = Component.translatable(baseKey + i, serverPlayer.getName());
					}
					rawMessage = messageTextComponent.getString();
				}

				ChatFormatting messageColour = ChatFormatting.WHITE;

				MutableComponent messageComponent = speakerComponent.copy().append(messageTextComponent.withStyle(messageColour));
				int delay = StoryUtil.getSentenceTickDelay(messageComponent);

				TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
					MessageFunctions.sendMessage(serverPlayer, messageComponent);
				}, totalDelay);

				totalDelay += delay;
			}

			ServerSaveData.get().answeredRadioPlayerUUIDS.add(playerUUID);
			SaveLoadUtils.savePlayerData(serverPlayer);
		}
		else {
			Pair<ResourceKey<Level>, BlockPos> voidbornBasePosPair = ServerSaveData.get().voidbornCosmosGatePositionPlayerUUIDS.get(playerUUID);

			ResourceKey<Level> levelResourceKey = voidbornBasePosPair.getFirst();
			BlockPos voidbornBasePos = voidbornBasePosPair.getSecond();

			// Overworld only?
			// Pos reminder

			MessageFunctions.sendMessage(serverPlayer, Component.translatable("voidvanguards.message.vanguards.radio.coordinates.1"));
			MessageFunctions.sendMessage(serverPlayer, Component.translatable("voidvanguards.message.vanguards.radio.coordinates.2", Component.literal(voidbornBasePos.getX() + ", " + voidbornBasePos.getY() + ", " + voidbornBasePos.getZ()).withStyle(ChatFormatting.GOLD)));
		}
	}

	public static void processInitialVoidbornLeaderEncounter(ServerLevel serverLevel, ServerPlayer serverPlayer, Mob voidbornLeader) {
		MinecraftServer minecraftServer = serverLevel.getServer();

		ServerSaveData.get().triggeredVoidbornLeaderPlayerUUIDS.add(serverPlayer.getUUID());
		SaveLoadUtils.savePlayerData(serverPlayer);

		for (Mob mob : serverLevel.getEntitiesOfClass(Mob.class, voidbornLeader.getBoundingBox().inflate(120.0D))) {
			if (mob instanceof Vex) {
				mob.remove(Entity.RemovalReason.DISCARDED);
				continue;
			}

			if (mob.getTags().contains(VoidbornConstants.voidbornTag)) {
				mob.addTag(Reference.MOD_ID + ".notarget");
				mob.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0D);
				mob.setTarget(null);
			}
		}

		String baseKey = "voidvanguards.message.voidborn.initial.";
		MutableComponent speakerComponent = StoryUtil.getSpeakerComponent("voidvanguards.voidborn.name.leader", ChatFormatting.DARK_PURPLE, true);

		int totalDelay = 20;
		int lastDelay = 0;
		for (int i = 1; i <= 11; i++) {
			MutableComponent messageTextComponent = Component.translatable(baseKey + i);
			String rawMessage = messageTextComponent.getString();
			if (rawMessage.contains("%s")) {
				messageTextComponent = Component.translatable(baseKey + i, serverPlayer.getName());
				rawMessage = messageTextComponent.getString();
			}

			ChatFormatting messageColour;
			if (i == 8) {
				messageColour = ChatFormatting.RED;
			}
			else if (i == 11) {
				messageColour = ChatFormatting.GRAY;
			} else {
				messageColour = ChatFormatting.WHITE;
			}

			MutableComponent messageComponent;
			if (!rawMessage.startsWith(" ~")) {
				messageComponent = speakerComponent.copy().append(messageTextComponent.withStyle(messageColour));
			}
			else {
				messageComponent = messageTextComponent.withStyle(messageColour);
			}

			int delay = StoryUtil.getSentenceTickDelay(messageComponent);

			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				MessageFunctions.sendMessage(serverPlayer, messageComponent, messageColour.equals(ChatFormatting.GRAY));
			}, totalDelay);

			totalDelay += delay;
			lastDelay = delay;
		}

		TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
			voidbornLeader.addTag(Reference.MOD_ID + ".vulnerable");
		}, totalDelay - lastDelay);
	}

	public static void processVoidbornLeaderGiveRadio(ServerLevel serverLevel, ServerPlayer serverPlayer, Mob voidbornLeader, ItemStack orbitalRadioStack) {
		MinecraftServer minecraftServer = serverLevel.getServer();

		UUID playerUUID = serverPlayer.getUUID();
		if (ServerSaveData.get().gaveVoidbornLeaderRadioPlayerUUIDS.contains(playerUUID)) {
			return;
		}

		ServerSaveData.get().gaveVoidbornLeaderRadioPlayerUUIDS.add(playerUUID);
		SaveLoadUtils.savePlayerData(serverPlayer);

		SaveLoadUtils.removeForcedPositionTag(serverPlayer);

		orbitalRadioStack.shrink(1);

		String baseKey = "voidvanguards.message.voidborn.givenradio.";
		MutableComponent speakerComponent = StoryUtil.getSpeakerComponent("voidvanguards.voidborn.name.leader", ChatFormatting.DARK_PURPLE, true);

		int totalDelay = 0;
		for (int i = 1; i <= 3; i++) {
			MutableComponent messageTextComponent = Component.translatable(baseKey + i);
			String rawMessage = messageTextComponent.getString();
			if (rawMessage.contains("%s")) {
				messageTextComponent = Component.translatable(baseKey + i, serverPlayer.getName());
				rawMessage = messageTextComponent.getString();
			}

			ChatFormatting messageColour = ChatFormatting.WHITE;

			MutableComponent messageComponent = speakerComponent.copy().append(messageTextComponent.withStyle(messageColour));

			int delay = StoryUtil.getSentenceTickDelay(messageComponent);

			int finalI = i;
			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				MessageFunctions.sendMessage(serverPlayer, messageComponent, finalI == 1);
			}, totalDelay);

			totalDelay += delay;
		}

		TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
			MessageFunctions.sendMessage(serverPlayer, speakerComponent.copy().append(Component.translatable(baseKey + "4").withStyle(ChatFormatting.DARK_PURPLE)), true);

			for (Mob mob : serverLevel.getEntitiesOfClass(Mob.class, serverPlayer.getBoundingBox().inflate(120.0D))) {
				Set<String> tags = mob.getTags();
				if (tags.contains(VoidbornConstants.voidbornTag) && !tags.contains(VoidbornConstants.voidbornLeaderTag)) {
					VanguardUtil.spawnTeleportParticles(serverLevel, mob.blockPosition());

					mob.remove(Entity.RemovalReason.DISCARDED);
				}
			}

			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				VanguardUtil.spawnTeleportParticles(serverLevel, voidbornLeader.blockPosition());

				voidbornLeader.remove(Entity.RemovalReason.DISCARDED);

				TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
					CosmosDimensionFunctions.teleportPlayerToVanguardStation(serverLevel, serverPlayer, false);

					processPostGivingRadioVanguardStation(Util.getCosmosLevel(serverLevel.getServer()), serverPlayer);
				}, 20);
			}, 20);
		}, totalDelay + 100);
	}

	public static void processVoidbornLeaderAttacked(ServerLevel serverLevel, ServerPlayer serverPlayer, Mob voidbornLeader) {
		MinecraftServer minecraftServer = serverLevel.getServer();

		voidbornLeader.addTag(Reference.MOD_ID + ".attacked");

		for (Mob mob : serverLevel.getEntitiesOfClass(Mob.class, voidbornLeader.getBoundingBox().inflate(120.0D))) {
			if (!mob.getTags().contains(VoidbornConstants.voidbornTag)) {
				return;
			}

			mob.removeTag(Reference.MOD_ID + ".notarget");

			float movementValue = VoidbornConstants.voidbornMovementSpeeds.get(mob.getType());
			mob.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(movementValue);
		}

		SaveLoadUtils.removeForcedPositionTag(serverPlayer);

		String baseKey = "voidvanguards.message.voidborn.attacked.";
		MutableComponent speakerComponent = StoryUtil.getSpeakerComponent("voidvanguards.voidborn.name.leader", ChatFormatting.DARK_PURPLE, true);

		int totalDelay = 0;
		for (int i = 1; i <= 4; i++) {
			MutableComponent messageTextComponent = Component.translatable(baseKey + i);
			String rawMessage = messageTextComponent.getString();
			if (rawMessage.contains("%s")) {
				messageTextComponent = Component.translatable(baseKey + i, serverPlayer.getName());
				rawMessage = messageTextComponent.getString();
			}

			ChatFormatting messageColour;
			if (i == 1) {
				messageColour = ChatFormatting.RED;
			}
			else if (i == 4) {
				messageColour = ChatFormatting.GRAY;
			} else {
				messageColour = ChatFormatting.WHITE;
			}

			MutableComponent messageComponent;
			if (!rawMessage.startsWith(" ~")) {
				messageComponent = speakerComponent.copy().append(messageTextComponent.withStyle(messageColour));
			}
			else {
				messageComponent = messageTextComponent.withStyle(messageColour);
			}

			int delay = StoryUtil.getSentenceTickDelay(messageComponent);

			int finalI = i;
			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				MessageFunctions.sendMessage(serverPlayer, messageComponent, finalI == 1 || messageColour.equals(ChatFormatting.GRAY));
			}, totalDelay);

			totalDelay += delay;
		}
	}

	public static void processVanguardsInVoidbornBase(ServerLevel serverLevel, ServerPlayer serverPlayer, List<LivingEntity> vanguards) {
		MinecraftServer minecraftServer = serverLevel.getServer();

		String baseKey = "voidvanguards.message.vanguards.voidbornBase.";
		MutableComponent speakerComponent = StoryUtil.getSpeakerComponent("voidvanguards.vanguard.name.leader", ChatFormatting.DARK_RED, true);

		int totalDelay = 20;
		for (int i = 1; i <= 6; i++) {
			MutableComponent messageTextComponent = Component.translatable(baseKey + i);
			String rawMessage = messageTextComponent.getString();
			if (rawMessage.contains("%s")) {
				if (i == 2) {
					messageTextComponent = Component.translatable(baseKey + i, Component.translatable("voidvanguards.voidborn.name").withStyle(ChatFormatting.DARK_PURPLE));
				}
				else {
					messageTextComponent = Component.translatable(baseKey + i, serverPlayer.getName());
				}
				rawMessage = messageTextComponent.getString();
			}

			ChatFormatting messageColour = ChatFormatting.WHITE;

			MutableComponent messageComponent = speakerComponent.copy().append(messageTextComponent.withStyle(messageColour));
			int delay = StoryUtil.getSentenceTickDelay(messageComponent);

			int finalI = i;
			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				MessageFunctions.sendMessage(serverPlayer, messageComponent, finalI == 1);
			}, totalDelay);

			totalDelay += delay;
		}

		TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
			SaveLoadUtils.removeForcedPositionTag(serverPlayer);

			ServerLevel overworldLevel = minecraftServer.overworld();
			Pair<ResourceKey<Level>, BlockPos> cosmosGatePair = ServerSaveData.get().voidbornCosmosGatePositionPlayerUUIDS.get(serverPlayer.getUUID());

			BlockPos gatePos;
			if (cosmosGatePair == null) {
				gatePos = Util.getRANDOMVoidbornCosmosGateCoordinates(overworldLevel, serverPlayer);
			}
			else {
				gatePos = cosmosGatePair.getSecond();
			}

			Services.TELEPORT.teleportEntity(serverPlayer, overworldLevel, gatePos);

			for (LivingEntity vanguard : vanguards) {
				vanguard.remove(Entity.RemovalReason.DISCARDED);
			}

			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				for (BlockPos aroundPos : BlockPos.betweenClosed(gatePos.offset(-10, -10, -10), gatePos.offset(10, 10, 10))) {
					if (overworldLevel.getBlockState(aroundPos).getBlock() instanceof CosmosPortalBlock) {
						overworldLevel.setBlock(aroundPos, Blocks.AIR.defaultBlockState(), 3);
					}
				}
			}, 50);

			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				processPostVoidbornBaseVanguardLeader(overworldLevel, serverPlayer);
			}, 200);
		}, totalDelay);
	}

	public static void processPostGivingRadioVanguardStation(ServerLevel serverLevel, ServerPlayer serverPlayer) {

		MinecraftServer minecraftServer = serverLevel.getServer();

		TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
			int vanguardCount = 4;
			List<BlockPos> vanguardPositions = VanguardUtil.getVanguardPositions(serverPlayer.blockPosition(), vanguardCount);
			List<LivingEntity> vanguards = VanguardUtil.getVanguardsToSummon(serverLevel, vanguardCount);

			VanguardFunctions.spawnVanguardsAroundPlayer(serverLevel, serverPlayer, serverPlayer.blockPosition(), vanguardCount, true, vanguardPositions, vanguards);

			for (LivingEntity vanguard : vanguards) {
				vanguard.addTag(Reference.MOD_ID + ".vulnerable");
			}

			String baseKey = "voidvanguards.message.voidborn.vanguardStation.";
			MutableComponent vanguardSpeakerComponent = StoryUtil.getSpeakerComponent("voidvanguards.vanguard.name.leader", ChatFormatting.DARK_RED, true);
			MutableComponent voidbornSpeakerComponent = StoryUtil.getSpeakerComponent("voidvanguards.voidborn.name.leader", ChatFormatting.DARK_PURPLE, true);

			int totalDelay = 20;
			for (int i = 1; i <= 4; i++) {
				MutableComponent messageTextComponent = Component.translatable(baseKey + i);
				String rawMessage = messageTextComponent.getString();
				if (rawMessage.contains("%s")) {
					messageTextComponent = Component.translatable(baseKey + i, serverPlayer.getName());
					rawMessage = messageTextComponent.getString();
				}

				ChatFormatting messageColour = StoryUtil.getMessageColour(rawMessage, "...", ChatFormatting.WHITE, ChatFormatting.RED);

				MutableComponent messageComponent = vanguardSpeakerComponent.copy().append(messageTextComponent.withStyle(messageColour));
				int delay = StoryUtil.getSentenceTickDelay(messageComponent);

				TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
					MessageFunctions.sendMessage(serverPlayer, messageComponent);
				}, totalDelay);

				totalDelay += delay;
			}

			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				BlockPos voidbornLeaderPos = serverPlayer.blockPosition().north();

				Mob voidbornLeader = VoidbornUtil.getVoidbornLeader(serverLevel, voidbornLeaderPos);
				voidbornLeader.addTag(Reference.MOD_ID + ".notarget");

				serverLevel.addFreshEntity(voidbornLeader);

				VanguardUtil.spawnTeleportParticles(serverLevel, voidbornLeaderPos);

				int newTotalDelay = 20;
				for (int i = 5; i <= 8; i++) {
					MutableComponent messageTextComponent = Component.translatable(baseKey + i);
					String rawMessage = messageTextComponent.getString();
					if (rawMessage.contains("%s")) {
						messageTextComponent = Component.translatable(baseKey + i, serverPlayer.getName());
						rawMessage = messageTextComponent.getString();
					}

					ChatFormatting messageColour = StoryUtil.getMessageColour(rawMessage, "...", ChatFormatting.WHITE, ChatFormatting.RED);

					MutableComponent messageComponent = voidbornSpeakerComponent.copy().append(messageTextComponent.withStyle(messageColour));
					int delay = StoryUtil.getSentenceTickDelay(messageComponent);

					int finalI = i;
					TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
						MessageFunctions.sendMessage(serverPlayer, messageComponent, finalI == 5);
					}, newTotalDelay);

					newTotalDelay += delay;
				}

				TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
					Registry<Enchantment> enchantmentRegistry = minecraftServer.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
					for (LivingEntity vanguard : vanguards) {
						vanguard.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.5F);
						for (int n = 1; n <= 4; n++) {
							Mob voidborn = VoidbornUtil.getVoidbornMob(serverLevel, vanguard.blockPosition(), enchantmentRegistry, false);
							voidborn.setPos(voidborn.getX()-n, voidborn.getY(), voidborn.getZ()+n);

							serverLevel.addFreshEntity(voidborn);
						}
					}

					TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
						int newNewTotalDelay = 0;
						for (int i = 9; i <= 11; i++) {
							MutableComponent messageTextComponent = Component.translatable(baseKey + i);
							String rawMessage = messageTextComponent.getString();
							if (rawMessage.contains("%s")) {
								messageTextComponent = Component.translatable(baseKey + i, serverPlayer.getName());
								rawMessage = messageTextComponent.getString();
							}

							ChatFormatting messageColour = StoryUtil.getMessageColour(rawMessage, "...", ChatFormatting.WHITE, ChatFormatting.RED);

							MutableComponent messageComponent = voidbornSpeakerComponent.copy().append(messageTextComponent.withStyle(messageColour));
							int delay = StoryUtil.getSentenceTickDelay(messageComponent);

							TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
								MessageFunctions.sendMessage(serverPlayer, messageComponent);
							}, newNewTotalDelay);

							newNewTotalDelay += delay;
						}

						TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
							ServerSaveData.get().voidVanguardsCompletedPlayerUUIDS.add(serverPlayer.getUUID());
							SaveLoadUtils.savePlayerData(serverPlayer);

							CosmosDimensionFunctions.teleportPlayerToVoidbornBase(serverPlayer.serverLevel(), serverPlayer);

							TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
								MessageFunctions.sendMessage(serverPlayer, Component.translatable("voidvanguards.message.voidborn.endAsteroid.1").withStyle(ChatFormatting.DARK_GREEN), true);
								MessageFunctions.sendMessage(serverPlayer, Component.translatable("voidvanguards.message.voidborn.endAsteroid.2").withStyle(ChatFormatting.DARK_GRAY));
							}, 10);
						}, newNewTotalDelay);
					}, 200);
				}, newTotalDelay);
			}, totalDelay);
		}, 10);
	}

	public static void processPostVoidbornBaseVanguardLeader(ServerLevel serverLevel, ServerPlayer serverPlayer) {
		MinecraftServer minecraftServer = serverLevel.getServer();

		int vanguardCount = 1;

		BlockPos playerPos = serverPlayer.blockPosition();
		List<BlockPos> vanguardPositions = VanguardUtil.getVanguardPositions(playerPos, vanguardCount);
		List<LivingEntity> vanguards = VanguardUtil.getVanguardsToSummon(serverLevel, vanguardCount);

		VanguardFunctions.spawnVanguardsAroundPlayer(serverLevel, serverPlayer, playerPos, vanguardCount, true, vanguardPositions, vanguards);

		SaveLoadUtils.setForcedPositionTag(serverPlayer, 4);

		String baseKey = "voidvanguards.message.vanguards.voidbornBaseDestroyed.";
		MutableComponent speakerComponent = StoryUtil.getSpeakerComponent("voidvanguards.vanguard.name.leader", ChatFormatting.DARK_RED, true);

		int totalDelay = 40;
		for (int i = 1; i <= 8; i++) {
			MutableComponent messageTextComponent = Component.translatable(baseKey + i);
			String rawMessage = messageTextComponent.getString();
			if (rawMessage.contains("%s")) {
				if (i == 2) {
					messageTextComponent = Component.translatable(baseKey + i, Component.translatable("voidvanguards.voidborn.name").withStyle(ChatFormatting.DARK_PURPLE));
				}
				else if (i == 8) {
					Component lyraName = Component.literal(VanguardVariables.vanguardNames.get(0)).withStyle(ChatFormatting.DARK_RED);

					messageTextComponent = Component.translatable(baseKey + i, lyraName);
					vanguards.get(0).setCustomName(lyraName);
				}
				else {
					messageTextComponent = Component.translatable(baseKey + i, serverPlayer.getName());
				}
				rawMessage = messageTextComponent.getString();
			}

			ChatFormatting messageColour = ChatFormatting.WHITE;
			if (i == 4) {
				messageColour = ChatFormatting.DARK_GRAY;
			}

			MutableComponent messageComponent;
			if (i == 8) {
				messageComponent = StoryUtil.getSpeakerComponent(VanguardVariables.vanguardNames.get(0), ChatFormatting.DARK_RED, false).copy().append(messageTextComponent.withStyle(messageColour));
			}
			else {
				messageComponent = speakerComponent.copy().append(messageTextComponent.withStyle(messageColour));
			}

			int delay = StoryUtil.getSentenceTickDelay(messageComponent);

			int finalI = i;
			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				MessageFunctions.sendMessage(serverPlayer, messageComponent, finalI == 1);

				if (finalI == 4) {
					ItemFunctions.giveOrDropItemStack(serverPlayer, new ItemStack(VanguardItems.ORBITAL_TRANSPONDER));
				}
			}, totalDelay);

			totalDelay += delay;
		}

		TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
			for (LivingEntity vanguard : vanguards) {
				VanguardUtil.spawnTeleportParticles(serverLevel, vanguard.blockPosition());

				vanguard.remove(Entity.RemovalReason.DISCARDED);
			}

			SaveLoadUtils.removeForcedPositionTag(serverPlayer);

			ServerSaveData.get().voidVanguardsCompletedPlayerUUIDS.add(serverPlayer.getUUID());
			SaveLoadUtils.savePlayerData(serverPlayer);
		}, totalDelay);

	}
}
