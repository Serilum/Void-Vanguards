package com.natamus.voidvanguards.neoforge.events;

import com.natamus.voidvanguards.cmd.CommandVoidVanguards;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class NeoForgeRegisterCommandsEvent {
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent e) {
		CommandVoidVanguards.register(e.getDispatcher());
	}
}
