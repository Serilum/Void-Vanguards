package com.serilum.voidvanguards.forge.events;

import com.serilum.voidvanguards.events.VoidClientEvents;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeVoidClientEvents {
	public static void registerEventsInBus() {
		BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeVoidClientEvents.class);
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post e) {
		VoidClientEvents.onClientTick();
	}

	@SubscribeEvent
	public static void onClientLogout(ClientPlayerNetworkEvent.LoggingOut e) {
		VoidClientEvents.onClientLogout();
	}
}
