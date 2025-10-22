package com.natamus.voidvanguards.neoforge.events;

import com.natamus.voidvanguards.events.VoidUtilEvents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

public class NeoForgeVoidUtilEvents {
	@SubscribeEvent
	public static void onServerShuttingdown(ServerStoppingEvent e) {
		VoidUtilEvents.onServerShuttingdown(e.getServer());
	}
}
