package com.serilum.voidvanguards.mixin;

import com.serilum.voidvanguards.util.Reference;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemFrame.class, priority = 1001)
public class ItemFrameMixin {
	@Inject(method = "dropItem(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;Z)V", at = @At(value = "HEAD"), cancellable = true)
	public void dropItem(ServerLevel serverLevel, @Nullable Entity entity, boolean b, CallbackInfo ci) {
		ItemFrame itemFrame = (ItemFrame)(Object)this;
		if (itemFrame.entityTags().contains(Reference.MOD_ID + ".tempframe")) {
			ItemEntity droppedItemEntity = itemFrame.spawnAtLocation(serverLevel, itemFrame.getItem());
			if (droppedItemEntity != null) {
				droppedItemEntity.setUnlimitedLifetime();
				droppedItemEntity.setPickUpDelay(0);
			}

			ci.cancel();
		}
	}

	@Inject(method = "survives()Z", at = @At(value = "HEAD"), cancellable = true)
	public void survives(CallbackInfoReturnable<Boolean> cir) {
		ItemFrame itemFrame = (ItemFrame)(Object)this;
		if (itemFrame.entityTags().contains(Reference.MOD_ID + ".tempframe")) {
			cir.setReturnValue(false);
		}
	}
}