package io.github.greenmc.greenspigot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.command.CraftConsoleCommandSender;

/**
 * @author Despical
 * <p>
 * Created at 6.05.2021
 */
public class TerminalConsoleCommandSender extends CraftConsoleCommandSender {

    private static final Logger LOGGER = LogManager.getRootLogger();

    @Override
    public void sendRawMessage(String message) {
        // TerminalConsoleAppender supports color codes directly in log messages
        LOGGER.info(message);
    }
}