package com.example.voicechatplugin;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatApi;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements VoicechatPlugin {


private VoiceChatWebSocketServer webSocketServer;

@Override
public void onEnable() {
    getLogger().info("SimpleVoiceChat Custom Plugin Aktif!");

    getCommand("voicechat").setExecutor(new VoiceChatCommand(api));
    
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
    getLogger().info("API SimpleVoiceChat berhasil diintegrasikan!");

    api.getEventRegistry().registerVoiceChatConnectedListener(event -> {
        getLogger().info(event.getPlayer().getName() + " bergabung ke voice chat!");
    });

    api.getEventRegistry().registerVoiceChatDisconnectedListener(event -> {
        getLogger().info(event.getPlayer().getName() + " keluar dari voice chat.");
    });
}

public void createVoiceGroup(VoicechatApi api, ServerPlayer player, String groupName) {
        Optional<ServerGroup> existingGroup = api.getGroupManager().getGroups().stream()
                .filter(g -> g.getName().equalsIgnoreCase(groupName))
                        .findFirst();

                            if (existingGroup.isPresent()) {
                                    player.sendMessage("Grup " + groupName + " sudah ada!");
                                            return;
                                                }

                                                    ServerGroup newGroup = api.getGroupManager().createGroup(groupName);
                                                        api.getGroupManager().addPlayerToGroup(player, newGroup);
                                                            player.sendMessage("Grup suara " + groupName + " berhasil dibuat!");
                                                            }
}