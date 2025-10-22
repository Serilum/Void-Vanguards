package com.natamus.voidvanguards.registry.objects;

import com.natamus.voidvanguards.util.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

public class CosmosDimensions {
	public static final ResourceKey<DimensionType> COSMOS_DIMENSION_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "cosmos"));
	;
	public static final ResourceKey<Biome> COSMOS_BIOME = ResourceKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "cosmos"));
	;
	public static final ResourceKey<Level> COSMOS_LEVEL = ResourceKey.create(Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "cosmos"));
	;
}
