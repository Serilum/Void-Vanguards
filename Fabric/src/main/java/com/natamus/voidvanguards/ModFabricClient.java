package com.natamus.voidvanguards;

import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.voidvanguards.events.VoidClientEvents;
import com.natamus.voidvanguards.util.Reference;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class ModFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		ModCommon.registerPackets();

		registerEvents();
	}

	private void registerEvents() {
		ClientTickEvents.END_CLIENT_TICK.register((mc) -> {
			VoidClientEvents.onClientTick();
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> {
			VoidClientEvents.onClientLogout();
		});
	}
}
