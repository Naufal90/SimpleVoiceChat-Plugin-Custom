package com.example.voicechatplugin;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatServer;
import de.maxhenkel.voicechat.api.ServerPlayer;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.GroupManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.PluginCommand;

import java.util.Optional;

public class Main extends JavaPlugin implements VoicechatPlugin {

    private VoicechatApi api;

    @Override
    public void onEnable() {
        getLogger().info("SimpleVoiceChat Custom Plugin Aktif!");

        PluginCommand voiceChatCommand = getCommand("voicechat");
        if (voiceChatCommand != null && api != null) {
            voiceChatCommand.setExecutor(new VoiceChatCommand(api));
        } else {
            getLogger().warning("Gagal mendaftarkan perintah /voicechat! API belum tersedia.");
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

    public void createVoiceGroup(ServerPlayer player, String groupName) {
        if (api == null) {
            getLogger().warning("API belum diinisialisasi!");
            return;
        }

        VoicechatServer voiceServer = api.getVoicechatServer();
        GroupManager groupManager = voiceServer.getGroupManager();

        if (groupManager.groupExists(groupName)) {
            player.getPlayer().sendMessage("Grup " + groupName + " sudah ada!");
            return;
        }

        Group newGroup = groupManager.createGroup(groupName, player);
        player.setGroup(newGroup);
        player.getPlayer().sendMessage("Grup suara " + groupName + " berhasil dibuat!");
    }

    public VoicechatApi getApi() {
        return api;
    }
}
