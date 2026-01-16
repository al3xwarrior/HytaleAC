package com.al3x.commands;

import com.al3x.Main;
import com.al3x.config.AnticheatConfig;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

import javax.annotation.Nonnull;
import java.awt.*;

public class AnticheatCommand extends CommandBase {

    private final Main main;
    private final RequiredArg subcommandArg;

    public AnticheatCommand(Main main) {
        super("anticheat", "Anticheat Main Command");
        this.main = main;
        this.subcommandArg = withRequiredArg("subcommand", "Descrpition", (ArgumentType) ArgTypes.STRING);
        requirePermission("anticheat.command");
    }

    @Override
    protected void executeSync(@Nonnull CommandContext commandContext) {

        String subCmd = (String) subcommandArg.get(commandContext);

        if (subCmd.equalsIgnoreCase("help")) {
            commandContext.sendMessage(Message.join(
                    Message.raw("Anticheat Commands:").color(Color.red),
                    Message.raw("\n/anticheat help - Show this message").color(Color.WHITE),
                    Message.raw("\n/anticheat reload - Reload the Anticheat Configuration").color(Color.WHITE),
                    Message.raw("\n/alerts - Toggle Anticheat Alerts").color(Color.WHITE),
                    Message.raw("\n/logs <player> - Show recent Player Logs").color(Color.WHITE),
                    Message.raw("\nCreated by Al3x").color(Color.GRAY)
            ));
        } else if (subCmd.equalsIgnoreCase("reload")) {
            AnticheatConfig.reload(main);
            commandContext.sendMessage(Message.join(
                    Message.raw("[Anticheat] ").color(Color.RED),
                    Message.raw("Configuration Reloaded!").color(Color.WHITE)
            ));
        }
    }
}
