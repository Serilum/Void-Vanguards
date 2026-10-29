package com.serilum.voidvanguards.forge.events;

import com.serilum.voidvanguards.cmd.CommandVoidVanguards;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeRegisterCommandsEvent {
	public static void registerEventsInBus() {
		// BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeRegisterCommandsEvent.class);

		RegisterCommandsEvent.BUS.addListener(ForgeRegisterCommandsEvent::registerCommands);
	}

	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent e) {
		CommandVoidVanguards.register(e.getDispatcher());
	}
}
