package com.example.voicechatplugin;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.events.VoiceChatConnectedEvent;
import de.maxhenkel.voicechat.api.events.VoiceChatDisconnectedEvent;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements VoicechatPlugin {

    private static Main instance;
    private VoicechatApi api;
    private int websocketPort;
    private VoiceChatWebSocketServer webSocketServer;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig(); // Buat config.yml jika belum ada

        websocketPort = getConfig().getInt("websocket-port", 24454);
        getLogger().info("WebSocket akan berjalan di port: " + websocketPort);

        // Jalankan WebSocket Server
        webSocketServer = new VoiceChatWebSocketServer(websocketPort);
        webSocketServer.start();

        // Registrasi perintah
        PluginCommand voiceChatCommand = getCommand("voicechat");
        if (voiceChatCommand != null) {
            voiceChatCommand.setExecutor(new VoiceChatCommand(this));
        } else {
            getLogger().warning("Gagal mendaftarkan perintah /voicechat!");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Dimatikan!");
        if (webSocketServer != null) {
            webSocketServer.stopServer();
        }
    }

    @Override
    public String getPluginId() {
        return "customvoicechat";
    }

    @Override
    public void initialize(VoicechatApi api) {
        this.api = api;
        getLogger().info("API SimpleVoiceChat berhasil diintegrasikan!");

        api.getEventRegistry().registerVoiceChatConnectedListener(event -> {
            getLogger().info(event.getPlayer().getName() + " bergabung ke voice chat!");
            webSocketServer.broadcastMessage(event.getPlayer().getName() + " bergabung ke voice chat!");
        });

        api.getEventRegistry().registerVoiceChatDisconnectedListener(event -> {
            getLogger().info(event.getPlayer().getName() + " keluar dari voice chat.");
            webSocketServer.broadcastMessage(event.getPlayer().getName() + " keluar dari voice chat.");
        });
    }

    public static Main getInstance() {
        return instance;
    }

    public VoicechatApi getApi() {
        return api;
    }

    public static int getWebsocketPort() {
        return instance.websocketPort;
    }

    public VoiceChatWebSocketServer getWebSocketServer() {
        return webSocketServer;
    }
}
