package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutOpenSignEditor implements Packet<PacketListenerPlayOut> {

    private BlockPosition blockPosition;

    public PacketPlayOutOpenSignEditor() {
    }

    public PacketPlayOutOpenSignEditor(BlockPosition var1) {
        this.blockPosition = var1;
    }

    public void a(PacketListenerPlayOut listener) {
        listener.a(this);
    }

    public void a(PacketDataSerializer serializer) throws IOException {
        this.blockPosition = serializer.c();
    }

    public void b(PacketDataSerializer serializer) throws IOException {
        serializer.a(this.blockPosition);
    }
}
