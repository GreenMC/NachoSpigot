package org.bukkit.craftbukkit.util;

import net.minecraft.server.ExceptionWorldConflict;
import net.minecraft.server.MinecraftServer;

public class ServerShutdownThread extends Thread {

    private final MinecraftServer server;

    public ServerShutdownThread(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            server.stop();
        } catch (ExceptionWorldConflict | InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            try {
                net.minecrell.terminalconsole.TerminalConsoleAppender.close(); // Green - Use TerminalConsoleAppender
            } catch (Exception ignored) {}
        }
    }
}