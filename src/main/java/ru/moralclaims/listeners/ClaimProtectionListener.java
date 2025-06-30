package ru.moralclaims.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.moralclaims.MoralClaimsPlugin;
import ru.moralclaims.models.Claim;
import ru.moralclaims.version.MaterialAdapter;

public class ClaimProtectionListener implements Listener {
    private final MoralClaimsPlugin plugin;
    
    public ClaimProtectionListener(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Claim claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
        
        if (claim != null && !claim.isTrusted(player)) {
            event.setCancelled(true);
            
            String message = plugin.getConfigManager().getRandomMessage("build_denied", claim.getOwnerName());
            plugin.getHologramManager().showHologram(player, event.getBlock().getLocation(), message);
            
            // Show claim border
            plugin.getBorderVisualizationManager().showClaimBorder(player, claim);
            
            // Send notifications
            plugin.getNotificationManager().addNotification(claim, player, "пытается строить", event.getBlock().getLocation());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Claim claim = plugin.getClaimManager().getClaimAt(event.getBlock().getLocation());
        
        if (claim != null && !claim.isTrusted(player)) {
            event.setCancelled(true);
            
            String message = plugin.getConfigManager().getRandomMessage("break_denied", claim.getOwnerName());
            plugin.getHologramManager().showHologram(player, event.getBlock().getLocation(), message);
            
            // Show claim border
            plugin.getBorderVisualizationManager().showClaimBorder(player, claim);
            
            // Send notifications
            plugin.getNotificationManager().addNotification(claim, player, "пытается ломать блоки", event.getBlock().getLocation());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        
        Player player = event.getPlayer();
        Claim claim = plugin.getClaimManager().getClaimAt(event.getClickedBlock().getLocation());
        
        if (claim != null && !claim.isTrusted(player)) {
            MaterialAdapter materialAdapter = plugin.getMaterialAdapter();
            
            // Check if it's an interaction that should be blocked
            if (materialAdapter.isInteractable(event.getClickedBlock().getType())) {
                event.setCancelled(true);
                
                String message = plugin.getConfigManager().getRandomMessage("interact_denied", claim.getOwnerName());
                plugin.getHologramManager().showHologram(player, event.getClickedBlock().getLocation(), message);
                
                // Show claim border
                plugin.getBorderVisualizationManager().showClaimBorder(player, claim);
                
                // Send notifications
                plugin.getNotificationManager().addNotification(claim, player, "пытается взаимодействовать с блоками", event.getClickedBlock().getLocation());
            }
        }
    }
    
    }