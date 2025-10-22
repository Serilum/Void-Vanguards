package com.natamus.voidvanguards.networking.packets;

import com.natamus.collective.functions.TaskFunctions;
import com.natamus.collective.implementations.networking.data.PacketContext;
import com.natamus.collective.implementations.networking.data.Side;
import com.natamus.voidvanguards.data.ClientVariables;
import com.natamus.voidvanguards.util.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ToClientQueueScreenFlickerEffectPacket {
	public static final ResourceLocation CHANNEL = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "to_client_queue_screen_flicker_effect_packet");

	private final int startDelay;
	private final int flickerTicks;

	public ToClientQueueScreenFlickerEffectPacket(int startDelayIn, int flickerTicksIn) {
		this.startDelay = startDelayIn;
		this.flickerTicks = flickerTicksIn;
	}

	public static ToClientQueueScreenFlickerEffectPacket decode(FriendlyByteBuf buf) {
		int startDelayIn = buf.readInt();
		int flickerTicksIn = buf.readInt();

		return new ToClientQueueScreenFlickerEffectPacket(startDelayIn, flickerTicksIn);
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(startDelay);
		buf.writeInt(flickerTicks);
	}

	public static void handle(PacketContext<ToClientQueueScreenFlickerEffectPacket> ctx) {
		if (ctx.side().equals(Side.CLIENT)) {
			ToClientQueueScreenFlickerEffectPacket packet = ctx.message();

			int delay = packet.startDelay;
			if (delay < 0) {
				return;
			}

			TaskFunctions.enqueueCollectiveClientTask(() -> {
				ClientVariables.flickerTicksLeft = packet.flickerTicks;
			}, delay);
		}
	}
}