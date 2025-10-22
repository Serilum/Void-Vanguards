package com.natamus.voidvanguards.neoforge.events;

import com.natamus.voidvanguards.events.VoidLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

public class NeoForgeVoidLivingEntityEvents {
	@SubscribeEvent
	public static void onLivingTick(EntityTickEvent.Pre e) {
		if (e.getEntity() instanceof LivingEntity livingEntity) {
			VoidLivingEntityEvents.onLivingEntityTick(livingEntity.level(), livingEntity);
		}
	}

	@SubscribeEvent
	public static void onEntityDamage(LivingIncomingDamageEvent e) {
		Entity entity = e.getEntity();
		if (!VoidLivingEntityEvents.onEntityDamage(entity.level(), entity, e.getSource(), e.getAmount())) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent e) {
		Entity entity = e.getEntity();
		if (entity.level().isClientSide) {
			return;
		}

		if (!(entity instanceof Player)) {
			return;
		}

		VoidLivingEntityEvents.onPlayerDeath((ServerPlayer)entity, e.getSource(), 0);
	}
}
