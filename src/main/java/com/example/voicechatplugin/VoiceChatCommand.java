package com.example.voicechatplugin;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.ServerPlayer;
import de.maxhenkel.voicechat.api.ServerGroup;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class VoiceChatCommand implements CommandExecutor {

    private final VoicechatApi api;

    public VoiceChatCommand(VoicechatApi api) {
        this.api = api;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Hanya pemain yang bisa menggunakan perintah ini!");
            return true;
        }

        Player player = (Player) sender;
        ServerPlayer serverPlayer = api.getPlayerManager().getPlayer(player.getUniqueId()).orElse(null);

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
                createVoiceGroup(serverPlayer, groupName);
                break;
            case "join":
                joinVoiceGroup(serverPlayer, groupName);
                break;
            case "leave":
                leaveVoiceGroup(serverPlayer);
                break;
            default:
                player.sendMessage("Perintah tidak dikenal! Gunakan: /voicechat [create|join|leave] [nama_grup]");
                break;
        }

        return true;
    }

    private void createVoiceGroup(ServerPlayer player, String groupName) {
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

        notifyWebSocket(player.getName() + " membuat grup suara: " + groupName);
    }

    private void joinVoiceGroup(ServerPlayer player, String groupName) {
        Optional<ServerGroup> group = api.getGroupManager().getGroups().stream()
                .filter(g -> g.getName().equalsIgnoreCase(groupName))
                .findFirst();

        if (group.isEmpty()) {
            player.sendMessage("Grup " + groupName + " tidak ditemukan!");
            return;
        }

        api.getGroupManager().addPlayerToGroup(player, group.get());
        player.sendMessage("Anda telah bergabung ke grup suara " + groupName);

        notifyWebSocket(player.getName() + " bergabung ke grup suara: " + groupName);
    }

    private void leaveVoiceGroup(ServerPlayer player) {
        api.getGroupManager().removePlayerFromAllGroups(player);
        player.sendMessage("Anda telah keluar dari semua grup suara!");

        notifyWebSocket(player.getName() + " keluar dari semua grup suara");
    }

    private void notifyWebSocket(String message) {
        if (Main.webSocketServer != null) {
            Main.webSocketServer.broadcastMessage(message);
        }
    }
}
