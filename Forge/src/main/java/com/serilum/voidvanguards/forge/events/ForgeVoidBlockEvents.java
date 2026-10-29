package com.serilum.voidvanguards.forge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.serilum.voidvanguards.events.VoidBlockEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeVoidBlockEvents {
	public static void registerEventsInBus() {
		BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeVoidBlockEvents.class);
	}

	@SubscribeEvent
	public static boolean onBlockBreak(BlockEvent.BreakEvent e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return false;
		}

		return !VoidBlockEvents.onBlockBreak(level, e.getPlayer(), e.getPos(), e.getState(), null);
	}

	@SubscribeEvent
	public static boolean onBlockPlace(BlockEvent.EntityPlaceEvent e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return false;
		}

		Entity entity = e.getEntity();
		if (!(entity instanceof LivingEntity)) {
			return false;
		}

		return !VoidBlockEvents.onBlockPlace(level, e.getPos(), e.getPlacedBlock(), (LivingEntity)entity, null);
	}

	@SubscribeEvent
	public static boolean onAnvilClick(PlayerInteractEvent.RightClickBlock e) {
		return !VoidBlockEvents.onRightClickBlock(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec()).equals(InteractionResult.PASS);
	}
}
