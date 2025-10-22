package com.natamus.voidvanguards.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameRenderer.class, priority = 1001)
public abstract class GameRendererMixin {
	//@Shadow protected abstract void bobView(PoseStack poseStack, float tickDelta);

	//@Redirect(method = "renderLevel", require = 0, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V"))
	//private void redirect_bobView(GameRenderer renderer, PoseStack poseStack, float tickDelta) { }

	@Unique
	private boolean ranFromRenderLevel = false;

	@Inject(method = "renderLevel(Lnet/minecraft/client/DeltaTracker;)V", at = @At(value = "HEAD"))
	public void renderLevel_aboveBob(DeltaTracker deltaTracker, CallbackInfo ci) {
		ranFromRenderLevel = true;
	}

	@Inject(method = "renderLevel(Lnet/minecraft/client/DeltaTracker;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;screenEffectScale()Lnet/minecraft/client/OptionInstance;"))
	public void renderLevel_belowBob(DeltaTracker deltaTracker, CallbackInfo ci) {
		ranFromRenderLevel = false;
	}

	@Inject(method = "bobView(Lcom/mojang/blaze3d/vertex/PoseStack;F)V", at = @At(value = "HEAD"), cancellable = true)
	private void bobView(PoseStack poseStack, float partialTicks, CallbackInfo ci) {
		if (ranFromRenderLevel) {
			ci.cancel();
		}
	}
}