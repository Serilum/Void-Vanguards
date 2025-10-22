package com.natamus.voidvanguards.forge.events;

import com.natamus.voidvanguards.cmd.CommandVoidVanguards;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeRegisterCommandsEvent {
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent e) {
		CommandVoidVanguards.register(e.getDispatcher());
	}
}
