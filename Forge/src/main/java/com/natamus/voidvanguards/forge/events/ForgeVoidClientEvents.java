package com.natamus.voidvanguards.forge.events;

import com.natamus.voidvanguards.events.VoidClientEvents;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeVoidClientEvents {
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent e) {
		if (!e.phase.equals(Phase.END)) {
			return;
		}

		VoidClientEvents.onClientTick();
	}

	@SubscribeEvent
	public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut e) {
		VoidClientEvents.onClientLogout();
	}
}
