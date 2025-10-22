package com.natamus.voidvanguards.mixin;

import com.natamus.voidvanguards.data.HeadData;
import com.natamus.voidvanguards.data.ServerSaveData;
import com.natamus.voidvanguards.parts.voidborn.data.VoidbornConstants;
import com.natamus.voidvanguards.util.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NearestAttackableTargetGoal.class, priority = 1001)
public abstract class NearestAttackableTargetGoalMixin extends TargetGoal {
	@Shadow protected LivingEntity target;

	public NearestAttackableTargetGoalMixin(Mob mob, boolean b) {
		super(mob, b);
	}

	@Inject(method = "findTarget", at = @At("TAIL"))
	private void onFindTarget(CallbackInfo ci) {
		if (this.mob.level().isClientSide) {
			return;
		}

		if (!this.mob.getTags().contains(VoidbornConstants.voidbornTag)) {
			return;
		}

		if (!(target instanceof Player player)) {
			return;
		}

		boolean preventTargeting = this.mob.getTags().contains(Reference.MOD_ID + ".notarget");

		if (!preventTargeting) {
			if (ServerSaveData.get().gaveVoidbornLeaderRadioPlayerUUIDS.contains(player.getUUID())) {
				preventTargeting = true;
			}
		}

		if (!preventTargeting) {
			ItemStack helmet = player.getInventory().getArmor(3);
			ItemStack voidbornHead = HeadData.HeadType.VOIDBORN.headStack;

			if (helmet.is(Items.PLAYER_HEAD)) {
				if (helmet.has(DataComponents.PROFILE) && voidbornHead.has(DataComponents.PROFILE)) {
					var wornProfile = helmet.get(DataComponents.PROFILE);
					var voidbornProfile = voidbornHead.get(DataComponents.PROFILE);

					if (wornProfile != null && wornProfile.equals(voidbornProfile)) {
						preventTargeting = true;
					}
				}
			}
		}

		if (preventTargeting) {
			this.target = null;
			this.mob.setTarget(null);
		}
	}
}
