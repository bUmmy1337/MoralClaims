package ru.moralclaims;

import org.bukkit.plugin.java.JavaPlugin;
import ru.moralclaims.commands.ClaimCommand;
import ru.moralclaims.commands.TelegramCommand;
import ru.moralclaims.listeners.ClaimProtectionListener;
import ru.moralclaims.listeners.SelectionListener;
import ru.moralclaims.managers.BorderVisualizationManager;
import ru.moralclaims.managers.ClaimManager;
import ru.moralclaims.managers.ConfigManager;
import ru.moralclaims.managers.ConfirmationManager;
import ru.moralclaims.managers.HologramManager;
import ru.moralclaims.managers.NotificationManager;
import ru.moralclaims.managers.SelectionManager;
import ru.moralclaims.managers.TelegramManager;

public class MoralClaimsPlugin extends JavaPlugin {
    
    private static MoralClaimsPlugin instance;
    private ConfigManager configManager;
    private ClaimManager claimManager;
    private HologramManager hologramManager;
    private SelectionManager selectionManager;
    private ConfirmationManager confirmationManager;
    private NotificationManager notificationManager;
    private BorderVisualizationManager borderVisualizationManager;
    private TelegramManager telegramManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize managers
        configManager = new ConfigManager(this);
        claimManager = new ClaimManager(this);
        hologramManager = new HologramManager(this);
        selectionManager = new SelectionManager(this);
        confirmationManager = new ConfirmationManager(this);
        notificationManager = new NotificationManager(this);
        borderVisualizationManager = new BorderVisualizationManager(this);
        telegramManager = new TelegramManager(this);
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        // Start Telegram bot if enabled
        if (configManager.isTelegramEnabled()) {
            telegramManager.startBot();
        }
        
        getLogger().info("MoralClaims plugin enabled!");
    }
    
    @Override
    public void onDisable() {
        if (telegramManager != null) {
            telegramManager.stopBot();
        }
        
        if (hologramManager != null) {
            hologramManager.cleanup();
        }
        
        if (selectionManager != null) {
            selectionManager.cleanup();
        }
        
        if (confirmationManager != null) {
            confirmationManager.cleanup();
        }
        
        if (notificationManager != null) {
            notificationManager.cleanup();
        }
        
        if (borderVisualizationManager != null) {
            borderVisualizationManager.cleanup();
        }
        
        getLogger().info("MoralClaims plugin disabled!");
    }
    
    private void registerCommands() {
        ClaimCommand claimCommand = new ClaimCommand(this);
        getCommand("claim").setExecutor(claimCommand);
        getCommand("unclaim").setExecutor(claimCommand);
        getCommand("claimlist").setExecutor(claimCommand);
        getCommand("claimtrust").setExecutor(claimCommand);
        getCommand("claimuntrust").setExecutor(claimCommand);
        getCommand("clearselection").setExecutor(claimCommand);
        
        getCommand("telegram").setExecutor(new TelegramCommand(this));
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ClaimProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new SelectionListener(this), this);
    }
    
    public static MoralClaimsPlugin getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public ClaimManager getClaimManager() {
        return claimManager;
    }
    
    public HologramManager getHologramManager() {
        return hologramManager;
    }
    
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
    
    public ConfirmationManager getConfirmationManager() {
        return confirmationManager;
    }
    
    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
    
    public BorderVisualizationManager getBorderVisualizationManager() {
        return borderVisualizationManager;
    }
    
    public TelegramManager getTelegramManager() {
        return telegramManager;
    }
}