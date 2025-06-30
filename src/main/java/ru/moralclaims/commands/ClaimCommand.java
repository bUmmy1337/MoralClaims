package ru.moralclaims.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.moralclaims.MoralClaimsPlugin;
import ru.moralclaims.managers.ClaimManager;
import ru.moralclaims.managers.ConfigManager;
import ru.moralclaims.models.Claim;

import java.util.List;

public class ClaimCommand implements CommandExecutor {
    private final MoralClaimsPlugin plugin;
    
    public ClaimCommand(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cЭта команда только для игроков!");
            return true;
        }
        
        Player player = (Player) sender;
        ClaimManager claimManager = plugin.getClaimManager();
        ConfigManager configManager = plugin.getConfigManager();
        
        switch (command.getName().toLowerCase()) {
            case "claim":
                return handleClaim(player, args, claimManager, configManager);
            case "unclaim":
                return handleUnclaim(player, claimManager);
            case "claimlist":
                return handleClaimList(player, claimManager);
            case "claimtrust":
                return handleClaimTrust(player, args, claimManager);
            case "claimuntrust":
                return handleClaimUntrust(player, args, claimManager);
            case "clearselection":
                return handleClearSelection(player);
            default:
                return false;
        }
    }
    
    private boolean handleClaim(Player player, String[] args, ClaimManager claimManager, ConfigManager configManager) {
        // Проверяем, есть ли выделенная область
        if (!plugin.getSelectionManager().hasCompleteSelection(player)) {
            player.sendMessage("§cСначала выдели территорию кисточкой археолога!");
            player.sendMessage("§7Переименуй кисточку археолога в наковальне на §f\"Инструмент привата\"");
            player.sendMessage("§7Левый клик - первая точка, правый клик - вторая точка");
            player.sendMessage("§7Скрафти кисточку археолога из: палка + перо + медный слиток");
            return true;
        }
        
        Location pos1 = plugin.getSelectionManager().getFirstPosition(player);
        Location pos2 = plugin.getSelectionManager().getSecondPosition(player);
        
        // Проверяем лимиты
        int area = plugin.getSelectionManager().getSelectionArea(player);
        if (area > configManager.getMaxClaimArea()) {
            player.sendMessage(String.format("§cСлишком большая область! Максимум: %d блоков", configManager.getMaxClaimArea()));
            return true;
        }
        
        if (area < configManager.getMinClaimArea()) {
            player.sendMessage(String.format("§cСлишком маленькая область! Минимум: %d блоков", configManager.getMinClaimArea()));
            return true;
        }
        
        if (claimManager.createClaim(player, pos1, pos2)) {
            player.sendMessage(String.format("§aПриват создан! Площадь: %d блоков", area));
            player.sendMessage("§7Теперь другие игроки не смогут строить в этой зоне");
            plugin.getSelectionManager().clearSelection(player);
        } else {
            List<Claim> playerClaims = claimManager.getPlayerClaims(player);
            if (playerClaims.size() >= configManager.getMaxClaimsPerPlayer()) {
                player.sendMessage(String.format("§cУ тебя уже максимум приватов (%d)!", configManager.getMaxClaimsPerPlayer()));
            } else {
                player.sendMessage("§cЗдесь уже есть чужой приват! Найди другое место.");
            }
        }
        
        return true;
    }
    
    private boolean handleUnclaim(Player player, ClaimManager claimManager) {
        if (claimManager.removeClaim(player, player.getLocation())) {
            player.sendMessage("§aПриват удален!");
        } else {
            player.sendMessage("§cЗдесь нет твоего привата!");
        }
        return true;
    }
    
    private boolean handleClaimList(Player player, ClaimManager claimManager) {
        List<Claim> claims = claimManager.getPlayerClaims(player);
        
        if (claims.isEmpty()) {
            player.sendMessage("§7У тебя нет приватов");
            return true;
        }
        
        player.sendMessage("§6=== Твои приваты ===");
        for (int i = 0; i < claims.size(); i++) {
            Claim claim = claims.get(i);
            player.sendMessage(String.format("§7%d. §f%s §7(размер: %dx%d, площадь: %d)", 
                    i + 1, claim.getCenterLocation().toString(), 
                    claim.getWidth(), claim.getLength(), claim.getArea()));
        }
        
        return true;
    }
    
    private boolean handleClaimTrust(Player player, String[] args, ClaimManager claimManager) {
        if (args.length != 1) {
            player.sendMessage("§cИспользуй: /claimtrust <игрок>");
            return true;
        }
        
        Claim claim = claimManager.getClaimAt(player.getLocation());
        if (claim == null) {
            player.sendMessage("§cТы не находишься в привате!");
            return true;
        }
        
        if (!claim.isOwner(player)) {
            player.sendMessage("§cТолько владелец может добавлять участников!");
            return true;
        }
        
        org.bukkit.OfflinePlayer targetPlayer = org.bukkit.Bukkit.getOfflinePlayer(args[0]);
        if (!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline()) {
            player.sendMessage("§cИгрок не найден!");
            return true;
        }
        
        if (claim.addTrustedPlayer(targetPlayer.getUniqueId())) {
            claimManager.saveClaims();
            player.sendMessage("§aИгрок §f" + targetPlayer.getName() + " §aдобавлен в приват!");
            
            if (targetPlayer.isOnline()) {
                targetPlayer.getPlayer().sendMessage("§aВас добавили в приват игрока §f" + player.getName());
            }
        } else {
            player.sendMessage("§cИгрок уже добавлен в приват!");
        }
        
        return true;
    }
    
    private boolean handleClaimUntrust(Player player, String[] args, ClaimManager claimManager) {
        if (args.length != 1) {
            player.sendMessage("§cИспользуй: /claimuntrust <игрок>");
            return true;
        }
        
        Claim claim = claimManager.getClaimAt(player.getLocation());
        if (claim == null) {
            player.sendMessage("§cТы не находишься в привате!");
            return true;
        }
        
        if (!claim.isOwner(player)) {
            player.sendMessage("§cТолько владелец может удалять участников!");
            return true;
        }
        
        org.bukkit.OfflinePlayer targetPlayer = org.bukkit.Bukkit.getOfflinePlayer(args[0]);
        if (!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline()) {
            player.sendMessage("§cИгрок не найден!");
            return true;
        }
        
        if (claim.removeTrustedPlayer(targetPlayer.getUniqueId())) {
            claimManager.saveClaims();
            player.sendMessage("§aИгрок §f" + targetPlayer.getName() + " §aудален из привата!");
            
            if (targetPlayer.isOnline()) {
                targetPlayer.getPlayer().sendMessage("§cВас удалили из привата игрока §f" + player.getName());
            }
        } else {
            player.sendMessage("§cИгрок не был добавлен в приват!");
        }
        
        return true;
    }
    
    private boolean handleClearSelection(Player player) {
        plugin.getSelectionManager().clearSelection(player);
        return true;
    }
}