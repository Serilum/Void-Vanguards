package com.natamus.voidvanguards;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.voidvanguards.neoforge.config.IntegrateNeoForgeConfig;
import com.natamus.voidvanguards.neoforge.events.*;
import com.natamus.voidvanguards.util.Reference;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Reference.MOD_ID)
public class ModNeoForge {

	public ModNeoForge(IEventBus modEventBus) {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		modEventBus.addListener(this::loadComplete);

		setGlobalConstants();
		ModCommon.init();
		ModCommon.registerAssets(modEventBus);

		IntegrateNeoForgeConfig.registerScreen(ModLoadingContext.get());

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		NeoForge.EVENT_BUS.register(NeoForgeRegisterCommandsEvent.class);

		NeoForge.EVENT_BUS.register(NeoForgeVoidBlockEvents.class);
		NeoForge.EVENT_BUS.register(NeoForgeVoidEntityEvents.class);
		NeoForge.EVENT_BUS.register(NeoForgeVoidLivingEntityEvents.class);
		NeoForge.EVENT_BUS.register(NeoForgeVoidUtilEvents.class);

		if (FMLEnvironment.dist.equals(Dist.CLIENT)) {
			NeoForge.EVENT_BUS.register(NeoForgeVoidClientEvents.class);
		}

		ModCommon.setAssets();
	}

	private static void setGlobalConstants() {

	}
}