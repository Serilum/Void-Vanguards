package com.natamus.voidvanguards.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.natamus.collective.functions.MessageFunctions;
import com.natamus.collective.functions.StringFunctions;
import com.natamus.voidvanguards.data.HeadData;
import com.natamus.voidvanguards.data.ServerSaveData;
import com.natamus.voidvanguards.parts.cosmos.functions.CosmosDimensionFunctions;
import com.natamus.voidvanguards.parts.skyship.functions.SkyShipFunctions;
import com.natamus.voidvanguards.parts.vanguard.functions.VanguardFunctions;
import com.natamus.voidvanguards.parts.voidborn.functions.VoidbornFunctions;
import com.natamus.voidvanguards.util.Reference;
import com.natamus.voidvanguards.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CommandVoidVanguards {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal(Reference.MOD_ID).requires((iCommandSender) -> { return iCommandSender.hasPermission(2); })
			.then(Commands.literal("reset")
			.executes((command) -> {
				CommandSourceStack source = command.getSource();

				SkyShipFunctions.resetSkyShipVariables(true);

				MessageFunctions.sendMessage(source, Component.translatable("voidvanguards.message.skyship.reset").withStyle(ChatFormatting.DARK_GREEN));

				return 1;
			}))

			.then(Commands.literal("return")
			.executes((command) -> {
				CommandSourceStack source = command.getSource();

				ServerPlayer serverPlayer = source.getPlayer();
				if (!Util.isInCosmosDimension(serverPlayer)) {
					MessageFunctions.sendMessage(source, Component.literal("You are not in the cosmos dimension.").withStyle(ChatFormatting.RED));
					return 1;
				}

				UUID playerUUID = serverPlayer.getUUID();

				if (!ServerSaveData.get().originalPlayerPositions.containsKey(playerUUID)) {
					MessageFunctions.sendMessage(source, Component.literal("Original location is not in save data.").withStyle(ChatFormatting.RED));
					return 1;
				}

				if (Util.teleportPlayerToOriginalPosition(serverPlayer)) {
					MessageFunctions.sendMessage(source, Component.literal("Returned to the original location.").withStyle(ChatFormatting.DARK_GREEN));
				}
				else {
					MessageFunctions.sendMessage(source, Component.literal("Unable to return player to original position.").withStyle(ChatFormatting.RED));
				}

				return 1;
			}))

			.then(Commands.literal("radio")
			.executes((command) -> {
				CommandSourceStack source = command.getSource();

				VanguardFunctions.generateRadioAtPlayerPosition(source.getPlayer());
				return 1;
			}))

			.then(Commands.literal("generate")
			.then(Commands.literal("cosmos_gate")
			.executes((command) -> {
				CommandSourceStack source = command.getSource();

				ServerPlayer serverPlayer = source.getPlayer();

				VoidbornFunctions.generateVoidbornCosmosGate(serverPlayer.serverLevel(), serverPlayer, serverPlayer.blockPosition().offset(5, 0, 5));

				MessageFunctions.sendMessage(serverPlayer, "Generated cosmos gate.", ChatFormatting.DARK_GREEN);
				return 1;
			})))

			.then(Commands.literal("teleport")
			.then(Commands.literal("voidborn_base")
			.executes((command) -> {
				CommandSourceStack source = command.getSource();

				ServerPlayer serverPlayer = source.getPlayer();

				CosmosDimensionFunctions.teleportPlayerToVoidbornBase(serverPlayer.serverLevel(), serverPlayer);

				MessageFunctions.sendMessage(serverPlayer, "Teleported to voidborn base.", ChatFormatting.DARK_GREEN);
				return 1;
			})))

			.then(Commands.literal("tag")
			.then(Commands.literal("remove")
			.executes((command) -> {
				CommandSourceStack source = command.getSource();

				ServerPlayer serverPlayer = source.getPlayer();
				for (String tag : serverPlayer.getTags()) {
					if (tag.toLowerCase().startsWith(Reference.MOD_ID)) {
						Util.removeTag(serverPlayer, tag);
					}
				}

				MessageFunctions.sendMessage(serverPlayer, "Removed all Void Vanguards tags.", ChatFormatting.DARK_GREEN);
				return 1;
			})))

			.then(Commands.literal("head")
			.then(Commands.literal("list")
			.executes((command) -> {
				CommandSourceStack source = command.getSource();

				MessageFunctions.sendMessage(source, "You can generate the following heads:", ChatFormatting.DARK_GREEN);
				MessageFunctions.sendMessage(source, " Usage: /" + Reference.MOD_ID + " head <name> <amount>:", ChatFormatting.DARK_GREEN);

				List<String> headNames = new ArrayList<>(HeadData.headMap.keySet());
				Collections.sort(headNames);
				String mnstr = String.join(", ", headNames);
				MessageFunctions.sendMessage(source, mnstr, ChatFormatting.YELLOW);

				return 1;
			})))
			.then(Commands.literal("head")
			.then(Commands.argument("head-name", StringArgumentType.string()).suggests(headStackSuggestions)
			.executes((command) -> {
				return headCommand(command, 1);
			})))
			.then(Commands.literal("head")
			.then(Commands.argument("head-name", StringArgumentType.string()).suggests(headStackSuggestions)
			.then(Commands.argument("amount", IntegerArgumentType.integer(1, 64))
			.executes((command) -> {
				return headCommand(command, IntegerArgumentType.getInteger(command, "amount"));
			}))))
		);
    }

	private static int headCommand(CommandContext<CommandSourceStack> command, int amount) {
		CommandSourceStack source = command.getSource();
		String headName = StringArgumentType.getString(command, "head-name").toLowerCase();

		if (!HeadData.headMap.containsKey(headName)) {
			MessageFunctions.sendMessage(source, "The headName '" + headName + "' does not exist. You can get a list of all possible heads with:", ChatFormatting.RED);
			MessageFunctions.sendMessage(source, " Usage: /" + Reference.MOD_ID + " head list", ChatFormatting.RED);
			return 1;
		}

		Player player;
		try {
			player = source.getPlayerOrException();
		}
		catch (CommandSyntaxException ex) {
			MessageFunctions.sendMessage(source, "This command can only be executed as a player in-game.", ChatFormatting.RED);
			return 1;
		}

		ItemStack headstack = HeadData.headMap.get(headName).copy();
		headstack.setCount(amount);
		if (!player.getInventory().add(headstack)) {
			player.drop(headstack, false);
		}

		String s = "";
		if (amount > 1) {
			s = "s";
		}

		MessageFunctions.sendMessage(source, "Successfully generated " + amount + " " + StringFunctions.capitalizeFirst(headName.replace("_", " ")) + " head" + s + ".", ChatFormatting.DARK_GREEN);
		return 1;
	}

	public static final SuggestionProvider<CommandSourceStack> headStackSuggestions = (context, builder) -> SharedSuggestionProvider.suggest(
		HeadData.headMap.keySet(), builder,
		value -> value,
		value -> Component.literal(value)
    );
}
