package com.natamus.voidvanguards;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.natamus.voidvanguards.forge.config.IntegrateForgeConfig;
import com.natamus.voidvanguards.forge.events.*;
import com.natamus.voidvanguards.util.Reference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Reference.MOD_ID)
public class ModForge {

	public ModForge() {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::loadComplete);

		setGlobalConstants();
		ModCommon.init();
		ModCommon.registerAssets(modEventBus);

		IntegrateForgeConfig.registerScreen(ModLoadingContext.get());

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		MinecraftForge.EVENT_BUS.register(ForgeRegisterCommandsEvent.class);

		MinecraftForge.EVENT_BUS.register(ForgeVoidBlockEvents.class);
		MinecraftForge.EVENT_BUS.register(ForgeVoidEntityEvents.class);
		MinecraftForge.EVENT_BUS.register(ForgeVoidLivingEntityEvents.class);
		MinecraftForge.EVENT_BUS.register(ForgeVoidUtilEvents.class);

		if (!FMLEnvironment.dist.equals(Dist.CLIENT)) {
			MinecraftForge.EVENT_BUS.register(ForgeVoidClientEvents.class);
		}

		ModCommon.setAssets();
	}

	private static void setGlobalConstants() {

	}
}