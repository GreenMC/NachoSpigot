package org.bukkit.craftbukkit.command;

import net.minecraft.server.DedicatedServer;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.util.Waitable;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ConsoleCommandCompleter implements Completer {

    private final DedicatedServer server; // Green - CraftServer -> DedicatedServer

    public ConsoleCommandCompleter(DedicatedServer server) { // Green - CraftServer -> DedicatedServer
        this.server = server;
    }

    @Override
    // Green start - Change method signature for JLine update
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        final CraftServer server = this.server.server;
        final String buffer = line.line();
        // Green end

        Waitable<List<String>> waitable = new Waitable<List<String>>() {
            @Override
            protected List<String> evaluate() {
                return server.getCommandMap().tabComplete(server.getConsoleSender(), buffer);
            }
        };

        server.getServer().processQueue.add(waitable); // Green
        try {
            List<String> offers = waitable.get();
            if (offers == null) {
                return;
            }

            // Green start - JLine update
            for (String completion : offers) {
                if (completion.isEmpty()) {
                    continue;
                }

                candidates.add(new Candidate(completion));

            }
            // Green end

            // Green start - JLine handles cursor now
            /*
            final int lastSpace = buffer.lastIndexOf(' ');
            if (lastSpace == -1) {
                return cursor - buffer.length();
            } else {
                return cursor - (buffer.length() - lastSpace - 1);
            }
            */
            // Green end
        } catch (ExecutionException e) {
            server.getLogger().log(Level.WARNING, "Unhandled exception when tab completing", e); // Green
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
