package com.natamus.voidvanguards.config;

import com.natamus.collective.config.DuskConfig;
import com.natamus.voidvanguards.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry(min = 1, max = 1200)
	public static int ticksNeededForMoreSkyShipDetail = 40;

	@Entry
	public static boolean devSettingInstantConversations = false;

	public static void initConfig() {
		configMetaData.put("ticksNeededForMoreSkyShipDetail", Arrays.asList(
				"The amount of ticks it takes to get more detail of the orbiting sky ship when using a spyglass. 20 ticks = 1 second"
		));
		configMetaData.put("devSettingInstantConversations", Arrays.asList(
				"Developer setting. The delay in ticks between sentences spoken by NPCs are set to 1."
		));

		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}