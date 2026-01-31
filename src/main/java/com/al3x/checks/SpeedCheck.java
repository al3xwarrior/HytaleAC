package com.al3x.checks;

import com.al3x.AnticheatPlayer;
import com.al3x.Main;
import com.al3x.config.AnticheatConfig;
import com.al3x.flags.FlyFlag;
import com.al3x.flags.SpeedFlag;
import com.al3x.utils.WeaponChargeHelper;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.Vector3d;
import com.hypixel.hytale.protocol.packets.player.ClientMovement;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.io.adapter.PacketAdapters;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.universe.Universe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpeedCheck {

    private final Main main;
    private final WeaponChargeHelper weaponChargeHelper;

    private final ConcurrentHashMap<UUID, ArrayList<Double>> recentSpeeds;
    private final ConcurrentHashMap<UUID, MovementStates> lastMovementStates;

    public SpeedCheck(Main main, WeaponChargeHelper weaponChargeHelper) {
        this.main = main;
        this.weaponChargeHelper = weaponChargeHelper;
        this.recentSpeeds = new ConcurrentHashMap<>();
        this.lastMovementStates = new ConcurrentHashMap<>();
    }

    public void register() {
        PacketAdapters.registerInbound((PlayerPacketFilter) (playerRef, packet) -> {
            if (packet instanceof ClientMovement clientMovement) {
                if (!AnticheatConfig.isSpeedEnabled()) return false;

                if (playerRef.getWorldUuid() == null) return false;

                // if the player has charged a weapon in the last second, skip speed check
                if (weaponChargeHelper.getLastChargeTime(playerRef.getUuid()) < System.currentTimeMillis() - 1000) return false;

                Universe.get().getWorld(playerRef.getWorldUuid()).execute(() -> {
                    Player player = playerRef.getComponent(Player.getComponentType());
                    if (player == null) return;

                    if (player.getGameMode().equals(GameMode.Creative)) return;

                    if (clientMovement.movementStates != null)
                        lastMovementStates.put(playerRef.getUuid(), clientMovement.movementStates);

                    if (lastMovementStates.get(playerRef.getUuid()) == null)
                        return;

                    if (clientMovement.velocity != null && playerRef.getWorldUuid() != null) {
                        MovementStates states = lastMovementStates.get(playerRef.getUuid());

                        if (states.flying || !states.onGround || states.inFluid || states.climbing || states.falling || states.jumping) return;

                        Vector3d clientVelocity = clientMovement.velocity;

                        double speed = Math.sqrt(clientVelocity.x * clientVelocity.x + clientVelocity.z * clientVelocity.z);

                        recentSpeeds.putIfAbsent(playerRef.getUuid(), new ArrayList<>());
                        ArrayList<Double> speeds = recentSpeeds.get(playerRef.getUuid());
                        speeds.add(speed);
                        if (speeds.size() > 20) speeds.removeFirst();

                        if (speed > AnticheatConfig.getMaxSpeedThreshold()) {
                            double avg = speeds.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                            if (avg > AnticheatConfig.getMaxSpeedThreshold()) {
                                AnticheatPlayer anticheatPlayer = main.getAnticheatPlayer(playerRef.getUuid());
                                if (anticheatPlayer == null) return;
                                anticheatPlayer.flagPlayer(new SpeedFlag(AnticheatConfig.getSpeedFlagsNeededToAlert(), speed));
                            }
                        }
                    }
                });
            }

            return false;
        });
    }

    public void removePlayer(UUID uuid) {
        recentSpeeds.remove(uuid);
    }
}
