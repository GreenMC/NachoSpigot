package org.spigotmc;

import net.minecraft.server.MinecraftServer;

public class AsyncCatcher
{

    public static boolean enabled = true;

    public static void catchOp(String reason)
    {
        if ( enabled && Thread.currentThread() != MinecraftServer.getServer().primaryThread )
        {
            // Green start
            //throw new IllegalStateException( "Asynchronous " + reason + "!" );
            // Green end
            // we want to break minecraft, soo
        }
    }
}
