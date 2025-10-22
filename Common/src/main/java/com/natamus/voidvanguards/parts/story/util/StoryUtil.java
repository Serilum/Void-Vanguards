package com.natamus.voidvanguards.parts.story.util;

import com.natamus.voidvanguards.config.ConfigHandler;
import com.natamus.voidvanguards.parts.story.data.StoryVariables;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class StoryUtil {
	public static int getSentenceTickDelay(Component sentenceComponent) {
		if (ConfigHandler.devSettingInstantConversations) {
			return 1;
		}

		String text = sentenceComponent.getString().trim();
		if (text.isEmpty()) {
			return StoryVariables.conversationEmptyMessageTicks;
		}

		int wordCount = text.split("\\s+").length;
		int variableTicks = wordCount * StoryVariables.conversationBaseVariableTicks;

		return Math.min(StoryVariables.conversationBaseTicks + variableTicks, StoryVariables.conversationMaxTicks);
	}

	public static MutableComponent getSpeakerComponent(String nameTranslateKey, ChatFormatting nameColour, boolean translateable) {
		MutableComponent nameComponent = Component.translatable(nameTranslateKey);
		if (!translateable) {
			nameComponent = Component.literal(nameTranslateKey);
		}
		return Component.literal("<").withStyle(ChatFormatting.WHITE).append(nameComponent.withStyle(nameColour).append(Component.literal("> ").withStyle(ChatFormatting.WHITE)));
	}

	public static ChatFormatting getMessageColour(String rawMessage, String pattern, ChatFormatting defaultColour, ChatFormatting specialColour) {
		if (rawMessage.startsWith(pattern) && rawMessage.endsWith(pattern)) {
			return specialColour;
		}
		return defaultColour;
	}
}
