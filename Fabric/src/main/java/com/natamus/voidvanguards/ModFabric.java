package com.natamus.voidvanguards;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.collective.fabric.callbacks.CollectiveBlockEvents;
import com.natamus.collective.fabric.callbacks.CollectiveEntityEvents;
import com.natamus.collective.fabric.callbacks.CollectivePlayerEvents;
import com.natamus.voidvanguards.cmd.CommandVoidVanguards;
import com.natamus.voidvanguards.events.VoidBlockEvents;
import com.natamus.voidvanguards.events.VoidEntityEvents;
import com.natamus.voidvanguards.events.VoidLivingEntityEvents;
import com.natamus.voidvanguards.events.VoidUtilEvents;
import com.natamus.voidvanguards.util.Reference;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class ModFabric implements ModInitializer {

	@Override
	public void onInitialize() {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		setGlobalConstants();
		ModCommon.init();
		ModCommon.registerAssets(null);
		ModCommon.setAssets();

		loadEvents();

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadEvents() {
		// VoidBlockEvents
		PlayerBlockBreakEvents.BEFORE.register((level, player, pos, state, entity) -> {
			return VoidBlockEvents.onBlockBreak(level, player, pos, state, entity);
		});

		CollectiveBlockEvents.BLOCK_PLACE.register((level, blockPos, blockState, livingEntity, itemStack) -> {
			return VoidBlockEvents.onBlockPlace(level, blockPos, blockState, livingEntity, itemStack);
		});

		UseBlockCallback.EVENT.register((player, level, interactionHand, blockHitResult) -> {
			return VoidBlockEvents.onRightClickBlock(player, level, interactionHand, blockHitResult);
		});

		// VoidEntityEvents
		ServerEntityEvents.ENTITY_LOAD.register((entity, serverLevel) -> {
			VoidEntityEvents.onEntityJoin(serverLevel, entity);
		});

		ServerEntityEvents.ENTITY_UNLOAD.register((entity, serverLevel) -> {
			VoidEntityEvents.onEntityLeave(serverLevel, entity);
		});

		CollectivePlayerEvents.PLAYER_LOGGED_IN.register((level, player) -> {
			VoidEntityEvents.onPlayerLogin(level, player);
		});

		CollectivePlayerEvents.PLAYER_LOGGED_OUT.register((level, player) -> {
			VoidEntityEvents.onPlayerLogout(level, player);
		});

		CollectivePlayerEvents.ON_ITEM_PICKED_UP.register((level, player, itemStack) -> {
			VoidEntityEvents.onItemPickup(level, player, itemStack);
		});

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			return VoidEntityEvents.onEntityInteract(player, world, hand, entity, hitResult);
		});

		// VoidLivingEntityEvents
		CollectiveEntityEvents.LIVING_TICK.register((level, entity) -> {
			VoidLivingEntityEvents.onLivingEntityTick(level, (LivingEntity) entity);
		});

		CollectiveEntityEvents.ON_LIVING_ATTACK.register((level, entity, damageSource, damageAmount) -> {
			return VoidLivingEntityEvents.onEntityDamage(level, entity, damageSource, damageAmount);
		});

		ServerLivingEntityEvents.ALLOW_DEATH.register((LivingEntity livingEntity, DamageSource damageSource, float damageAmount) -> {
			if (livingEntity instanceof ServerPlayer) {
				VoidLivingEntityEvents.onPlayerDeath((ServerPlayer)livingEntity, damageSource, damageAmount);
			}
			return true;
		});

		// VoidUtilEvents
		ServerLifecycleEvents.SERVER_STOPPING.register((MinecraftServer server) -> {
			VoidUtilEvents.onServerShuttingdown(server);
		});

		// Commands
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			CommandVoidVanguards.register(dispatcher);
		});
	}

	private static void setGlobalConstants() {

	}
}
