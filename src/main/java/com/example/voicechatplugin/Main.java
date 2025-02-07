package com.example.voicechatplugin;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.ServerPlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;

public class Main extends JavaPlugin implements VoicechatPlugin {

    private VoicechatApi api;

    @Override
    public void onEnable() {
        getLogger().info("SimpleVoiceChat Custom Plugin Aktif!");

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
        });

        api.getEventRegistry().registerVoiceChatDisconnectedListener(event -> {
            getLogger().info(event.getPlayer().getName() + " keluar dari voice chat.");
        });
    }

    public VoicechatApi getApi() {
        return api;
    }
}
