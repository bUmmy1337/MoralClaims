package ru.moralclaims.managers;

import org.bukkit.configuration.file.FileConfiguration;
import ru.moralclaims.MoralClaimsPlugin;

import java.util.List;
import java.util.Random;

public class ConfigManager {
    private final MoralClaimsPlugin plugin;
    private final Random random = new Random();
    
    public ConfigManager(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
    }
    
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    
    public void reloadConfig() {
        plugin.reloadConfig();
    }
    
    // Telegram settings
    public boolean isTelegramEnabled() {
        return getConfig().getBoolean("telegram.enabled", true);
    }
    
    public String getTelegramBotToken() {
        return getConfig().getString("telegram.bot_token", "");
    }
    
    public String getTelegramBotUsername() {
        return getConfig().getString("telegram.bot_username", "");
    }
    
    // Claim settings
    public int getMaxClaimsPerPlayer() {
        return getConfig().getInt("claims.max_claims_per_player", 5);
    }
    
    public int getMaxClaimArea() {
        return getConfig().getInt("claims.max_claim_area", 2500);
    }
    
    public int getMinClaimArea() {
        return getConfig().getInt("claims.min_claim_area", 25);
    }
    
    public int getMaxClaimWidth() {
        return getConfig().getInt("claims.max_claim_width", 100);
    }
    
    public int getMaxClaimLength() {
        return getConfig().getInt("claims.max_claim_length", 100);
    }
    
    // Hologram settings
    public boolean areHologramsEnabled() {
        return getConfig().getBoolean("holograms.enabled", true);
    }
    
    public double getHologramHeightOffset() {
        return getConfig().getDouble("holograms.height_offset", 2.0);
    }
    
    public int getHologramDuration() {
        return getConfig().getInt("holograms.duration_seconds", 5);
    }
    
    // Messages
    public String getRandomMessage(String type, String ownerName) {
        List<String> messages = getConfig().getStringList("messages." + type);
        if (messages.isEmpty()) {
            return plugin.getLangManager().getMessage("protection.default_message", ownerName);
        }
        
        String message = messages.get(random.nextInt(messages.size()));
        return message.replace("{owner}", ownerName);
    }
    
    // Notification settings
    public boolean isServerBroadcastEnabled() {
        return getConfig().getBoolean("notifications.server_broadcast", true);
    }
    
    public boolean areTelegramNotificationsEnabled() {
        return getConfig().getBoolean("notifications.telegram_notifications", true);
    }
    
    public boolean isActionLoggingEnabled() {
        return getConfig().getBoolean("notifications.log_actions", true);
    }
    
    public int getNotificationDelay() {
        return getConfig().getInt("notifications.group_delay_seconds", 3);
    }
    
    // Border visualization settings
    public boolean isBorderVisualizationEnabled() {
        return getConfig().getBoolean("border_visualization.enabled", true);
    }
    
    public int getBorderVisualizationDuration() {
        return getConfig().getInt("border_visualization.duration_seconds", 10);
    }
    
    public int getBorderVisualizationInterval() {
        return getConfig().getInt("border_visualization.particle_interval_ticks", 10);
    }

    // Locale setting
    public String getLocale() {
        return getConfig().getString("locale", "en_us");
    }
}