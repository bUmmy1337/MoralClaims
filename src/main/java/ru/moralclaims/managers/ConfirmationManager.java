package ru.moralclaims.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.moralclaims.MoralClaimsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConfirmationManager {
    private final MoralClaimsPlugin plugin;
    private final Map<UUID, PendingConfirmation> pendingConfirmations = new HashMap<>();
    
    public ConfirmationManager(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void requestTelegramUnlink(Player player) {
        UUID playerId = player.getUniqueId();
        
        // Удаляем предыдущее подтверждение если есть
        removePendingConfirmation(playerId);
        
        // Создаем новое подтверждение
        PendingConfirmation confirmation = new PendingConfirmation(
            ConfirmationType.TELEGRAM_UNLINK,
            System.currentTimeMillis() + 30000 // 30 секунд
        );
        
        pendingConfirmations.put(playerId, confirmation);
        
        player.sendMessage("§c⚠ Подтверждение отвязки Telegram");
        player.sendMessage("§7Повторите команду §f/telegram §7в течение 30 секунд для подтверждения");
        player.sendMessage("§7Это действие отвяжет ваш Telegram аккаунт от игрового профиля");
        
        // Автоматически удаляем подтверждение через 30 секунд
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (removePendingConfirmation(playerId)) {
                if (player.isOnline()) {
                    player.sendMessage("§7Время подтверждения отвязки истекло");
                }
            }
        }, 600L); // 30 секунд = 600 тиков
    }
    
    public boolean confirmTelegramUnlink(Player player) {
        UUID playerId = player.getUniqueId();
        PendingConfirmation confirmation = pendingConfirmations.get(playerId);
        
        if (confirmation == null) {
            return false;
        }
        
        if (confirmation.type != ConfirmationType.TELEGRAM_UNLINK) {
            return false;
        }
        
        if (System.currentTimeMillis() > confirmation.expiresAt) {
            removePendingConfirmation(playerId);
            player.sendMessage("§cВремя подтверждения истекло");
            return false;
        }
        
        removePendingConfirmation(playerId);
        return true;
    }
    
    public boolean hasPendingConfirmation(Player player, ConfirmationType type) {
        PendingConfirmation confirmation = pendingConfirmations.get(player.getUniqueId());
        return confirmation != null && confirmation.type == type && 
               System.currentTimeMillis() <= confirmation.expiresAt;
    }
    
    private boolean removePendingConfirmation(UUID playerId) {
        return pendingConfirmations.remove(playerId) != null;
    }
    
    public void cleanup() {
        pendingConfirmations.clear();
    }
    
    private static class PendingConfirmation {
        final ConfirmationType type;
        final long expiresAt;
        
        PendingConfirmation(ConfirmationType type, long expiresAt) {
            this.type = type;
            this.expiresAt = expiresAt;
        }
    }
    
    public enum ConfirmationType {
        TELEGRAM_UNLINK
    }
}