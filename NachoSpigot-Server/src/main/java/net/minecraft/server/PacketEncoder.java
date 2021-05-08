package net.minecraft.server;

import dev.cobblesword.nachospigot.exception.ExploitException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private static final Logger a = LogManager.getLogger();
    private static final Marker b = MarkerManager.getMarker("PACKET_SENT", NetworkManager.PACKET_MARKER);
    private final EnumProtocolDirection c;

    public PacketEncoder(EnumProtocolDirection enumprotocoldirection) {
        this.c = enumprotocoldirection;
    }

    protected void a(ChannelHandlerContext channelhandlercontext, Packet packet, ByteBuf bytebuf) throws Exception {
        Integer integer = channelhandlercontext.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get().a(this.c, packet);

        if (PacketEncoder.a.isDebugEnabled()) {
            PacketEncoder.a.debug(PacketEncoder.b, "OUT: [{}:{}] {}", channelhandlercontext.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get(), integer, packet.getClass().getName());
        }

        if (integer == null) {
            throw new IOException("Can't serialize unregistered packet");
        } else {
            PacketDataSerializer packetdataserializer = new PacketDataSerializer(bytebuf);

            packetdataserializer.b(integer.intValue());

            try {
                packet.b(packetdataserializer);
            } catch (ExploitException ex) {
                System.out.println("rarrr " + channelhandlercontext.channel().attr(NetworkManager.ATTRIBUTE_PROTOCOL).get());
            }

        }
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        this.a(channelHandlerContext, packet, byteBuf);
    }
}
