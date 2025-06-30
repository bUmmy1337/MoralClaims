package ru.moralclaims.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import ru.moralclaims.MoralClaimsPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HologramManager {
    private final MoralClaimsPlugin plugin;
    private final Map<UUID, ArmorStand> activeHolograms = new HashMap<>();
    
    public HologramManager(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void showHologram(Player player, Location location, String message) {
        if (!plugin.getConfigManager().areHologramsEnabled()) {
            return;
        }
        
        // Remove existing hologram for this player
        removeHologram(player);
        
        // Create new hologram
        Location hologramLocation = location.clone().add(0, plugin.getConfigManager().getHologramHeightOffset(), 0);
        ArmorStand hologram = (ArmorStand) location.getWorld().spawnEntity(hologramLocation, EntityType.ARMOR_STAND);
        
        hologram.setVisible(false);
        hologram.setGravity(false);
        hologram.setCanPickupItems(false);
        hologram.setCustomName(message);
        hologram.setCustomNameVisible(true);
        hologram.setMarker(true);
        hologram.setInvulnerable(true);
        hologram.setSmall(true);
        hologram.setBasePlate(false);
        hologram.setArms(false);
        
        activeHolograms.put(player.getUniqueId(), hologram);
        
        // Schedule removal
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            removeHologram(player);
        }, plugin.getConfigManager().getHologramDuration() * 20L);
    }
    
    public void removeHologram(Player player) {
        ArmorStand hologram = activeHolograms.remove(player.getUniqueId());
        if (hologram != null && !hologram.isDead()) {
            hologram.remove();
        }
    }
    
    public void cleanup() {
        for (ArmorStand hologram : activeHolograms.values()) {
            if (!hologram.isDead()) {
                hologram.remove();
            }
        }
        activeHolograms.clear();
    }
}