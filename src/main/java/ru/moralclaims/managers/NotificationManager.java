package ru.moralclaims.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.moralclaims.MoralClaimsPlugin;
import ru.moralclaims.models.Claim;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationManager {
    private final MoralClaimsPlugin plugin;
    private final Map<UUID, Map<String, NotificationGroup>> pendingNotifications = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitTask> notificationTasks = new ConcurrentHashMap<>();
    
    public NotificationManager(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void addNotification(Claim claim, Player intruder, String action, org.bukkit.Location location) {
        UUID ownerId = claim.getOwnerId();
        String key = intruder.getName() + ":" + action;
        
        // Получаем или создаем группу уведомлений для владельца
        Map<String, NotificationGroup> ownerNotifications = pendingNotifications.computeIfAbsent(ownerId, k -> new ConcurrentHashMap<>());
        
        // Получаем или создаем группу для конкретного действия
        NotificationGroup group = ownerNotifications.computeIfAbsent(key, k -> new NotificationGroup(claim, intruder, action, location));
        
        // Увеличиваем счетчик
        group.incrementCount();
        
        // Сбрасываем таймер
        resetNotificationTimer(ownerId);
    }
    
    private void resetNotificationTimer(UUID ownerId) {
        // Отменяем предыдущий таймер если есть
        BukkitTask existingTask = notificationTasks.get(ownerId);
        if (existingTask != null) {
            existingTask.cancel();
        }
        
        // Создаем новый таймер
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            sendGroupedNotifications(ownerId);
        }, getNotificationDelay() * 20L); // Конвертируем секунды в тики
        
        notificationTasks.put(ownerId, task);
    }
    
    private void sendGroupedNotifications(UUID ownerId) {
        Map<String, NotificationGroup> ownerNotifications = pendingNotifications.remove(ownerId);
        notificationTasks.remove(ownerId);
        
        if (ownerNotifications == null || ownerNotifications.isEmpty()) {
            return;
        }
        
        ConfigManager config = plugin.getConfigManager();
        TelegramManager telegramManager = plugin.getTelegramManager();
        
        // Отправляем уведомления в игру
        if (config.isServerBroadcastEnabled()) {
            Player owner = Bukkit.getPlayer(ownerId);
            if (owner != null && owner.isOnline()) {
                for (NotificationGroup group : ownerNotifications.values()) {
                    String message = formatGameNotification(group);
                    owner.sendMessage(message);
                }
            }
        }
        
        // Отправляем уведомления в Telegram
        if (config.areTelegramNotificationsEnabled()) {
            StringBuilder telegramMessage = new StringBuilder();
            telegramMessage.append("⚠️ Активность в ваших приватах:\n\n");
            
            for (NotificationGroup group : ownerNotifications.values()) {
                telegramMessage.append(formatTelegramNotification(group)).append("\n");
            }
            
            telegramManager.sendNotification(ownerId, telegramMessage.toString().trim());
        }
        
        // Логирование
        if (config.isActionLoggingEnabled()) {
            for (NotificationGroup group : ownerNotifications.values()) {
                plugin.getLogger().info(String.format("[MoralClaims] %s %s in %s's claim %dx", 
                        group.getIntruderName(), group.getAction(), group.getClaim().getOwnerName(), group.getCount()));
            }
        }
    }
    
    private String formatGameNotification(NotificationGroup group) {
        String locationStr = String.format("(%d, %d, %d)", 
                group.getLocation().getBlockX(), 
                group.getLocation().getBlockY(), 
                group.getLocation().getBlockZ());
        
        if (group.getCount() == 1) {
            return String.format("§c⚠ %s %s в твоем привате %s", 
                    group.getIntruderName(), group.getAction(), locationStr);
        } else {
            return String.format("§c⚠ %s %s в твоем привате %s §7(x%d)", 
                    group.getIntruderName(), group.getAction(), locationStr, group.getCount());
        }
    }
    
    private String formatTelegramNotification(NotificationGroup group) {
        String locationStr = String.format("(%d, %d, %d)", 
                group.getLocation().getBlockX(), 
                group.getLocation().getBlockY(), 
                group.getLocation().getBlockZ());
        
        if (group.getCount() == 1) {
            return String.format("• %s %s\n  📍 %s", 
                    group.getIntruderName(), group.getAction(), locationStr);
        } else {
            return String.format("• %s %s (x%d)\n  📍 %s", 
                    group.getIntruderName(), group.getAction(), group.getCount(), locationStr);
        }
    }
    
    private int getNotificationDelay() {
        return plugin.getConfigManager().getNotificationDelay();
    }
    
    public void cleanup() {
        // Отправляем все оставшиеся уведомления
        for (UUID ownerId : pendingNotifications.keySet()) {
            BukkitTask task = notificationTasks.get(ownerId);
            if (task != null) {
                task.cancel();
            }
            sendGroupedNotifications(ownerId);
        }
        
        pendingNotifications.clear();
        notificationTasks.clear();
    }
    
    private static class NotificationGroup {
        private final Claim claim;
        private final String intruderName;
        private final String action;
        private final org.bukkit.Location location;
        private int count = 0;
        
        public NotificationGroup(Claim claim, Player intruder, String action, org.bukkit.Location location) {
            this.claim = claim;
            this.intruderName = intruder.getName();
            this.action = action;
            this.location = location.clone();
        }
        
        public void incrementCount() {
            count++;
        }
        
        public Claim getClaim() { return claim; }
        public String getIntruderName() { return intruderName; }
        public String getAction() { return action; }
        public org.bukkit.Location getLocation() { return location; }
        public int getCount() { return count; }
    }
}