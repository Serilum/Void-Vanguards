package com.serilum.voidvanguards;

import com.natamus.collective.functions.CreativeModeTabFunctions;
import com.natamus.collective.services.Services;
import com.serilum.voidvanguards.config.ConfigHandler;
import com.serilum.voidvanguards.data.HeadData;
import com.serilum.voidvanguards.networking.PacketRegistration;
import com.serilum.voidvanguards.registry.block.CosmosPortalBlock;
import com.serilum.voidvanguards.registry.item.OrbitalRadioItem;
import com.serilum.voidvanguards.registry.item.OrbitalTransponderItem;
import com.serilum.voidvanguards.registry.objects.VanguardBlocks;
import com.serilum.voidvanguards.registry.objects.VanguardItems;
import com.serilum.voidvanguards.util.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModCommon {

	public static void init() {
		ConfigHandler.initConfig();

		registerPackets();

		load();
	}

	private static void load() {
		HeadData.generateHeadData();
	}

	public static void registerPackets() {
		new PacketRegistration().init();
	}

	public static void registerAssets(Object modEventBusObject) {
		Services.REGISTERBLOCK.registerBlockWithoutItem(
				modEventBusObject,
				new ResourceLocation(Reference.MOD_ID, "cosmos_portal"),
				() -> new CosmosPortalBlock(
						BlockBehaviour.Properties.of().noCollission().randomTicks().strength(-1.0F).sound(SoundType.GLASS).lightLevel(($$0x) -> 11).pushReaction(PushReaction.BLOCK).mapColor(MapColor.COLOR_CYAN)),
				true
		);

		Services.REGISTERITEM.registerItem(
				modEventBusObject,
				new ResourceLocation(Reference.MOD_ID, "orbital_radio"),
				() -> new OrbitalRadioItem(new Item.Properties()),
				CreativeModeTabFunctions.getCreativeModeTabResourceKey("tools_and_utilities"),
				false
		);

		Services.REGISTERITEM.registerItem(
				modEventBusObject,
				new ResourceLocation(Reference.MOD_ID, "orbital_transponder"),
				() -> new OrbitalTransponderItem(new Item.Properties()),
				CreativeModeTabFunctions.getCreativeModeTabResourceKey("tools_and_utilities"),
				true
		);
	}

	public static void setAssets() {
		VanguardItems.ORBITAL_RADIO = (OrbitalRadioItem)Services.REGISTERITEM.getRegisteredItem(new ResourceLocation(Reference.MOD_ID, "orbital_radio"));
		VanguardItems.ORBITAL_TRANSPONDER = (OrbitalTransponderItem)Services.REGISTERITEM.getRegisteredItem(new ResourceLocation(Reference.MOD_ID, "orbital_transponder"));

		VanguardBlocks.COSMOS_PORTAL = (CosmosPortalBlock)Services.REGISTERBLOCK.getRegisteredBlockWithoutItem(new ResourceLocation(Reference.MOD_ID, "cosmos_portal"));
	}
}
