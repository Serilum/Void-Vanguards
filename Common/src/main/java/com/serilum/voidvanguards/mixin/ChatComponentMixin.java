package com.serilum.voidvanguards.mixin;

import com.serilum.voidvanguards.data.ClientConstants;
import com.serilum.voidvanguards.data.ClientVariables;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChatComponent.class, priority = 1001)
public class ChatComponentMixin {
	@Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;IIILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;Z)V", at = @At(value = "HEAD"))
	private void fadeChatDuringFlicker(GuiGraphicsExtractor guiGraphics, Font font, int $$2, int $$3, int $$4, ChatComponent.DisplayMode displayMode, boolean $$6, CallbackInfo ci) {
		if (ClientVariables.lastFlickerAlpha <= 0.01F) {
			return;
		}

		int screenWidth = ClientConstants.mc.getWindow().getGuiScaledWidth();
		int screenHeight = ClientConstants.mc.getWindow().getGuiScaledHeight();

		int alphaInt = (int) (ClientVariables.lastFlickerAlpha * 204) & 0xFF;
		int color = (alphaInt << 24);

		guiGraphics.fill(0, 0, screenWidth, screenHeight, color);
	}
}
