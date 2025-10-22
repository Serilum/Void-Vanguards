package com.natamus.voidvanguards.registry.block;

import com.natamus.voidvanguards.data.Constants;
import com.natamus.voidvanguards.data.ServerSaveData;
import com.natamus.voidvanguards.parts.cosmos.functions.CosmosDimensionFunctions;
import com.natamus.voidvanguards.parts.voidborn.data.VoidbornConstants;
import com.natamus.voidvanguards.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class CosmosPortalBlock extends NetherPortalBlock {
	public CosmosPortalBlock(Properties properties) {
		super(properties);
		this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AXIS, Direction.Axis.X));
	}

	@Override
	protected void randomTick(@NotNull BlockState blockState, @NotNull ServerLevel serverLevel, @NotNull BlockPos blockPos, @NotNull RandomSource randomSource) {

	}

	@Override
    protected void entityInside(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull Entity entity) {
		if (level.isClientSide) {
			return;
		}

        if (!(entity instanceof ServerPlayer serverPlayer)) {
			return;
		}

		if (serverPlayer.getTags().contains(Constants.triggeredEntityInsideTag)) {
			return;
		}

		List<Mob> nearbyMobs = level.getEntitiesOfClass(Mob.class, new AABB(blockPos).inflate(16.0D), mob ->
			mob.getTags().contains(VoidbornConstants.voidbornTag)
		);

		if (!nearbyMobs.isEmpty()) {
			Util.addSendMessageTag(serverPlayer, Component.translatable("voidvanguards.message.voidborn.cosmos_gate.mobsAround").getString());
			return;
		}

		serverPlayer.getTags().add(Constants.triggeredEntityInsideTag);

		UUID playerUUID = serverPlayer.getUUID();
		if (ServerSaveData.get().answeredRadioPlayerUUIDS.contains(playerUUID) && !ServerSaveData.get().sabotagedVoidbornRadarPlayerUUIDS.contains(playerUUID)) {
			CosmosDimensionFunctions.teleportPlayerToVoidbornBase(serverPlayer.serverLevel(), serverPlayer);
		}
		else {
			Direction facing = serverPlayer.getDirection().getOpposite();
			BlockPos targetPos = serverPlayer.blockPosition().relative(facing, 2);

			// Optional: ensure safe teleport (e.g. not inside a wall)
			while (!level.getBlockState(targetPos).isAir() && targetPos.getY() < level.getMaxBuildHeight()) {
				targetPos = targetPos.above();
			}

			serverPlayer.teleportTo(
				(ServerLevel) level,
				targetPos.getX() + 0.5D,
				targetPos.getY(),
				targetPos.getZ() + 0.5D,
				serverPlayer.getYRot(),
				serverPlayer.getXRot()
			);

			Util.addSendMessageTag(serverPlayer, Component.translatable("voidvanguards.message.voidborn.cosmos_gate.enterUnable").getString());

			Util.removeTag(serverPlayer, Constants.triggeredEntityInsideTag);
		}
    }

	@Override
	protected @NotNull BlockState updateShape(@NotNull BlockState blockStateA, @NotNull Direction $$1, @NotNull BlockState blockStateB, @NotNull LevelAccessor levelAccessor, @NotNull BlockPos blockPosA, @NotNull BlockPos blockPosB) {
		return blockStateA;
	}

	@Override
    public void animateTick(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull RandomSource randomSource) {
        for(int $$4 = 0; $$4 < 4; ++$$4) {
            double $$5 = (double)blockPos.getX() + randomSource.nextDouble();
            double $$6 = (double)blockPos.getY() + randomSource.nextDouble();
            double $$7 = (double)blockPos.getZ() + randomSource.nextDouble();
            double $$8 = ((double)randomSource.nextFloat() - (double)0.5F) * (double)0.5F;
            double $$9 = ((double)randomSource.nextFloat() - (double)0.5F) * (double)0.5F;
            double $$10 = ((double)randomSource.nextFloat() - (double)0.5F) * (double)0.5F;
            int $$11 = randomSource.nextInt(2) * 2 - 1;
            if (!level.getBlockState(blockPos.west()).is(this) && !level.getBlockState(blockPos.east()).is(this)) {
                $$5 = (double)blockPos.getX() + (double)0.5F + (double)0.25F * (double)$$11;
                $$8 = (double)(randomSource.nextFloat() * 2.0F * (float)$$11);
            } else {
                $$7 = (double)blockPos.getZ() + (double)0.5F + (double)0.25F * (double)$$11;
                $$10 = (double)(randomSource.nextFloat() * 2.0F * (float)$$11);
            }

            level.addParticle(ParticleTypes.DRAGON_BREATH, $$5, $$6, $$7, $$8, $$9, $$10);
        }
    }
}
