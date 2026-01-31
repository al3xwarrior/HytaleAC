package com.al3x.utils;

import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionSyncData;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChain;
import com.hypixel.hytale.protocol.packets.interaction.SyncInteractionChains;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class WeaponChargeHelper {

    private ConcurrentHashMap<UUID, Long> lastChargeTimes = new ConcurrentHashMap<>();

    public WeaponChargeHelper() {
        PacketAdapters.registerInbound((PlayerPacketFilter) (playerRef, packet) -> {
            if (!(packet instanceof SyncInteractionChains chains)) return false;
            for (SyncInteractionChain chain : chains.updates) {
                if (chain.interactionType == InteractionType.Primary && chain.interactionData != null) {
                    for (InteractionSyncData data : chain.interactionData) {
                        float charge = data.chargeValue;
                        InteractionState state = data.state;
                        if (state == InteractionState.Finished && charge >= 0.3f)
                            lastChargeTimes.put(playerRef.getUuid(), System.currentTimeMillis());
                    }
                }
            }
            return false;
        });
    }

    public Long getLastChargeTime(UUID playerUuid) {
        return lastChargeTimes.getOrDefault(playerUuid, 0L);
    }

    public void removePlayer(UUID playerUuid) {
        lastChargeTimes.remove(playerUuid);
    }

}
