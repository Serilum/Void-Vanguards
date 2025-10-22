package com.natamus.voidvanguards.registry.identifier;

import net.minecraft.network.chat.Component;

public record CosmosLocation(String id, Component displayName) {
	public static final CosmosLocation VANGUARD_STATION = new CosmosLocation("vanguard_station", Component.translatable("voidvanguards.vanguard.base"));
	public static final CosmosLocation VOIDBORN_BASE = new CosmosLocation("voidborn_base", Component.translatable("voidvanguards.voidborn.base"));
}
