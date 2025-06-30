package ru.moralclaims.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import ru.moralclaims.MoralClaimsPlugin;
import ru.moralclaims.models.Claim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BorderVisualizationManager {
    private final MoralClaimsPlugin plugin;
    private final Map<UUID, BukkitTask> activeBorders = new HashMap<>();
    
    public BorderVisualizationManager(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void showClaimBorder(Player player, Claim claim) {
        if (!plugin.getConfigManager().isBorderVisualizationEnabled()) {
            return;
        }
        
        UUID playerId = player.getUniqueId();
        
        // Останавливаем предыдущую визуализацию если есть
        stopBorderVisualization(playerId);
        
        // Получаем точки границы
        List<Location> borderPoints = calculateBorderPoints(claim);
        
        // Запускаем визуализацию
        int interval = plugin.getConfigManager().getBorderVisualizationInterval();
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline()) {
                stopBorderVisualization(playerId);
                return;
            }
            
            showBorderParticles(player, borderPoints);
        }, 0L, interval);
        
        activeBorders.put(playerId, task);
        
        // Автоматически останавливаем через настроенное время
        int duration = plugin.getConfigManager().getBorderVisualizationDuration();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            stopBorderVisualization(playerId);
        }, duration * 20L); // Конвертируем секунды в тики
    }
    
    private List<Location> calculateBorderPoints(Claim claim) {
        List<Location> points = new ArrayList<>();
        World world = claim.getWorld();
        
        int minX = claim.getMinX();
        int maxX = claim.getMaxX();
        int minZ = claim.getMinZ();
        int maxZ = claim.getMaxZ();
        
        // Обходим периметр привата
        // Верхняя граница (Z = minZ)
        for (int x = minX; x <= maxX; x++) {
            Location loc = new Location(world, x, 0, minZ);
            int groundY = findGroundLevel(loc);
            if (groundY != -1) {
                points.add(new Location(world, x + 0.5, groundY + 0.1, minZ + 0.5));
            }
        }
        
        // Правая граница (X = maxX)
        for (int z = minZ + 1; z <= maxZ; z++) {
            Location loc = new Location(world, maxX, 0, z);
            int groundY = findGroundLevel(loc);
            if (groundY != -1) {
                points.add(new Location(world, maxX + 0.5, groundY + 0.1, z + 0.5));
            }
        }
        
        // Нижняя граница (Z = maxZ)
        for (int x = maxX - 1; x >= minX; x--) {
            Location loc = new Location(world, x, 0, maxZ);
            int groundY = findGroundLevel(loc);
            if (groundY != -1) {
                points.add(new Location(world, x + 0.5, groundY + 0.1, maxZ + 0.5));
            }
        }
        
        // Левая граница (X = minX)
        for (int z = maxZ - 1; z >= minZ + 1; z--) {
            Location loc = new Location(world, minX, 0, z);
            int groundY = findGroundLevel(loc);
            if (groundY != -1) {
                points.add(new Location(world, minX + 0.5, groundY + 0.1, z + 0.5));
            }
        }
        
        return points;
    }
    
    private int findGroundLevel(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int z = location.getBlockZ();
        
        // Ищем самый высокий твердый блок
        for (int y = world.getMaxHeight() - 1; y >= world.getMinHeight(); y--) {
            Material material = world.getBlockAt(x, y, z).getType();
            if (material.isSolid() && !material.isAir()) {
                return y;
            }
        }
        
        // Если не найден твердый блок, используем уровень моря
        return world.getSeaLevel();
    }
    
    private void showBorderParticles(Player player, List<Location> borderPoints) {
        for (Location point : borderPoints) {
            // Проверяем, что игрок достаточно близко для отображения частиц
            if (player.getLocation().distance(point) <= 64) {
                // Показываем красные частицы
                player.spawnParticle(
                    Particle.DUST, 
                    point, 
                    1, 
                    0, 0, 0, 0,
                    new Particle.DustOptions(org.bukkit.Color.RED, 1.0f)
                );
                
                // Добавляем дополнительные частицы для лучшей видимости
                Location upperPoint = point.clone().add(0, 0.5, 0);
                player.spawnParticle(
                    Particle.DUST, 
                    upperPoint, 
                    1, 
                    0, 0, 0, 0,
                    new Particle.DustOptions(org.bukkit.Color.RED, 0.8f)
                );
            }
        }
    }
    
    public void stopBorderVisualization(UUID playerId) {
        BukkitTask task = activeBorders.remove(playerId);
        if (task != null) {
            task.cancel();
        }
    }
    
    public void stopBorderVisualization(Player player) {
        stopBorderVisualization(player.getUniqueId());
    }
    
    public void cleanup() {
        for (BukkitTask task : activeBorders.values()) {
            task.cancel();
        }
        activeBorders.clear();
    }
}