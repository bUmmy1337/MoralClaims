package ru.moralclaims.version;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.moralclaims.version.VersionManager.MinecraftVersion;

/**
 * Адаптер для работы со звуками в разных версиях Minecraft
 */
public class SoundAdapter {
    
    private final VersionManager versionManager;
    
    public SoundAdapter() {
        this.versionManager = VersionManager.getInstance();
    }
    
    /**
     * Воспроизвести звук успеха
     */
    public void playSuccessSound(Player player) {
        Sound sound = getSuccessSound();
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }
    
    /**
     * Воспроизвести звук ошибки
     */
    public void playErrorSound(Player player) {
        Sound sound = getErrorSound();
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }
    
    /**
     * Воспроизвести звук клика
     */
    public void playClickSound(Player player) {
        Sound sound = getClickSound();
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        }
    }
    
    /**
     * Воспроизвести звук уведомления
     */
    public void playNotificationSound(Player player) {
        Sound sound = getNotificationSound();
        if (sound != null) {
            player.playSound(player.getLocation(), sound, 1.0f, 1.2f);
        }
    }
    
    /**
     * Получить звук успеха в зависимости от версии
     */
    private Sound getSuccessSound() {
        // Пытаемся использовать современные звуки
        Sound sound = getSafeSound("ENTITY_EXPERIENCE_ORB_PICKUP");
        if (sound != null) return sound;
        
        sound = getSafeSound("ENTITY_PLAYER_LEVELUP");
        if (sound != null) return sound;
        
        // Fallback на старые звуки
        sound = getSafeSound("ORB_PICKUP");
        if (sound != null) return sound;
        
        sound = getSafeSound("LEVEL_UP");
        if (sound != null) return sound;
        
        return null;
    }
    
    /**
     * Получить звук ошибки в зависимости от версии
     */
    private Sound getErrorSound() {
        Sound sound = getSafeSound("ENTITY_VILLAGER_NO");
        if (sound != null) return sound;
        
        sound = getSafeSound("BLOCK_NOTE_BLOCK_BASS");
        if (sound != null) return sound;
        
        // Fallback на старые звуки
        sound = getSafeSound("VILLAGER_NO");
        if (sound != null) return sound;
        
        sound = getSafeSound("NOTE_BASS");
        if (sound != null) return sound;
        
        return null;
    }
    
    /**
     * Получить звук клика в зависимости от версии
     */
    private Sound getClickSound() {
        Sound sound = getSafeSound("UI_BUTTON_CLICK");
        if (sound != null) return sound;
        
        sound = getSafeSound("BLOCK_STONE_BUTTON_CLICK_ON");
        if (sound != null) return sound;
        
        // Fallback на старые звуки
        sound = getSafeSound("CLICK");
        if (sound != null) return sound;
        
        sound = getSafeSound("STONE_BUTTON_CLICK_ON");
        if (sound != null) return sound;
        
        return null;
    }
    
    /**
     * Получить звук уведомле��ия в зависимости от версии
     */
    private Sound getNotificationSound() {
        Sound sound = getSafeSound("BLOCK_NOTE_BLOCK_PLING");
        if (sound != null) return sound;
        
        sound = getSafeSound("ENTITY_EXPERIENCE_ORB_PICKUP");
        if (sound != null) return sound;
        
        // Fallback на старые звуки
        sound = getSafeSound("NOTE_PLING");
        if (sound != null) return sound;
        
        sound = getSafeSound("ORB_PICKUP");
        if (sound != null) return sound;
        
        return null;
    }
    
    /**
     * Безопасное получение звука по имени
     */
    private Sound getSafeSound(String soundName) {
        try {
            return Sound.valueOf(soundName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Воспроизвести звук с проверкой доступности
     */
    public void playSafeSound(Player player, String soundName, float volume, float pitch) {
        Sound sound = getSafeSound(soundName);
        if (sound != null) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }
}