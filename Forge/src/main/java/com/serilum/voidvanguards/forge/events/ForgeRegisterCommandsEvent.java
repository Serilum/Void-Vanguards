package com.serilum.voidvanguards.forge.events;

import com.serilum.voidvanguards.cmd.CommandVoidVanguards;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeRegisterCommandsEvent {
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent e) {
		CommandVoidVanguards.register(e.getDispatcher());
	}
}
