package com.serilum.voidvanguards.forge.events;

import com.serilum.voidvanguards.events.VoidLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeVoidLivingEntityEvents {
	public static void registerEventsInBus() {
		BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeVoidLivingEntityEvents.class);
	}

	@SubscribeEvent
	public static void onLivingTick(LivingEvent.LivingTickEvent e) {
		LivingEntity livingEntity = e.getEntity();
		VoidLivingEntityEvents.onLivingEntityTick(livingEntity.level(), livingEntity);
	}

	@SubscribeEvent
	public static boolean onEntityDamage(LivingAttackEvent e) {
		Entity entity = e.getEntity();
		return !VoidLivingEntityEvents.onEntityDamage(entity.level(), entity, e.getSource(), e.getAmount());
	}

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent e) {
		Entity entity = e.getEntity();
		if (entity.level().isClientSide()) {
			return;
		}

		if (!(entity instanceof Player)) {
			return;
		}

		VoidLivingEntityEvents.onPlayerDeath((ServerPlayer)entity, e.getSource(), 0);
	}
}
