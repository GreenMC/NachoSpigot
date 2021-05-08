package io.github.greenmc.greenspigot.events.block;

import org.bukkit.block.Sign;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;

public class SignOpenEvent extends BlockEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Sign block;
    private final SignOpenType signOpenType;

    private boolean cancelled = false;

    public SignOpenEvent(Sign block, SignOpenType signOpenType) {
        super(block.getBlock());
        this.block = block;
        this.signOpenType = signOpenType;
    }

    public Sign getSign() {
        return block;
    }

    public SignOpenType getOpenType() {
        return signOpenType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


    public enum SignOpenType {
        BLOCK, PLUGIN
    }
}