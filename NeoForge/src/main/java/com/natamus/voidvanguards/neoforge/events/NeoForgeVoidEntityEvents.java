package com.natamus.voidvanguards.neoforge.events;

import com.natamus.voidvanguards.events.VoidEntityEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class NeoForgeVoidEntityEvents {
	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent e) {
		VoidEntityEvents.onEntityJoin(e.getLevel(), e.getEntity());
	}

	@SubscribeEvent
	public static void onEntityLeave(EntityLeaveLevelEvent e) {
		VoidEntityEvents.onEntityLeave(e.getLevel(), e.getEntity());
	}

	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent e) {
		Player player = e.getEntity();
		VoidEntityEvents.onPlayerLogin(player.level(), player);
	}

	@SubscribeEvent
	public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent e) {
		Player player = e.getEntity();
		VoidEntityEvents.onPlayerLogout(player.level(), player);
	}

	@SubscribeEvent
	public static void onItemPickup(ItemEntityPickupEvent.Post e) {
		Player player = e.getPlayer();
		VoidEntityEvents.onItemPickup(player.level(), player, e.getItemEntity().getItem());
	}

	@SubscribeEvent
	public static void onBoatClick(PlayerInteractEvent.EntityInteract e) {
		if (VoidEntityEvents.onEntityInteract(e.getEntity(), e.getLevel(), e.getHand(), e.getTarget(), null).equals(InteractionResult.SUCCESS)) {
			e.setCanceled(true);
		}
	}
}
