package org.bukkit.command.defaults.nacho;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class KnockbackCommand extends Command {
    public KnockbackCommand(String name, List<String> aliases) {
        super(name, "", "/" + name, aliases);
        setPermission("ns.knockback");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] str) {
        // GreenMC - fixes https://github.com/CobbleSword/NachoSpigot/issues/37
        if (!testPermission(sender)) return true;
        // GreenMC
        if(str.length == 0) {
            sendHelp(sender);
            return false;
        }
        Server server = sender.getServer();
        switch (str[0].toLowerCase()) {
            case "reload": {
                sender.sendMessage("Reloading KB config..");
                server.reloadKB();
                sender.sendMessage(ChatColor.GRAY + "Reloaded KB config!");
                break;
            }
            case "set": {
                String[] args = Arrays.copyOfRange(str, 1, str.length);
                if (args.length < 2) {
                    sendHelpKB(sender);
                    break;
                }
                double value;
                try {
                    value = Double.parseDouble(args[1]);
                } catch (Exception ignored) {
                    sender.sendMessage(ChatColor.RED + "Invalid value.");
                    break;
                }
                boolean success = true;
                switch (args[0].toLowerCase()) {
                    case "f": {
                        server.setKnockbackFriction(value);
                        break;
                    }
                    case "h": {
                        server.setKnockbackHorizontal(value);
                        break;
                    }
                    case "v": {
                        server.setKnockbackVertical(value);
                        break;
                    }
                    case "vl": {
                        server.setKnockbackVerticalLimit(value);
                        break;
                    }
                    case "eh": {
                        server.setKnockbackExtraHorizontal(value);
                        break;
                    }
                    case "ev": {
                        server.setKnockbackExtraVertical(value);
                        break;
                    }
                    default: {
                        sendHelpKB(sender);
                        success = false;
                        break;
                    }
                }
                if (success) {
                    sender.sendMessage(ChatColor.GRAY + args[0].toLowerCase() + ChatColor.AQUA + " set to " + ChatColor.GRAY + value);
                }
                break;
            }
            default: {
                sendHelp(sender);
                break;
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Please specify a subcommand. Possible subcommands:");
        sender.sendMessage(ChatColor.RED + "<subcommand> | <description>");
        sender.sendMessage(ChatColor.RED + "reload | reload the config");
        sender.sendMessage(ChatColor.RED + "set <what to set> <value> | set a kb value to something and save to config");
    }

    private void sendHelpKB(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Please specify what to set. Possible values:");
        sender.sendMessage(ChatColor.GRAY + "<to type> | <description>");
        sender.sendMessage(ChatColor.RED + "f | friction");
        sender.sendMessage(ChatColor.RED + "h | horizontal");
        sender.sendMessage(ChatColor.RED + "v | vertical");
        sender.sendMessage(ChatColor.RED + "vl | vertical limit");
        sender.sendMessage(ChatColor.RED + "eh | extra horizontal");
        sender.sendMessage(ChatColor.RED + "ev | extra vertical");
    }
}
