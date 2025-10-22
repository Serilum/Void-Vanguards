package com.natamus.voidvanguards.mixin;

import com.natamus.voidvanguards.util.Reference;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = ItemFrame.class, priority = 1001)
public class ItemFrameMixin {
	@Inject(method = "dropItem(Lnet/minecraft/world/entity/Entity;Z)V", at = @At(value = "HEAD"), cancellable = true)
	public void dropItem(@Nullable Entity entity, boolean b, CallbackInfo ci) {
		ItemFrame itemFrame = (ItemFrame)(Object)this;
		if (itemFrame.getTags().contains(Reference.MOD_ID + ".tempframe")) {
			ItemEntity droppedItemEntity = itemFrame.spawnAtLocation(itemFrame.getItem());
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
		if (itemFrame.getTags().contains(Reference.MOD_ID + ".tempframe")) {
			cir.setReturnValue(false);
		}
	}
}