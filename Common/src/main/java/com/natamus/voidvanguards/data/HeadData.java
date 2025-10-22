package com.natamus.voidvanguards.data;

import com.mojang.authlib.GameProfile;
import com.natamus.collective.functions.HeadFunctions;
import com.natamus.collective.functions.StringFunctions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HeadData {

	public enum HeadType {
		VANGUARD("vanguard_helmet", "1dcdacdf-8419-4b2c-84ed-9faf9742f650", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGJmMWJlZmRmMTUyZGJiNzY4YTE1OTMxNWE5Y2YzYTM4YzFjYTk0ZWJhZTVkY2U4YmNiMDVhNDFkNWRjM2U4ZiJ9fX0="),
		VANGUARD_LEADER("vanguard_leader_helmet", "ba002e9d-0f53-4cb4-8583-be839c0c8353", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzAyYTI4YmYwMzU4MGQ3MzcxZjFiOWM4YzYzMTQwMmE5NGI1OGZkMWJiZDIyNmQxMjk5NWNiYTc5MTIwM2FjIn19fQ=="),
		VOIDBORN("voidborn_helmet", "dc0d2b70-8165-4fc6-975d-99456d2162dd", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2U3ZWI0OWI1OGQyNzUyYzBiZWI4NzBkOGE3OTdlMTVhNzU1MzMwMWZkOGY4NWZmMjNlYTU0N2QyYTNmYjk2NSJ9fX0="),
		VOIDBORN_LEADER("voidborn_leader_helmet", "dc0d2b70-8165-4fc6-975d-99456d2162dd", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjg2NmFjOTM5MmMxODc2MWYzZDhkZDhkZTBkMGZmYmVkZTk3ZDdlYTcwMjg2NjRlMDdkNmI3M2RlNGU0NDdjZSJ9fX0=");

		public final String name;
		public final String uuid;
		public final String texture;
		public ItemStack headStack;

		HeadType(String name, String uuid, String texture) {
			this.name = name;
			this.uuid = uuid;
			this.texture = texture;
		}

		public void generate() {
			this.headStack = HeadFunctions.getNewTexturedHead(name, texture, uuid, "", 1);
			this.headStack.set(DataComponents.CUSTOM_NAME, Component.literal(StringFunctions.capitalizeEveryWord(name.replace("_", " "))));
		}
	}

	private static final Map<String, ItemStack> tempMap = new HashMap<>();
	public static Map<String, ItemStack> headMap = Collections.emptyMap();

	public static void generateHeadData() {
		for (HeadType type : HeadType.values()) {
			type.generate();
			tempMap.put(type.name, type.headStack);
		}

		headMap = Collections.unmodifiableMap(tempMap);
	}

	public static ItemStack getGeneratedHead(String name) {
		return headMap.get(name).copy();
	}
	public static ItemStack getGeneratedHead(HeadType headType) {
		return headType.headStack.copy();
	}

	public static @Nullable GameProfile getHeadGameProfile(String name) {
		for (HeadType type : HeadType.values()) {
			if (type.name.equals(name)) {
				return HeadFunctions.getTexturedHeadGameProfile(name, type.texture, type.uuid);
			}
		}
		return null;
	}
	public static @Nullable GameProfile getHeadGameProfile(HeadType headType) {
		return HeadFunctions.getTexturedHeadGameProfile(headType.name, headType.texture, headType.uuid);
	}
}
