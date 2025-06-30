package ru.moralclaims.version;

import org.bukkit.Material;
import ru.moralclaims.version.VersionManager.MinecraftVersion;

/**
 * Адаптер для работы с Material в разных версиях Minecraft
 */
public class MaterialAdapter {
    
    private final VersionManager versionManager;
    
    public MaterialAdapter() {
        this.versionManager = VersionManager.getInstance();
    }
    
    /**
     * Получить материал кисти для выделения территории
     */
    public Material getSelectionTool() {
        // В версиях до 1.20 используем золотую лопату, в 1.20+ кисть
        if (versionManager.isBelow(MinecraftVersion.V1_20)) {
            return Material.GOLDEN_SHOVEL;
        } else {
            Material brush = getMaterial("BRUSH");
            return brush != null ? brush : Material.GOLDEN_SHOVEL;
        }
    }
    
    /**
     * Получить материал сундука
     */
    public Material getChest() {
        return Material.CHEST;
    }
    
    /**
     * Получить материал двери (универсальная проверка)
     */
    public boolean isDoor(Material material) {
        String name = material.name();
        return name.contains("_DOOR") && !name.contains("TRAPDOOR");
    }
    
    /**
     * Получить материал кнопки
     */
    public boolean isButton(Material material) {
        return material.name().contains("_BUTTON");
    }
    
    /**
     * Получить материал рычага
     */
    public Material getLever() {
        return Material.LEVER;
    }
    
    /**
     * Получить материал нажимной плиты
     */
    public boolean isPressurePlate(Material material) {
        return material.name().contains("_PRESSURE_PLATE");
    }
    
    /**
     * Получить материал люка
     */
    public boolean isTrapdoor(Material material) {
        return material.name().contains("_TRAPDOOR");
    }
    
    /**
     * Получить материал ворот
     */
    public boolean isFenceGate(Material material) {
        return material.name().contains("_FENCE_GATE");
    }
    
    /**
     * Проверить, является ли материал интерактивным блоком
     */
    public boolean isInteractable(Material material) {
        if (material == null) return false;
        
        String name = material.name();
        
        // Основные интерактивные блоки
        if (material == Material.CHEST || 
            material == Material.TRAPPED_CHEST ||
            material == Material.ENDER_CHEST ||
            material == Material.BARREL ||
            material == Material.SHULKER_BOX ||
            material == Material.FURNACE ||
            material == Material.BLAST_FURNACE ||
            material == Material.SMOKER ||
            material == Material.BREWING_STAND ||
            material == Material.ENCHANTING_TABLE ||
            material == Material.ANVIL ||
            material == Material.CRAFTING_TABLE ||
            material == Material.LEVER ||
            material == Material.COMPARATOR ||
            material == Material.REPEATER) {
            return true;
        }
        
        // Проверяем по паттернам имен
        return isDoor(material) || 
               isButton(material) || 
               isPressurePlate(material) || 
               isTrapdoor(material) || 
               isFenceGate(material) ||
               name.contains("_SHULKER_BOX") ||
               name.contains("_BED");
    }
    
    /**
     * Безопасное получение материала
     */
    private Material getMaterial(String name) {
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Получить все доступные материалы шалкер-боксов
     */
    public boolean isShulkerBox(Material material) {
        if (material == null) return false;
        return material.name().contains("_SHULKER_BOX") || material == Material.SHULKER_BOX;
    }
    
    /**
     * Проверить, является ли материал кроватью
     */
    public boolean isBed(Material material) {
        if (material == null) return false;
        return material.name().contains("_BED");
    }
}