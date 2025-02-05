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
                                                                                    System.out.println("Koneksi WebSocket tertutup: " + conn.getRemoteSocketAddress());
                                                                                        }

                                                                                            @Override
                                                                                                public void onMessage(WebSocket conn, String message) {
                                                                                                        System.out.println("Pesan dari client: " + message);
                                                                                                                conn.send("Pesan diterima: " + message);
                                                                                                                    }

                                                                                                                        @Override
                                                                                                                            public void onError(WebSocket conn, Exception ex) {
                                                                                                                                    ex.printStackTrace();
                                                                                                                                        }

                                                                                                                                            @Override
                                                                                                                                                public void onStart() {
                                                                                                                                                        System.out.println("WebSocket Server dimulai di port " + PORT);
                                                                                                                                                            }

                                                                                                                                                                public void broadcastMessage(String message) {
                                                                                                                                                                        for (WebSocket client : clients) {
                                                                                                                                                                                    client.send(message);
                                                                                                                                                                                            }
                                                                                                                                                                                                }
                                                                                                                                                                                                }