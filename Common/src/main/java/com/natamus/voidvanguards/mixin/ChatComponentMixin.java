package com.natamus.voidvanguards.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.natamus.voidvanguards.data.ClientConstants;
import com.natamus.voidvanguards.data.ClientVariables;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatComponent.class, priority = 1001)
public class ChatComponentMixin {
	@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIIZ)V", at = @At("HEAD"))
	private void fadeChatDuringFlicker(GuiGraphics guiGraphics, int $$1, int $$2, int $$3, boolean $$4, CallbackInfo ci) {
		if (ClientVariables.lastFlickerAlpha <= 0.01F) {
			return;
		}

		int screenWidth = ClientConstants.mc.getWindow().getGuiScaledWidth();
		int screenHeight = ClientConstants.mc.getWindow().getGuiScaledHeight();

		int chatHeight = ClientConstants.mc.gui.getChat().getHeight();

		int alphaInt = (int) (ClientVariables.lastFlickerAlpha * 204) & 0xFF;
		int color = (alphaInt << 24);

		RenderSystem.disableDepthTest();
		guiGraphics.fill(0, 0, screenWidth, screenHeight, color);
		RenderSystem.enableDepthTest();
	}
}
