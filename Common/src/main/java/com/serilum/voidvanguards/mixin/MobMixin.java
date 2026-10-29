package com.serilum.voidvanguards.mixin;

import com.natamus.collective.functions.TaskFunctions;
import com.serilum.voidvanguards.data.HeadData;
import com.serilum.voidvanguards.parts.voidborn.data.VoidbornConstants;
import com.serilum.voidvanguards.util.Reference;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Mob.class, priority = 1001)
public class MobMixin {
	@Inject(method = "dropCustomDeathLoot(Lnet/minecraft/world/damagesource/DamageSource;IZ)V", at = @At(value = "HEAD"))
	protected void dropCustomDeathLoot(DamageSource damageSource, int looting, boolean b, CallbackInfo ci) {
		Mob mob = (Mob)(Object)this;
		if (!mob.getTags().contains(VoidbornConstants.voidbornTag)) {
			return;
		}

		if (!(mob.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		TaskFunctions.enqueueCollectiveServerTask(serverLevel.getServer(), () -> {
			serverLevel.getEntitiesOfClass(
				net.minecraft.world.entity.item.ItemEntity.class,
				mob.getBoundingBox().inflate(3.0D),
				itemEntity -> true
			).forEach(itemEntity -> {
				if (VoidbornConstants.possibleIllagerDrops.contains(itemEntity.getItem().getItem())) {
					itemEntity.discard(); // safely removes the entity
				}
			});

			ItemStack voidbornHeadStack = HeadData.getGeneratedHead(HeadData.HeadType.VOIDBORN);
			serverLevel.addFreshEntity(new ItemEntity(serverLevel, mob.getX(), mob.getY(), mob.getZ(), voidbornHeadStack));
		}, 0);
	}

	@Inject(method = "getTarget()Lnet/minecraft/world/entity/LivingEntity;", at = @At(value = "HEAD"), cancellable = true)
	public void getTarget(CallbackInfoReturnable<LivingEntity> cir) {
		if (((Mob)(Object)this).getTags().contains(Reference.MOD_ID + ".notarget")) {
			cir.setReturnValue(null);
		}
	}
}