package com.serilum.voidvanguards.neoforge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.serilum.voidvanguards.events.VoidBlockEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

public class NeoForgeVoidBlockEvents {
	@SubscribeEvent
	public static void onBlockBreak(BreakBlockEvent e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return;
		}

		if (!VoidBlockEvents.onBlockBreak(level, e.getPlayer(), e.getPos(), e.getState(), null)) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onBlockPlace(BlockEvent.EntityPlaceEvent e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return;
		}

		Entity entity = e.getEntity();
		if (!(entity instanceof LivingEntity)) {
			return;
		}

		if (!VoidBlockEvents.onBlockPlace(level, e.getPos(), e.getPlacedBlock(), (LivingEntity)entity, null)) {
			e.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onAnvilClick(PlayerInteractEvent.RightClickBlock e) {
		if (!VoidBlockEvents.onRightClickBlock(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec()).equals(InteractionResult.PASS)) {
			e.setCanceled(true);
		}
	}
}
