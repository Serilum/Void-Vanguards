package com.serilum.voidvanguards.util;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.BlockPosFunctions;
import com.natamus.collective.functions.TaskFunctions;
import com.natamus.collective.services.Services;
import com.serilum.voidvanguards.data.Constants;
import com.serilum.voidvanguards.data.ServerSaveData;
import com.serilum.voidvanguards.registry.objects.CosmosDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

public class Util {
	public static ServerLevel getCosmosLevel(MinecraftServer minecraftServer) {
		return minecraftServer.getLevel(CosmosDimensions.COSMOS_LEVEL);
	}

	public static boolean isCosmosDimension(ServerLevel serverLevel) {
		return serverLevel.equals(getCosmosLevel(serverLevel.getServer()));
	}

	public static boolean isInCosmosDimension(ServerPlayer serverPlayer) {
		return isCosmosDimension(serverPlayer.level());
	}

	public static boolean teleportPlayerToOriginalPosition(ServerPlayer serverPlayer) {
		if (!isInCosmosDimension(serverPlayer)) {
			return false;
		}

		UUID playerUUID = serverPlayer.getUUID();
		if (!ServerSaveData.get().originalPlayerPositions.containsKey(playerUUID)) {
			SaveLoadUtils.loadPlayerData(serverPlayer);

			if (!ServerSaveData.get().originalPlayerPositions.containsKey(playerUUID)) {
				return false;
			}
		}

		Pair<ResourceKey<Level>, BlockPos> originalPositionPair = ServerSaveData.get().originalPlayerPositions.get(playerUUID);

		Services.TELEPORT.teleportEntity(serverPlayer, originalPositionPair.getFirst(), originalPositionPair.getSecond());
		return true;
	}

	public static BlockPos getRANDOMVoidbornCosmosGateCoordinates(ServerLevel serverLevel, ServerPlayer serverPlayer) {
		return BlockPosFunctions.getRandomCoordinatesInNearestUngeneratedChunk(serverLevel, serverPlayer.blockPosition());
	}

	public static void addSendMessageTag(ServerPlayer serverPlayer, String message) {
		serverPlayer.entityTags().add(Constants.sendMessageTagPrefix + Constants.saveDataTagMainDelimiter + message);
	}

	public static void removeTag(ServerPlayer serverPlayer, String tag) {
		TaskFunctions.enqueueCollectiveServerTask(serverPlayer.level().getServer(), () -> {
			serverPlayer.entityTags().remove(tag);
		}, 0);
	}
	public static void removeTag(MinecraftServer minecraftServer, Entity entity, String tag) {
		TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
			entity.entityTags().remove(tag);
		}, 0);
	}

	public static void spawnLightning(ServerLevel serverLevel, BlockPos lightningPos, @Nullable ServerPlayer serverPlayer) {
		LightningBolt lightningbolt = EntityTypes.LIGHTNING_BOLT.create(serverLevel, EntitySpawnReason.EVENT);
		lightningbolt.snapTo(Vec3.atBottomCenterOf(lightningPos));
		serverLevel.addFreshEntity(lightningbolt);

		if (serverPlayer != null) {
			serverPlayer.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 5.0F, 1.0F);
		}
	}

	// Schematics
	public static InputStream getSchematicsInputStream(MinecraftServer minecraftServer, String schematicName) {
		return getSchematicsInputStream(minecraftServer, schematicName, ".schem");
	}

	public static InputStream getSchematicsInputStream(MinecraftServer minecraftServer, String schematicName, String fileExtension) {
		try {
			Optional<Resource> resourceOptional = minecraftServer.getResourceManager().getResource(Identifier.parse(Reference.MOD_ID + ":schematics/" + schematicName + fileExtension));
			if (resourceOptional.isPresent()) {
				Resource resource = resourceOptional.get();
				return resource.open();
			}
		} catch (IOException ignored) {
		}
		return null;
	}

	// Date
	public static String getCurrentDateString() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy - MM - dd"));
	}

	public static String getCurrentYearString() {
		return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
	}
}
