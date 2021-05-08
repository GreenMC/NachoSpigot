package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.cobblesword.nachospigot.protocol.MinecraftPipeline;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerConnection {

    private static final Logger e = LogManager.getLogger();

    private static boolean isUsingEpoll = false;

    public static LazyInitVar<NioEventLoopGroup> a;
    public static LazyInitVar<EpollEventLoopGroup> b;
    public static final LazyInitVar<DefaultEventLoopGroup> c = new LazyInitVar() {
        protected DefaultEventLoopGroup a() {
            return new DefaultEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Server IO #%d").setDaemon(true).build());
        }

        protected Object init() {
            return this.a();
        }
    };
    public final MinecraftServer f;
    public volatile boolean d;
    private final List<ChannelFuture> g = Collections.synchronizedList(Lists.newArrayList()); public List<ChannelFuture> getListeningChannels() { return this.g; } //OBFHELPER
    private final List<NetworkManager> h = Collections.synchronizedList(Lists.newArrayList()); public List<NetworkManager> getConnectedChannels() { return this.h; } //OBFHELPER
    // Paper start - prevent blocking on adding a new network manager while the server is ticking
    public final java.util.Queue<NetworkManager> pending = new java.util.concurrent.ConcurrentLinkedQueue<>();
    private void addPending() {
        NetworkManager manager;
        while ((manager = pending.poll()) != null) {
            this.getConnectedChannels().add(manager);
        }
    }
    // Paper end


    public ServerConnection(MinecraftServer minecraftserver) {
        this.f = minecraftserver;
        this.d = true;
    }

    public void a(InetAddress inetaddress, int i) throws IOException {
        synchronized (this.g) {
            Class oclass;
            LazyInitVar lazyinitvar;

            // [Nacho-0039] Fixed a bug in Netty epoll, and by the way; shouldn't it first check if you even want to use native transport before checking if its available?
            // I mean, why check if its available when in the end you don't even want to use it.
            // Minecraft is weird :)
            // ----------------------
            // Added late init and only shuts it down when it is assigned to do so, maybe this will fix it?
            // Yes, I keep the variables "a" and "b" because other classes might use these.
            if (this.f.ai() && Epoll.isAvailable()) {
                oclass = EpollServerSocketChannel.class;
                ServerConnection.b = new LazyInitVar() {
                    protected EpollEventLoopGroup a() {
                        return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
                    }
                    protected Object init() {
                        return this.a();
                    }
                };
                lazyinitvar = ServerConnection.b;
                isUsingEpoll = true;
                ServerConnection.e.info("Using epoll channel type");
            } else {
                oclass = NioServerSocketChannel.class;
                ServerConnection.a = new LazyInitVar() {
                    protected NioEventLoopGroup a() {
                        return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
                    }

                    protected Object init() {
                        return this.a();
                    }
                };
                lazyinitvar = ServerConnection.a;
                isUsingEpoll = false;
                ServerConnection.e.info("Using default channel type");
            }

            this.g.add(((new ServerBootstrap()
                    .channel(oclass))
                    .childHandler(new MinecraftPipeline(this))
                    .group((EventLoopGroup) lazyinitvar.c())
                    .localAddress(inetaddress, i))
                    .bind()
                    .syncUninterruptibly());

                        /*    new ChannelInitializer()
            {
                protected void initChannel(Channel channel) throws Exception {
                    try {
                        channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                    } catch (ChannelException channelexception) {
                        ;
                    }

                    channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30))
                                      .addLast("legacy_query", new LegacyPingHandler(ServerConnection.this))
                                      .addLast("splitter", PacketSplitter.INSTANCE)
                                      .addLast("decoder", new PacketDecoder(EnumProtocolDirection.SERVERBOUND))
                                      .addLast("prepender", PacketPrepender.INSTANCE)
                                      .addLast("encoder", new PacketEncoder(EnumProtocolDirection.CLIENTBOUND));
                    NetworkManager networkmanager = new NetworkManager(EnumProtocolDirection.SERVERBOUND);

                    pending.add(networkmanager); // Paper
                    channel.pipeline().addLast("packet_handler", networkmanager);
                    networkmanager.a((PacketListener) (new HandshakeListener(ServerConnection.this.f, networkmanager)));
                }
            }*/
        }
    }

    public void b() throws InterruptedException {
        this.d = false;
        for (ChannelFuture channelfuture : this.g) {
            try {
                channelfuture.channel().close().sync();
            } finally {
                if(isUsingEpoll) b.c().shutdownGracefully();
                else a.c().shutdownGracefully();
                c.c().shutdownGracefully();
            }
        }

    }

    public void c() {
        synchronized (this.getConnectedChannels()) {
            // Spigot Start
            this.addPending(); // Paper
            // This prevents players from 'gaming' the server, and strategically relogging to increase their position in the tick order
            if ( org.spigotmc.SpigotConfig.playerShuffle > 0 && MinecraftServer.currentTick % org.spigotmc.SpigotConfig.playerShuffle == 0 ) {
                Collections.shuffle( this.h );
            }
            // Spigot End
            Iterator<NetworkManager> iterator = this.h.iterator();

            while (iterator.hasNext()) {
                final NetworkManager networkmanager = iterator.next();

                if (!networkmanager.h()) {
                    if (!networkmanager.isConnected()) {
                        // Spigot Start
                        // Fix a race condition where a NetworkManager could be unregistered just before connection.
                        if (networkmanager.preparing) continue;
                        // Spigot End
                        iterator.remove();
                        networkmanager.l();
                    } else {
                        try {
                            networkmanager.tick();
                        } catch (Exception exception) {
                            if (networkmanager.c()) {
                                CrashReport crashreport = CrashReport.a(exception, "Ticking memory connection");
                                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Ticking connection");

                                crashreportsystemdetails.a("Connection", networkmanager::toString);
                                throw new ReportedException(crashreport);
                            }

                            ServerConnection.e.warn("Failed to handle packet for " + networkmanager.getSocketAddress(), exception);
                            final ChatComponentText chatcomponenttext = new ChatComponentText("Internal server error");

                            networkmanager.a(new PacketPlayOutKickDisconnect(chatcomponenttext), (GenericFutureListener) future -> networkmanager.close(chatcomponenttext));
                            networkmanager.k();
                        }
                    }
                }
            }

        }
    }

    public MinecraftServer d() {
        return this.f;
    }
}
