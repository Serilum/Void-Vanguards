package com.natamus.voidvanguards.parts.voidborn.util;

import com.mojang.datafixers.util.Pair;
import com.natamus.collective.functions.TaskFunctions;
import com.natamus.voidvanguards.data.HeadData;
import com.natamus.voidvanguards.parts.cosmos.util.CosmosDimensionUtil;
import com.natamus.voidvanguards.parts.vanguard.util.VanguardUtil;
import com.natamus.voidvanguards.parts.voidborn.data.VoidbornConstants;
import com.natamus.voidvanguards.registry.identifier.CosmosLocation;
import com.natamus.voidvanguards.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class VoidbornUtil {
	public static void replaceSignWithVoidbornMob(ServerLevel serverLevel, BlockPos blockPos, Registry<Enchantment> enchantmentRegistry) {
		Mob voidbornMob = getVoidbornMob(serverLevel, blockPos, enchantmentRegistry, false);

		serverLevel.addFreshEntity(voidbornMob);

		serverLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
	}

	public static Mob getVoidbornMob(ServerLevel serverLevel, BlockPos blockPos, Registry<Enchantment> enchantmentRegistry, boolean isLeader) {
		Pair<EntityType<?>, Mob> voidbornMobPair = VoidbornUtil.getRandomVoidbornMob(serverLevel);
		return getVoidbornMob(serverLevel, blockPos, enchantmentRegistry, isLeader, voidbornMobPair.getFirst(), voidbornMobPair.getSecond());
	}
	public static Mob getVoidbornMob(ServerLevel serverLevel, BlockPos blockPos, Registry<Enchantment> enchantmentRegistry, boolean isLeader, EntityType<?> voidbornMobEntityType, Mob voidbornMob) {
		voidbornMob.setPos(blockPos.getX()+0.5, blockPos.getY(), blockPos.getZ()+0.5);

		if (isLeader) {
			voidbornMob.setCustomName(Component.translatable("voidvanguards.voidborn.name.leader").withStyle(ChatFormatting.DARK_PURPLE));
			voidbornMob.setItemSlot(EquipmentSlot.HEAD, HeadData.getGeneratedHead(HeadData.HeadType.VOIDBORN_LEADER));
		}
		else {
			voidbornMob.setCustomName(Component.translatable("voidvanguards.voidborn.name.recruit"));
			voidbornMob.setItemSlot(EquipmentSlot.HEAD, HeadData.getGeneratedHead(HeadData.HeadType.VOIDBORN));
		}

		// Weapon
		Item weaponItem = VoidbornUtil.getRandomVoidbornMobWeapon(voidbornMobEntityType);
		ItemStack weaponStack = new ItemStack(weaponItem);

		weaponStack.enchant(enchantmentRegistry.wrapAsHolder(enchantmentRegistry.get(Enchantments.INFINITY)), 1);

		voidbornMob.setItemSlot(EquipmentSlot.MAINHAND, weaponStack);

		voidbornMob.getTags().add(VoidbornConstants.voidbornTag);

		voidbornMob.setPersistenceRequired();
		voidbornMob.setCanPickUpLoot(false);

		return voidbornMob;
	}

	public static Mob getVoidbornLeader(ServerLevel serverLevel, BlockPos blockPos) {
		EntityType<?> leaderVoidbornType = EntityType.EVOKER;

		Mob voidbornLeader = VoidbornUtil.getVoidbornMob(serverLevel, blockPos, serverLevel.registryAccess().registryOrThrow(Registries.ENCHANTMENT), true, leaderVoidbornType, (Mob)leaderVoidbornType.create(serverLevel));

		voidbornLeader.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.0D);
		voidbornLeader.addTag(VoidbornConstants.voidbornLeaderTag);

		return voidbornLeader;
	}
	public static Mob spawnVoidbornLeader(ServerLevel serverLevel, BlockPos blockPos) {
		Mob voidbornLeader = getVoidbornLeader(serverLevel, blockPos);

		serverLevel.addFreshEntity(voidbornLeader);

		VanguardUtil.spawnTeleportParticles(serverLevel, blockPos);

		return voidbornLeader;
	}

	public static Pair<EntityType<?>, Mob> getRandomVoidbornMob(ServerLevel serverLevel) {
		EntityType<?> randomType = VoidbornConstants.possibleEnemyMobVariants.get(
			serverLevel.getRandom().nextInt(VoidbornConstants.possibleEnemyMobVariants.size())
		);

		return Pair.of(randomType, (Mob)randomType.create(serverLevel));
	}

	public static Item getRandomVoidbornMobWeapon(EntityType<?> entityType) {
		List<Item> weapons = VoidbornConstants.possibleWeapons.get(entityType);
		if (weapons == null || weapons.isEmpty()) {
			return Items.AIR;
		}
		return weapons.get((int)(Math.random() * weapons.size()));
	}

	public static void fillChestWithItems(ServerLevel serverLevel, BlockEntity blockEntity, BlockPos blockPos, BlockState blockState, Registry<Enchantment> enchantmentRegistry) {
		if (blockEntity instanceof ChestBlockEntity chestBlockEntity) {
			DataComponentMap.Builder dataComponentMapBuilder = DataComponentMap.builder();
			dataComponentMapBuilder.set(DataComponents.CUSTOM_NAME, Component.translatable("voidvanguards.voidborn.chest.title"));
			chestBlockEntity.setComponents(dataComponentMapBuilder.build());

			int chestSize = chestBlockEntity.getContainerSize();
			List<Integer> chestSlotRange = new ArrayList<>(IntStream.range(0, chestSize).boxed().toList());
			Collections.shuffle(chestSlotRange);

			int slotIndex = 0;

			Holder<Enchantment> protectionEnchantmentHolder = enchantmentRegistry.wrapAsHolder(enchantmentRegistry.get(Enchantments.PROTECTION));
			Holder<Enchantment> sharpnessEnchantmentHolder = enchantmentRegistry.wrapAsHolder(enchantmentRegistry.get(Enchantments.SHARPNESS));

			Item[] armorItems = {
				Items.IRON_HELMET,
				Items.IRON_CHESTPLATE,
				Items.IRON_LEGGINGS,
				Items.IRON_BOOTS
			};

			for (Item armor : armorItems) {
				if (slotIndex >= chestSlotRange.size()) break;

				ItemStack armourStack = new ItemStack(armor);
				armourStack.enchant(protectionEnchantmentHolder, 1);
				chestBlockEntity.setItem(chestSlotRange.get(slotIndex++), armourStack);
			}

			Item[] weaponItems = {
				Items.IRON_SWORD,
				Items.IRON_AXE
			};

			for (Item weapon : weaponItems) {
				if (slotIndex >= chestSlotRange.size()) break;

				ItemStack weaponStack = new ItemStack(weapon);
				weaponStack.enchant(sharpnessEnchantmentHolder, 1);
				chestBlockEntity.setItem(chestSlotRange.get(slotIndex++), weaponStack);
			}

			if (slotIndex < chestSlotRange.size()) {
				chestBlockEntity.setItem(chestSlotRange.get(slotIndex++), new ItemStack(Items.SHIELD));
			}

			Item[] foodItems = {
				Items.BREAD,
				Items.COOKED_CHICKEN,
				Items.COOKED_BEEF
			};

			RandomSource random = serverLevel.random;

			while (slotIndex < chestSlotRange.size()) {
				Item randomFood = foodItems[random.nextInt(foodItems.length)];
				int count = 1 + random.nextInt(4);
				chestBlockEntity.setItem(chestSlotRange.get(slotIndex++), new ItemStack(randomFood, count));
			}

			chestBlockEntity.setChanged();
			serverLevel.sendBlockUpdated(blockPos, blockState, blockState, 3);
		}
	}

	public static BlockPos getVoidbornBaseSpawnPos(ServerPlayer serverPlayer) {
		return getVoidbornBaseSpawnPos(serverPlayer, CosmosDimensionUtil.getUniqueBlockPositionFromUUID(serverPlayer.getUUID(), CosmosLocation.VOIDBORN_BASE));
	}
	public static BlockPos getVoidbornBaseSpawnPos(ServerPlayer serverPlayer, BlockPos voidbornCosmosBasePos) {
		return voidbornCosmosBasePos.offset(15, 0, 0);
	}

	public static BlockPos getTeleportBackButtonPosition(ServerPlayer serverPlayer) {
		return getTeleportBackButtonPosition(serverPlayer, CosmosDimensionUtil.getUniqueBlockPositionFromUUID(serverPlayer.getUUID(), CosmosLocation.VOIDBORN_BASE));
	}
	public static BlockPos getTeleportBackButtonPosition(ServerPlayer serverPlayer, BlockPos voidbornCosmosBasePos) {
		return getVoidbornBaseSpawnPos(serverPlayer, voidbornCosmosBasePos).offset(3, 1, 0);
	}
	public static boolean isBlockPosTeleportBackButtonPosition(ServerPlayer serverPlayer, BlockPos blockPos) {
		return blockPos.equals(getTeleportBackButtonPosition(serverPlayer));
	}

	public static BlockPos getRadarLeverPosition(ServerPlayer serverPlayer) {
		return CosmosDimensionUtil.getUniqueBlockPositionFromUUID(serverPlayer.getUUID(), CosmosLocation.VOIDBORN_BASE).offset(-28, 8, -37);
	}
	public static boolean isBlockPosRadarLeverPosition(ServerPlayer serverPlayer, BlockPos blockPos) {
		return blockPos.equals(getRadarLeverPosition(serverPlayer));
	}

	public static List<BlockPos> getRadarEndRodPositions(ServerPlayer serverPlayer) {
		BlockPos middlePos = CosmosDimensionUtil.getUniqueBlockPositionFromUUID(serverPlayer.getUUID(), CosmosLocation.VOIDBORN_BASE).offset(-26, 12, -37);

		return Arrays.asList(middlePos.offset(0, 1, 0), middlePos.offset(-1, 0, -1), middlePos.offset(-1, 0, 1), middlePos.offset(1, 0, -1), middlePos.offset(1, 0, 1));
	}
	public static void spawnLightningAtRadarEndRods(ServerPlayer serverPlayer) {
		MinecraftServer minecraftServer = serverPlayer.getServer();

		for (int delay = 0; delay <= 50; delay+=10) {
			TaskFunctions.enqueueCollectiveServerTask(minecraftServer, () -> {
				for (BlockPos endRodPos : getRadarEndRodPositions(serverPlayer)) {
					Util.spawnLightning(serverPlayer.serverLevel(), endRodPos, serverPlayer);
				}
			}, delay);
		}
	}

	public static boolean shouldMakeAmbientSound(Level level) {
		return level.random.nextInt(5) == 0;
	}
}
