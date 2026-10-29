package com.serilum.voidvanguards.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.serilum.voidvanguards.parts.skyship.functions.SkyShipRenderFunctions;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.MoonPhase;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkyRenderer.class, priority = 1001)
public class SkyRendererMixin {
	@Inject(method = "renderSunMoonAndStars(Lcom/mojang/blaze3d/vertex/PoseStack;FFFLnet/minecraft/world/level/MoonPhase;FF)V", at = @At(value = "RETURN"))
	public void renderSunMoonAndStars(PoseStack poseStack, float sunAngle, float moonAngle, float starAngle, MoonPhase moonPhase, float rainBrightness, float starBrightness, CallbackInfo ci) {
		Profiler.get().push("orbiting_ship");
		SkyShipRenderFunctions.renderOrbitingShip(new Matrix4f(RenderSystem.getModelViewMatrix()));
		Profiler.get().pop();
	}
}
