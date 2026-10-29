package com.serilum.voidvanguards.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.serilum.voidvanguards.parts.skyship.functions.SkyShipRenderFunctions;
import net.minecraft.client.renderer.SkyRenderer;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SkyRenderer.class, priority = 1001)
public class SkyRendererMixin {
	@Inject(method = "render(Lcom/mojang/renderpearl/api/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/state/level/SkyRenderState;)V", at = @At(value = "RETURN"))
	public void render(GpuBufferSlice skyFog, SkyRenderState skyRenderState, CallbackInfo ci) {
		if (skyRenderState.skybox != DimensionType.Skybox.OVERWORLD) {
			return;
		}

		Profiler.get().push("orbiting_ship");
		SkyShipRenderFunctions.renderOrbitingShip(RenderSystem.getModelViewMatrixCopy());
		Profiler.get().pop();
	}
}
