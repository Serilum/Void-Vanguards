package com.natamus.voidvanguards.networking;

import com.natamus.collective.implementations.networking.api.Network;
import com.natamus.voidvanguards.networking.packets.*;

public class PacketRegistration {

	public void init() {
		initClientPackets();
		initServerPackets();
	}

	private void initClientPackets() {
		Network.registerPacket(ToClientQueueScreenFlickerEffectPacket.CHANNEL, ToClientQueueScreenFlickerEffectPacket.class, ToClientQueueScreenFlickerEffectPacket::encode, ToClientQueueScreenFlickerEffectPacket::decode, ToClientQueueScreenFlickerEffectPacket::handle);
		Network.registerPacket(ToClientSkyShipEntersOrbitPacket.CHANNEL, ToClientSkyShipEntersOrbitPacket.class, ToClientSkyShipEntersOrbitPacket::encode, ToClientSkyShipEntersOrbitPacket::decode, ToClientSkyShipEntersOrbitPacket::handle);
		Network.registerPacket(ToClientSkyShipLeavesOrbitPacket.CHANNEL, ToClientSkyShipLeavesOrbitPacket.class, ToClientSkyShipLeavesOrbitPacket::encode, ToClientSkyShipLeavesOrbitPacket::decode, ToClientSkyShipLeavesOrbitPacket::handle);
		Network.registerPacket(ToClientSyncSaveDataPacket.CHANNEL, ToClientSyncSaveDataPacket.class, ToClientSyncSaveDataPacket::encode, ToClientSyncSaveDataPacket::decode, ToClientSyncSaveDataPacket::handle);
	}

	private void initServerPackets() {
		Network.registerPacket(ToServerTriggerSkyShipEventPacket.CHANNEL, ToServerTriggerSkyShipEventPacket.class, ToServerTriggerSkyShipEventPacket::encode, ToServerTriggerSkyShipEventPacket::decode, ToServerTriggerSkyShipEventPacket::handle);
	}
}
