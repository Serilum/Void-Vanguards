package com.serilum.voidvanguards.forge.events;

import com.serilum.voidvanguards.events.VoidUtilEvents;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeVoidUtilEvents {
	@SubscribeEvent
	public static void onServerShuttingdown(ServerStoppingEvent e) {
		VoidUtilEvents.onServerShuttingdown(e.getServer());
	}
}
