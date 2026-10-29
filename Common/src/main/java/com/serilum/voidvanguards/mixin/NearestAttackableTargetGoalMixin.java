package com.serilum.voidvanguards.mixin;

import com.serilum.voidvanguards.data.HeadData;
import com.serilum.voidvanguards.data.ServerSaveData;
import com.serilum.voidvanguards.parts.voidborn.data.VoidbornConstants;
import com.serilum.voidvanguards.util.Reference;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
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
		if (this.mob.level().isClientSide()) {
			return;
		}

		if (!this.mob.entityTags().contains(VoidbornConstants.voidbornTag)) {
			return;
		}

		if (!(target instanceof Player player)) {
			return;
		}

		boolean preventTargeting = this.mob.entityTags().contains(Reference.MOD_ID + ".notarget");

		if (!preventTargeting) {
			if (ServerSaveData.get().gaveVoidbornLeaderRadioPlayerUUIDS.contains(player.getUUID())) {
				preventTargeting = true;
			}
		}

		if (!preventTargeting) {
			ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
			ItemStack voidbornHead = HeadData.getGeneratedHead(HeadData.HeadType.VOIDBORN);

			if (helmet.is(Items.PLAYER_HEAD)) {
				if (helmet.has(DataComponents.PROFILE) && voidbornHead.has(DataComponents.PROFILE)) {
					ResolvableProfile wornProfile = helmet.get(DataComponents.PROFILE);
					ResolvableProfile voidbornProfile = voidbornHead.get(DataComponents.PROFILE);

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
