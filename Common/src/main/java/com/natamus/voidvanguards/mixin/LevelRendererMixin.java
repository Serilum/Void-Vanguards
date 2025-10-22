package com.natamus.voidvanguards.mixin;

import com.natamus.voidvanguards.parts.skyship.functions.SkyShipRenderFunctions;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LevelRenderer.class, priority = 1001)
public class LevelRendererMixin {
	@Shadow
	private ClientLevel level;

	@Inject(method = "renderSky", at = @At("TAIL"))
	private void renderSky(Matrix4f matrixA, Matrix4f matrixB, float partialTicks, Camera camera, boolean b, Runnable runnable, CallbackInfo ci) {
		// SkyShipRenderFunctions.renderOrbitingShip(matrixA);
	}

	@SuppressWarnings("DiscouragedShift")
	@Inject(method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;getCloudsType()Lnet/minecraft/client/CloudStatus;", shift = At.Shift.BEFORE))
	public void renderLevel(DeltaTracker $$0, boolean $$1, Camera $$2, GameRenderer $$3, LightTexture $$4, Matrix4f matrixA, Matrix4f $$6, CallbackInfo ci) {
		// SkyShipRenderFunctions.renderOrbitingShip(matrixA);
	}

	@SuppressWarnings("DiscouragedShift")
	@Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V", shift = At.Shift.BEFORE))
	private void renderShipBeforeWeather(DeltaTracker deltaTracker, boolean someFlag, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projMatrix, Matrix4f modelMatrix, CallbackInfo ci) {
		// SkyShipRenderFunctions.renderOrbitingShip(projMatrix);
	}

	@Inject(method = "renderLevel(Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/FogRenderer;setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZF)V"))
	public void renderLevel_afterFog(DeltaTracker $$0, boolean $$1, Camera $$2, GameRenderer $$3, LightTexture $$4, Matrix4f matrixA, Matrix4f $$6, CallbackInfo ci) {
		this.level.getProfiler().push("orbiting_ship");
		SkyShipRenderFunctions.renderOrbitingShip(matrixA);
	}
}
