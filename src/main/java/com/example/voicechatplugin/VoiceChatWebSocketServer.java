package com.example.voicechatplugin;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VoiceChatWebSocketServer extends WebSocketServer {

    private static final int PORT = 24454; // Ganti sesuai kebutuhan
    private static final Set<WebSocket> clients = Collections.synchronizedSet(new HashSet<>());

    public VoiceChatWebSocketServer() {
        super(new InetSocketAddress(PORT));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        clients.add(conn);
        System.out.println("Koneksi WebSocket baru: " + conn.getRemoteSocketAddress());
        conn.send("Terhubung ke VoiceChat WebSocket Server");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        clients.remove(conn);
        System.out.println("Koneksi WebSocket tertutup: " + conn.getRemoteSocketAddress() + " (Code: " + code + ", Reason: " + reason + ")");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Pesan dari client: " + message);
        try {
            conn.send("Pesan diterima: " + message);
        } catch (Exception ex) {
            System.err.println("Gagal mengirim pesan ke client: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error pada WebSocket: " + ex.getMessage());
        ex.printStackTrace();
        if (conn != null) {
            conn.close(1011, "Internal Server Error");
        }
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket Server dimulai di port " + PORT);
        System.out.println("Menunggu koneksi client...");
    }

    public void broadcastMessage(String message) {
        synchronized (clients) {
            for (WebSocket client : clients) {
                try {
                    if (client.isOpen()) {
                        client.send(message);
                    }
                } catch (Exception ex) {
                    System.err.println("Gagal mengirim pesan ke client: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }

    public void stopServer() {
        try {
            for (WebSocket client : clients) {
                client.close(1000, "Server dimatikan");
            }
            this.stop();
            System.out.println("WebSocket Server dihentikan.");
        } catch (Exception ex) {
            System.err.println("Gagal menghentikan server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
