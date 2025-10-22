package com.natamus.voidvanguards.mixin;

import com.natamus.voidvanguards.parts.voidborn.util.VoidbornUtil;
import com.natamus.voidvanguards.util.Reference;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(value = Villager.class, priority = 1001)
public class VillagerMixin {
	@Inject(method = "getAmbientSound()Lnet/minecraft/sounds/SoundEvent;", at = @At(value = "HEAD"), cancellable = true)
	protected void getAmbientSound(CallbackInfoReturnable<SoundEvent> cir) {
		Villager villager = (Villager) (Object) this;

		Set<String> tags = villager.getTags();
		if (tags.contains(Reference.MOD_ID + ".vanguard")) {
			if (tags.contains(Reference.MOD_ID + ".vanguard_leader")) {
				if (VoidbornUtil.shouldMakeAmbientSound(villager.level())) {
					cir.setReturnValue(SoundEvents.WARDEN_HEARTBEAT);
					return;
				}
			}

			cir.setReturnValue(null);
		}
	}
}