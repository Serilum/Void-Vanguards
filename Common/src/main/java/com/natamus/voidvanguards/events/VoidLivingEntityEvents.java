package com.natamus.voidvanguards.events;

import com.natamus.collective.functions.MessageFunctions;
import com.natamus.voidvanguards.data.Constants;
import com.natamus.voidvanguards.data.ServerSaveData;
import com.natamus.voidvanguards.parts.cosmos.util.CosmosDimensionUtil;
import com.natamus.voidvanguards.parts.story.functions.StoryFunctions;
import com.natamus.voidvanguards.parts.vanguard.data.VanguardConstants;
import com.natamus.voidvanguards.parts.vanguard.util.VanguardUtil;
import com.natamus.voidvanguards.parts.voidborn.data.VoidbornConstants;
import com.natamus.voidvanguards.parts.voidborn.functions.VoidbornFunctions;
import com.natamus.voidvanguards.util.Reference;
import com.natamus.voidvanguards.util.SaveLoadUtils;
import com.natamus.voidvanguards.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Set;

public class VoidLivingEntityEvents {
	public static void onLivingEntityTick(Level level, LivingEntity livingEntity) {
		if (level.isClientSide) {
			return;
		}

		if (livingEntity instanceof Villager) {
			if (!livingEntity.getTags().contains(Reference.MOD_ID + ".vanguard")) {
				return;
			}

			List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class,
					livingEntity.getBoundingBox().inflate(10.0D));

			if (!nearbyPlayers.isEmpty()) {
				Player nearestPlayer = nearbyPlayers.get(0);
				livingEntity.lookAt(EntityAnchorArgument.Anchor.EYES, nearestPlayer.position());
			}
		}

		if (livingEntity instanceof ServerPlayer serverPlayer) {
			if (serverPlayer.tickCount % 10 == 0) {
				String forcedTag = null;

				for (String tag : serverPlayer.getTags()) {
					if (tag.startsWith(Constants.forcedPositionTagPrefix)) {
						VanguardUtil.keepPlayersWithinVanguardCircle(serverPlayer, tag);
					}
					else if (tag.startsWith(Constants.sendMessageTagPrefix)) {
						String message = tag.split(Constants.saveDataTagMainDelimiter)[1];

						MessageFunctions.sendMessage(serverPlayer, message, ChatFormatting.DARK_GRAY, true);

						Util.removeTag(serverPlayer, tag);
					}
				}
			}

			if (serverPlayer.tickCount % 20 == 0) {
				if (Util.isInCosmosDimension(serverPlayer)) {
					if (CosmosDimensionUtil.playerIsInVoidbornBase(serverPlayer)) {
						if (!ServerSaveData.get().triggeredVoidbornBaseStoryPlayerUUIDS.contains(serverPlayer.getUUID())) {
							VoidbornFunctions.checkVoidbornLeftForStoryTrigger(serverPlayer.serverLevel(), serverPlayer);
						}
					}
				}
			}
		}
	}

	@SuppressWarnings("RedundantIfStatement")
	public static boolean onEntityDamage(Level level, Entity entity, DamageSource damageSource, float damageAmount) {
		if (level.isClientSide) {
			return true;
		}

		Entity sourceEntity = damageSource.getEntity();
		if (entity instanceof ServerPlayer serverPlayer) {
			if (Util.isInCosmosDimension(serverPlayer)) {
				if (damageSource.getMsgId().equals("lightningBolt")) {
					return false;
				}
			}
		}
		else if (entity instanceof Villager villager) {
			if (villager.getTags().contains(VanguardConstants.vanguardTag) && !villager.getTags().contains(Reference.MOD_ID + ".vulnerable")) {
				return false;
			}
		}
		else if (entity instanceof AbstractIllager abstractIllager) {
			Set<String> tags = abstractIllager.getTags();
			if (tags.contains(VoidbornConstants.voidbornLeaderTag)) {
				if (tags.contains(Reference.MOD_ID + ".vulnerable") && !tags.contains(Reference.MOD_ID + ".attacked") && sourceEntity instanceof ServerPlayer serverPlayer) {
					StoryFunctions.processVoidbornLeaderAttacked(serverPlayer.serverLevel(), serverPlayer, (Mob)abstractIllager);
					return true;
				}
				else if (tags.contains(Reference.MOD_ID + ".vulnerable")) {
					return true;
				}

				return false;
			}
		}

		return true;
	}

	public static void onPlayerDeath(ServerPlayer player, DamageSource damageSource, float damageAmount) {
		SaveLoadUtils.removeForcedPositionTag(player);
	}
}