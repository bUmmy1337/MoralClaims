package ru.moralclaims.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.moralclaims.MoralClaimsPlugin;
import ru.moralclaims.version.ParticleAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SelectionManager {
    private final MoralClaimsPlugin plugin;
    private final Map<UUID, Location> firstPositions = new HashMap<>();
    private final Map<UUID, Location> secondPositions = new HashMap<>();
    private final Map<UUID, BukkitRunnable> visualizationTasks = new HashMap<>();
    
    public SelectionManager(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void setFirstPosition(Player player, Location location) {
        firstPositions.put(player.getUniqueId(), location.clone());
        player.sendMessage(plugin.getLangManager().getMessage("selection.first_point", formatLocation(location)));
        
        if (hasSecondPosition(player)) {
            showSelection(player);
        }
    }
    
    public void setSecondPosition(Player player, Location location) {
        secondPositions.put(player.getUniqueId(), location.clone());
        player.sendMessage(plugin.getLangManager().getMessage("selection.second_point", formatLocation(location)));
        
        if (hasFirstPosition(player)) {
            showSelection(player);
        }
    }
    
    public boolean hasFirstPosition(Player player) {
        return firstPositions.containsKey(player.getUniqueId());
    }
    
    public boolean hasSecondPosition(Player player) {
        return secondPositions.containsKey(player.getUniqueId());
    }
    
    public boolean hasCompleteSelection(Player player) {
        return hasFirstPosition(player) && hasSecondPosition(player);
    }
    
    public Location getFirstPosition(Player player) {
        return firstPositions.get(player.getUniqueId());
    }
    
    public Location getSecondPosition(Player player) {
        return secondPositions.get(player.getUniqueId());
    }
    
    public void clearSelection(Player player) {
        UUID playerId = player.getUniqueId();
        firstPositions.remove(playerId);
        secondPositions.remove(playerId);
        stopVisualization(player);
        player.sendMessage(plugin.getLangManager().getMessage("selection.cleared"));
    }
    
    public int getSelectionArea(Player player) {
        if (!hasCompleteSelection(player)) {
            return 0;
        }
        
        Location pos1 = getFirstPosition(player);
        Location pos2 = getSecondPosition(player);
        
        int width = Math.abs(pos1.getBlockX() - pos2.getBlockX()) + 1;
        int length = Math.abs(pos1.getBlockZ() - pos2.getBlockZ()) + 1;
        
        return width * length;
    }
    
    private void showSelection(Player player) {
        if (!hasCompleteSelection(player)) {
            return;
        }
        
        stopVisualization(player);
        
        Location pos1 = getFirstPosition(player);
        Location pos2 = getSecondPosition(player);
        
        int area = getSelectionArea(player);
        int width = Math.abs(pos1.getBlockX() - pos2.getBlockX()) + 1;
        int length = Math.abs(pos1.getBlockZ() - pos2.getBlockZ()) + 1;
        player.sendMessage(plugin.getLangManager().getMessage("selection.area_selected", width, length, area));
        
        // Запускаем визуализацию
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                
                visualizeSelection(player, pos1, pos2);
            }
        };
        
        task.runTaskTimer(plugin, 0L, 20L); // Каждую секунду
        visualizationTasks.put(player.getUniqueId(), task);
    }
    
    private void visualizeSelection(Player player, Location pos1, Location pos2) {
        ParticleAdapter particleAdapter = plugin.getParticleAdapter();
        
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        
        double y = Math.max(pos1.getY(), pos2.getY()) + 1;
        
        // Рисуем границы
        for (int x = minX; x <= maxX; x++) {
            // Верхняя и нижняя границы
            particleAdapter.spawnSelectionParticle(player, new Location(pos1.getWorld(), x + 0.5, y, minZ + 0.5));
            particleAdapter.spawnSelectionParticle(player, new Location(pos1.getWorld(), x + 0.5, y, maxZ + 0.5));
        }
        
        for (int z = minZ; z <= maxZ; z++) {
            // Левая и правая границы
            particleAdapter.spawnSelectionParticle(player, new Location(pos1.getWorld(), minX + 0.5, y, z + 0.5));
            particleAdapter.spawnSelectionParticle(player, new Location(pos1.getWorld(), maxX + 0.5, y, z + 0.5));
        }
    }
    
    private void stopVisualization(Player player) {
        BukkitRunnable task = visualizationTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }
    
    private String formatLocation(Location location) {
        return String.format("(%d, %d, %d)", 
                location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
    
    public void cleanup() {
        for (BukkitRunnable task : visualizationTasks.values()) {
            task.cancel();
        }
        visualizationTasks.clear();
        firstPositions.clear();
        secondPositions.clear();
    }
}