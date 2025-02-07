package com.example.voicechatplugin;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatServer;
import de.maxhenkel.voicechat.api.ServerPlayer;
import de.maxhenkel.voicechat.api.Group;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class VoiceChatCommand implements CommandExecutor {

    private final Main plugin;

    public VoiceChatCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Hanya pemain yang bisa menggunakan perintah ini!");
            return true;
        }

        Player player = (Player) sender;
        VoicechatApi api = plugin.getApi();
        if (api == null) {
            player.sendMessage("API Voice Chat tidak tersedia!");
            return true;
        }

        VoicechatServer voiceServer = api.getVoicechatServer();
        ServerPlayer serverPlayer = voiceServer.getPlayer(player.getUniqueId()).orElse(null);

        if (serverPlayer == null) {
            player.sendMessage("Gagal mendapatkan data pemain.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("Gunakan: /voicechat [create|join|leave] [nama_grup]");
            return true;
        }

        String action = args[0].toLowerCase();
        String groupName = args[1];

        switch (action) {
            case "create":
                createVoiceGroup(voiceServer, serverPlayer, player, groupName);
                break;
            case "join":
                joinVoiceGroup(voiceServer, serverPlayer, player, groupName);
                break;
            case "leave":
                leaveVoiceGroup(serverPlayer, player);
                break;
            default:
                player.sendMessage("Perintah tidak dikenal! Gunakan: /voicechat [create|join|leave] [nama_grup]");
                break;
        }

        return true;
    }

    private void createVoiceGroup(VoicechatServer voiceServer, ServerPlayer serverPlayer, Player player, String groupName) {
        Optional<Group> existingGroup = voiceServer.getGroup(groupName);
        if (existingGroup.isPresent()) {
            player.sendMessage("Grup " + groupName + " sudah ada!");
            return;
        }

        Group newGroup = voiceServer.createGroup(groupName, serverPlayer);
        serverPlayer.setGroup(newGroup);
        player.sendMessage("Grup suara " + groupName + " berhasil dibuat!");

        plugin.getWebSocketServer().broadcastMessage(player.getName() + " membuat grup suara: " + groupName);
    }

    private void joinVoiceGroup(VoicechatServer voiceServer, ServerPlayer serverPlayer, Player player, String groupName) {
        Optional<Group> group = voiceServer.getGroup(groupName);
        if (group.isEmpty()) {
            player.sendMessage("Grup " + groupName + " tidak ditemukan!");
            return;
        }

        serverPlayer.setGroup(group.get());
        player.sendMessage("Anda telah bergabung ke grup suara " + groupName);

        plugin.getWebSocketServer().broadcastMessage(player.getName() + " bergabung ke grup suara: " + groupName);
    }

    private void leaveVoiceGroup(ServerPlayer serverPlayer, Player player) {
        serverPlayer.setGroup(null);
        player.sendMessage("Anda telah keluar dari semua grup suara!");

        plugin.getWebSocketServer().broadcastMessage(player.getName() + " keluar dari semua grup suara");
    }
            }
