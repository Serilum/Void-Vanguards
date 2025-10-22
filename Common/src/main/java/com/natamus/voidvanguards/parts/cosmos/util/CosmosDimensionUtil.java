package com.natamus.voidvanguards.parts.cosmos.util;

import com.natamus.voidvanguards.parts.cosmos.data.CosmosDimensionVariables;
import com.natamus.voidvanguards.registry.identifier.CosmosLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public class CosmosDimensionUtil {
	public static BlockPos getUniqueBlockPositionFromUUID(UUID uuid, CosmosLocation cosmosLocation) {
		long most = uuid.getMostSignificantBits();
		long least = uuid.getLeastSignificantBits();

		long mixed = most ^ least;

		int x = (int) ((mixed & 0xFFFFFFF) % CosmosDimensionVariables.cosmosMaxCoordinateLimit);
		int z = (int) (((mixed >> 28) & 0xFFFFFFF) % CosmosDimensionVariables.cosmosMaxCoordinateLimit);

		if (cosmosLocation.equals(CosmosLocation.VOIDBORN_BASE)) {
			x = -x;
			z = -z;
		}

		return new BlockPos(x, 100, z);
	}

	public static boolean playerIsInVanguardStation(ServerPlayer serverPlayer) {
		return getPlayerCosmosLocation(serverPlayer).equals(CosmosLocation.VANGUARD_STATION);
	}
	public static boolean playerIsInVoidbornBase(ServerPlayer serverPlayer) {
		return getPlayerCosmosLocation(serverPlayer).equals(CosmosLocation.VOIDBORN_BASE);
	}

	public static CosmosLocation getPlayerCosmosLocation(ServerPlayer serverPlayer) {
		return (serverPlayer.getX() >= 0 && serverPlayer.getZ() >= 0)
			? CosmosLocation.VANGUARD_STATION
			: CosmosLocation.VOIDBORN_BASE;
	}
}
