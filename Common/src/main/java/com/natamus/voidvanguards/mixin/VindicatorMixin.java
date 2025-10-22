package com.natamus.voidvanguards.mixin;

import com.natamus.voidvanguards.parts.voidborn.data.VoidbornConstants;
import com.natamus.voidvanguards.parts.voidborn.util.VoidbornUtil;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.monster.Vindicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Vindicator.class, priority = 1001)
public class VindicatorMixin {
	@Inject(method = "getAmbientSound()Lnet/minecraft/sounds/SoundEvent;", at = @At(value = "HEAD"), cancellable = true)
    protected void getAmbientSound(CallbackInfoReturnable<SoundEvent> cir) {
		Vindicator vindicator = (Vindicator)(Object)this;
		if (vindicator.getTags().contains(VoidbornConstants.voidbornTag)) {
			if (VoidbornUtil.shouldMakeAmbientSound(vindicator.level())) {
				cir.setReturnValue(SoundEvents.WARDEN_HEARTBEAT);
			}
			else {
				cir.setReturnValue(null);
			}
		}
    }

	@Inject(method = "getDeathSound()Lnet/minecraft/sounds/SoundEvent;", at = @At(value = "HEAD"), cancellable = true)
    protected void getDeathSound(CallbackInfoReturnable<SoundEvent> cir) {
		if (((Vindicator)(Object)this).getTags().contains(VoidbornConstants.voidbornTag)) {
			cir.setReturnValue(SoundEvents.VILLAGER_HURT);
		}
    }

	/*@Inject(method = "getHurtSound(Lnet/minecraft/world/damagesource/DamageSource;)Lnet/minecraft/sounds/SoundEvent;", at = @At(value = "HEAD"), cancellable = true)
    protected void getHurtSound(DamageSource $$0, CallbackInfoReturnable<SoundEvent> cir) {
		if (((Vindicator)(Object)this).getTags().contains(VoidbornConstants.voidbornTag)) {
			cir.setReturnValue(SoundEvents.WARDEN_DEATH);
		}
    }*/
}