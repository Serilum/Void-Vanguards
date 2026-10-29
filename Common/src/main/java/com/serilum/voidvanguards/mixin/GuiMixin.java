package com.serilum.voidvanguards.mixin;

import com.serilum.voidvanguards.events.VoidClientEvents;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Gui.class, priority = 1001)
public class GuiMixin {
	@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;F)V", at = @At(value = "RETURN"))
	public void render(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
		VoidClientEvents.renderOverlay(guiGraphics, partialTick);
	}
}
