package cc.unknown.handlers;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.ServerJoinEvent;

public final class ConnectionHandler {
    public static String ip;
    public static int port;

    @EventLink
    public final Listener<ServerJoinEvent> join = event -> {
        ip = event.getIp();
        port = event.getPort();
    };

}