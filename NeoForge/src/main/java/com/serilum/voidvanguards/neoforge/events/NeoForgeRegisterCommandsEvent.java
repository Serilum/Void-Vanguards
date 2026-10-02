package com.serilum.voidvanguards.neoforge.events;

import com.serilum.voidvanguards.cmd.CommandVoidVanguards;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class NeoForgeRegisterCommandsEvent {
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent e) {
		CommandVoidVanguards.register(e.getDispatcher());
	}
}
