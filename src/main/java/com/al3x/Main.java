package com.al3x;

import com.al3x.checks.TimerCheck;
import com.al3x.commands.AlertsCommand;
import com.al3x.commands.AnticheatCommand;
import com.al3x.commands.LogsCommand;
import com.al3x.config.AnticheatConfig;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.modules.accesscontrol.AccessControlModule;
import com.hypixel.hytale.server.core.modules.accesscontrol.provider.HytaleBanProvider;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.netty.util.internal.ReflectionUtil;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Main extends JavaPlugin {

    private TimerCheck timerCheck;

    private final ArrayList<AnticheatPlayer> anticheatPlayers;
    private final StaffManager staffManager;
    private ScheduledFuture<?> flagResetTask;
    private HytaleBanProvider banProvider;

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
        this.anticheatPlayers = new ArrayList<>();
        this.staffManager = new StaffManager();
    }

    @Override
    protected void setup() {
        AnticheatConfig.reload(this);
        this.getCommandRegistry().registerCommand(new AlertsCommand(staffManager));
        this.getCommandRegistry().registerCommand(new AnticheatCommand(this));
        this.getCommandRegistry().registerCommand(new LogsCommand(this));

        this.banProvider = getPublic(HytaleBanProvider.class, AccessControlModule.get(), "banProvider");
        if (banProvider == null) throw new RuntimeException("Could not find Hytale Access Control Module");

        // Add Anticheat Player on Connect and Remove on Disconnect + Give Staff Alert Permission
        this.getEventRegistry().registerGlobal(PlayerConnectEvent.class, (event) -> {
            PlayerRef playerRef = event.getPlayerRef();
            Player player = playerRef.getComponent(Player.getComponentType());
            anticheatPlayers.add(new AnticheatPlayer(this, playerRef));
            if (player.hasPermission("hytaleac.alerts")) {
                staffManager.addAlertUser(playerRef.getUuid());
            }
        });
        this.getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, (event) -> {
            UUID uuid = event.getPlayerRef().getUuid();
            anticheatPlayers.removeIf(acPlayer -> acPlayer.getUuid().equals(uuid));
            timerCheck.removePlayer(uuid);
        });

        // Register Checks
        timerCheck = new TimerCheck(this);

        timerCheck.register();

        scheduleFlagReset();
    }

    public AnticheatPlayer getAnticheatPlayer(UUID uuid) {
        for (AnticheatPlayer acPlayer : anticheatPlayers) {
            if (acPlayer.getUuid().equals(uuid)) {
                return acPlayer;
            }
        }
        return null;
    }

    public StaffManager getStaffManager() {
        return staffManager;
    }

    public void reloadConfig() {
        AnticheatConfig.reload(this);
        scheduleFlagReset();
    }

    private void scheduleFlagReset() {
        if (flagResetTask != null) {
            flagResetTask.cancel(false);
            flagResetTask = null;
        }
        int intervalSeconds = AnticheatConfig.getAlertResetIntervalSeconds();
        if (intervalSeconds <= 0) {
            return;
        }
        flagResetTask = HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            anticheatPlayers.forEach(AnticheatPlayer::resetFlags);
            if (AnticheatConfig.isAlertNotifyReset()) {
                staffManager.alertViolationsReset();
            }
        }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }

    // Credits to buuz135 for this reflection method
    public <T> T getPublic(Class<T> classZ, Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public HytaleBanProvider getBanProvider() {
        return banProvider;
    }

    @Override
    protected void shutdown() {
        if (flagResetTask != null) flagResetTask.cancel(false);
        super.shutdown();
    }
}
