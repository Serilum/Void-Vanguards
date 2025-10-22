package com.natamus.voidvanguards.forge.events;

import com.natamus.voidvanguards.events.VoidUtilEvents;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeVoidUtilEvents {
	@SubscribeEvent
	public static void onServerShuttingdown(ServerStoppingEvent e) {
		VoidUtilEvents.onServerShuttingdown(e.getServer());
	}
}
