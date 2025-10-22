package com.natamus.voidvanguards.forge.events;

import com.natamus.voidvanguards.events.VoidEntityEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeVoidEntityEvents {
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
    public static void onItemPickup(EntityItemPickupEvent e) {
        Player player = e.getEntity();
        VoidEntityEvents.onItemPickup(player.level(), player, e.getItem().getItem());
    }

	@SubscribeEvent
	public static void onBoatClick(PlayerInteractEvent.EntityInteract e) {
		if (VoidEntityEvents.onEntityInteract(e.getEntity(), e.getLevel(), e.getHand(), e.getTarget(), null).equals(InteractionResult.SUCCESS)) {
			e.setCanceled(true);
		}
	}
}
