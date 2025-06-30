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
            // Check if it's an interaction that should be blocked
            switch (event.getClickedBlock().getType()) {
                case CHEST:
                case BARREL:
                case SHULKER_BOX:
                case FURNACE:
                case BLAST_FURNACE:
                case SMOKER:
                case CRAFTING_TABLE:
                case ENCHANTING_TABLE:
                case ANVIL:
                case CHIPPED_ANVIL:
                case DAMAGED_ANVIL:
                case BREWING_STAND:
                case CAULDRON:
                case WATER_CAULDRON:
                case LAVA_CAULDRON:
                case POWDER_SNOW_CAULDRON:
                case LEVER:
                case STONE_BUTTON:
                case OAK_BUTTON:
                case SPRUCE_BUTTON:
                case BIRCH_BUTTON:
                case JUNGLE_BUTTON:
                case ACACIA_BUTTON:
                case DARK_OAK_BUTTON:
                case CRIMSON_BUTTON:
                case WARPED_BUTTON:
                case POLISHED_BLACKSTONE_BUTTON:
                case MANGROVE_BUTTON:
                case BAMBOO_BUTTON:
                case CHERRY_BUTTON:
                case OAK_DOOR:
                case SPRUCE_DOOR:
                case BIRCH_DOOR:
                case JUNGLE_DOOR:
                case ACACIA_DOOR:
                case DARK_OAK_DOOR:
                case CRIMSON_DOOR:
                case WARPED_DOOR:
                case MANGROVE_DOOR:
                case BAMBOO_DOOR:
                case CHERRY_DOOR:
                case IRON_DOOR:
                    event.setCancelled(true);
                    
                    String message = plugin.getConfigManager().getRandomMessage("interact_denied", claim.getOwnerName());
                    plugin.getHologramManager().showHologram(player, event.getClickedBlock().getLocation(), message);
                    
                    // Show claim border
                    plugin.getBorderVisualizationManager().showClaimBorder(player, claim);
                    
                    // Send notifications
                    plugin.getNotificationManager().addNotification(claim, player, "пытается взаимодействовать с блоками", event.getClickedBlock().getLocation());
                    break;
                default:
                    break;
            }
        }
    }
    
    }