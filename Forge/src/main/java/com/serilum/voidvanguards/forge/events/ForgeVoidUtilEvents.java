package com.serilum.voidvanguards.forge.events;

import com.serilum.voidvanguards.events.VoidUtilEvents;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeVoidUtilEvents {
	public static void registerEventsInBus() {
		// BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeVoidUtilEvents.class);

		ServerStoppingEvent.BUS.addListener(ForgeVoidUtilEvents::onServerShuttingdown);
	}

	@SubscribeEvent
	public static void onServerShuttingdown(ServerStoppingEvent e) {
		VoidUtilEvents.onServerShuttingdown(e.getServer());
	}
}
