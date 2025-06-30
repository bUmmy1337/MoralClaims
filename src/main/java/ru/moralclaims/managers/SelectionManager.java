package ru.moralclaims.managers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.moralclaims.MoralClaimsPlugin;

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
        player.sendMessage("§aПервая точка установлена: §f" + formatLocation(location));
        
        if (hasSecondPosition(player)) {
            showSelection(player);
        }
    }
    
    public void setSecondPosition(Player player, Location location) {
        secondPositions.put(player.getUniqueId(), location.clone());
        player.sendMessage("§aВторая точка установлена: §f" + formatLocation(location));
        
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
        player.sendMessage("§7Выделение очищено");
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
        player.sendMessage(String.format("§6Выделена область: §f%dx%d §6(площадь: %d блоков)", 
                Math.abs(pos1.getBlockX() - pos2.getBlockX()) + 1,
                Math.abs(pos1.getBlockZ() - pos2.getBlockZ()) + 1,
                area));
        
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
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        
        double y = Math.max(pos1.getY(), pos2.getY()) + 1;
        
        // Рисуем границы
        for (int x = minX; x <= maxX; x++) {
            // Верхняя и нижняя границы
            player.spawnParticle(Particle.HAPPY_VILLAGER, x + 0.5, y, minZ + 0.5, 1, 0, 0, 0, 0);
            player.spawnParticle(Particle.HAPPY_VILLAGER, x + 0.5, y, maxZ + 0.5, 1, 0, 0, 0, 0);
        }
        
        for (int z = minZ; z <= maxZ; z++) {
            // Левая и правая границы
            player.spawnParticle(Particle.HAPPY_VILLAGER, minX + 0.5, y, z + 0.5, 1, 0, 0, 0, 0);
            player.spawnParticle(Particle.HAPPY_VILLAGER, maxX + 0.5, y, z + 0.5, 1, 0, 0, 0, 0);
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