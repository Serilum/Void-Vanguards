package com.serilum.voidvanguards.parts.voidborn.data;

import com.serilum.voidvanguards.util.Reference;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VoidbornConstants {
	public static final String voidbornTag = Reference.MOD_ID + ".voidborn";
	public static final String voidbornLeaderTag = Reference.MOD_ID + ".voidborn_leader";
	public static final String voidbornRadarSignTag = Reference.MOD_ID + ".radarSign";

	public static List<EntityType<?>> possibleEnemyMobVariants = Arrays.asList(EntityTypes.PILLAGER, EntityTypes.VINDICATOR); //, EntityTypes.EVOKER);

	public static HashMap<EntityType<?>, List<Item>> possibleWeapons = new HashMap<>() {{
		put(EntityTypes.PILLAGER, List.of(Items.CROSSBOW));
		put(EntityTypes.VINDICATOR, List.of(Items.IRON_AXE, Items.IRON_HOE, Items.IRON_SHOVEL));
		put(EntityTypes.EVOKER, List.of(Items.STICK));
	}};

	public static HashMap<EntityType<?>, Float> voidbornMovementSpeeds = new HashMap<>() {{
		put(EntityTypes.PILLAGER, 0.35F);
		put(EntityTypes.VINDICATOR, 0.35F);
		put(EntityTypes.EVOKER, 0.5F);
	}};

	public static final List<Item> possibleIllagerDrops = List.of(Items.TOTEM_OF_UNDYING, Items.EMERALD, Items.CROSSBOW, Items.ENCHANTED_BOOK, Items.IRON_AXE, Items.IRON_SWORD, Items.IRON_HELMET, Items.IRON_SHOVEL, Items.IRON_PICKAXE, Items.STICK, Items.PLAYER_HEAD);

	public static int voidbornBaseMobCountStoryTrigger = 25;
}
