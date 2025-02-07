package com.example.voicechatplugin;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements VoicechatPlugin {

    private VoicechatApi api;
    public static VoiceChatWebSocketServer webSocketServer;

    @Override
    public void onEnable() {
        getLogger().info("SimpleVoiceChat Custom Plugin Aktif!");

        // Inisialisasi WebSocket Server jika belum berjalan
        if (webSocketServer == null) {
            webSocketServer = new VoiceChatWebSocketServer(24454);
            webSocketServer.start();
        }

        // Daftarkan command /voicechat
        getCommand("voicechat").setExecutor(new VoiceChatCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Dimatikan!");
        if (webSocketServer != null) {
            webSocketServer.stopServer();
            webSocketServer = null;
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
    }

    public VoicechatApi getApi() {
        return api;
    }
}
