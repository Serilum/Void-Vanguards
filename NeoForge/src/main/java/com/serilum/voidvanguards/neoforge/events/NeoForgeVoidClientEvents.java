package com.serilum.voidvanguards.neoforge.events;

import com.serilum.voidvanguards.events.VoidClientEvents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class NeoForgeVoidClientEvents {
	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post e) {
		VoidClientEvents.onClientTick();
	}

	@SubscribeEvent
	public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut e) {
		VoidClientEvents.onClientLogout();
	}
}
