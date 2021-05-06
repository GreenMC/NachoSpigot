package io.github.greenmc.greenspigot;

import net.minecraft.server.DedicatedServer;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.bukkit.craftbukkit.command.ConsoleCommandCompleter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

/**
 * @author Despical
 * <p>
 * Created at 6.05.2021
 */
public final class GreenConsole extends SimpleTerminalConsole {

    private final DedicatedServer server;

    public GreenConsole(DedicatedServer server) {
        this.server = server;
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        return super.buildReader(builder
                .appName("Green")
                .completer(new ConsoleCommandCompleter(this.server))
        );
    }

    @Override
    protected boolean isRunning() {
        return !this.server.isStopped() && this.server.isRunning();
    }

    @Override
    protected void runCommand(String command) {
        this.server.issueCommand(command, this.server);
    }

    @Override
    protected void shutdown() {
        this.server.safeShutdown();
    }
}