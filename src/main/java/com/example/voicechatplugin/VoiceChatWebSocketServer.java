package com.example.voicechatplugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

public class VoiceChatWebSocketServer extends WebSocketServer {

    public VoiceChatWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Client terhubung: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Client terputus: " + conn.getRemoteSocketAddress());
    }

    public void broadcastMessage(String message) {
        for (WebSocket conn : getConnections()) {
            conn.send(message);
        }
    }

    public void stopServer() {
        try {
            stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
