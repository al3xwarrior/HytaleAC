package com.al3x.commands;

import com.al3x.StaffManager;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.UUID;

public class AlertsCommand extends CommandBase {

    private final StaffManager staffManager;

    public AlertsCommand(StaffManager staffManager) {
        super("alerts", "Toggle Anticheat Alerts");
        requirePermission("hytaleac.alerts");
        this.staffManager = staffManager;
    }

    @Override
    protected void executeSync(@Nonnull CommandContext commandContext) {
        if (commandContext.isPlayer()) {

            Player player = commandContext.senderAs(Player.class);
            UUID uuid = player.getUuid();

            if (staffManager.isAlertUser(uuid)) {
                staffManager.removeAlertUser(uuid);
                player.sendMessage(Message.join(
                        Message.raw("[Anticheat] ").color(Color.red),
                        Message.raw("Alerts Disabled!").color(Color.white)
                ));
            } else {
                staffManager.addAlertUser(uuid);
                player.sendMessage(Message.join(
                        Message.raw("[Anticheat] ").color(Color.red),
                        Message.raw("Alerts Enabled!").color(Color.white)
                ));
            }


        }
    }
}
