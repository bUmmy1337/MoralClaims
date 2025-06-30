package ru.moralclaims.version;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import ru.moralclaims.version.VersionManager.MinecraftVersion;

/**
 * Адаптер для работы с частицами в разных версиях Minecraft
 */
public class ParticleAdapter {
    
    private final VersionManager versionManager;
    
    public ParticleAdapter() {
        this.versionManager = VersionManager.getInstance();
    }
    
    /**
     * Создать частицу счастливого жителя для выделения
     */
    public void spawnSelectionParticle(Player player, Location location) {
        try {
            // В большинстве версий доступна VILLAGER_HAPPY
            Particle particle = getParticleByName("VILLAGER_HAPPY");
            if (particle == null) {
                particle = getParticleByName("HAPPY_VILLAGER");
            }
            if (particle != null) {
                player.spawnParticle(particle, location, 1, 0, 0, 0, 0);
                return;
            }
        } catch (Exception e) {
            // Игнорируем
        }
        
        // Fallback на простые частицы
        try {
            player.spawnParticle(Particle.HEART, location, 1, 0, 0, 0, 0);
        } catch (Exception e2) {
            // Если и это не работает, используем самые базовые
            try {
                player.spawnParticle(Particle.FLAME, location, 1, 0, 0, 0, 0);
            } catch (Exception e3) {
                // Последний fallback
                player.spawnParticle(Particle.SMOKE_NORMAL, location, 1, 0, 0, 0, 0);
            }
        }
    }
    
    /**
     * Создать красную пылевую частицу для границ
     */
    public void spawnBorderParticle(Player player, Location location, float size) {
        try {
            // Пытаемся использовать DUST с красным цветом
            Particle dustParticle = getParticleByName("DUST");
            if (dustParticle != null && supportsDustOptions()) {
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, size);
                player.spawnParticle(dustParticle, location, 1, 0, 0, 0, 0, dustOptions);
                return;
            }
            
            // Fallback на REDSTONE для старых версий
            Particle redstoneParticle = getParticleByName("REDSTONE");
            if (redstoneParticle != null) {
                player.spawnParticle(redstoneParticle, location, 1, 0, 0, 0, 0);
                return;
            }
        } catch (Exception e) {
            // Игнорируем
        }
        
        // Универсальный fallback
        try {
            player.spawnParticle(Particle.FLAME, location, 1, 0, 0, 0, 0);
        } catch (Exception e2) {
            // Последний fallback
            try {
                player.spawnParticle(Particle.SMOKE_NORMAL, location, 1, 0, 0, 0, 0);
            } catch (Exception e3) {
                // Совсем последний fallback
                player.spawnParticle(Particle.CRIT, location, 1, 0, 0, 0, 0);
            }
        }
    }
    
    /**
     * Создать частицу с координатами
     */
    public void spawnParticle(Player player, Particle particle, double x, double y, double z, int count) {
        try {
            player.spawnParticle(particle, x, y, z, count, 0, 0, 0, 0);
        } catch (Exception e) {
            // Fallback на базовые частицы
            try {
                player.spawnParticle(Particle.FLAME, x, y, z, count, 0, 0, 0, 0);
            } catch (Exception e2) {
                // Игнорируем если ��овсем ничего не работает
            }
        }
    }
    
    /**
     * Получить доступную частицу для выделения
     */
    public Particle getSelectionParticle() {
        Particle particle = getParticleByName("VILLAGER_HAPPY");
        if (particle != null) return particle;
        
        particle = getParticleByName("HAPPY_VILLAGER");
        if (particle != null) return particle;
        
        return Particle.HEART;
    }
    
    /**
     * Получить доступную частицу для границ
     */
    public Particle getBorderParticle() {
        Particle particle = getParticleByName("DUST");
        if (particle != null) return particle;
        
        particle = getParticleByName("REDSTONE");
        if (particle != null) return particle;
        
        return Particle.FLAME;
    }
    
    /**
     * Проверить, поддерживается ли частица DUST с опциями
     */
    public boolean supportsDustOptions() {
        try {
            // Пытаемся создать DustOptions
            new Particle.DustOptions(Color.RED, 1.0f);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Безопасное получение частицы по имени
     */
    private Particle getParticleByName(String name) {
        try {
            return Particle.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}