package ru.moralclaims.version;

import org.bukkit.Bukkit;

/**
 * Менеджер для определения версии сервера и управления совместимостью
 */
public class VersionManager {
    
    private static VersionManager instance;
    private final MinecraftVersion version;
    
    public enum MinecraftVersion {
        V1_16(16, 0),
        V1_17(17, 0),
        V1_18(18, 0),
        V1_19(19, 0),
        V1_20(20, 0),
        V1_21(21, 0),
        UNKNOWN(0, 0);
        
        private final int major;
        private final int minor;
        
        MinecraftVersion(int major, int minor) {
            this.major = major;
            this.minor = minor;
        }
        
        public int getMajor() {
            return major;
        }
        
        public int getMinor() {
            return minor;
        }
        
        public boolean isAtLeast(MinecraftVersion other) {
            if (this.major > other.major) return true;
            if (this.major < other.major) return false;
            return this.minor >= other.minor;
        }
        
        public boolean isBelow(MinecraftVersion other) {
            return !isAtLeast(other);
        }
    }
    
    private VersionManager() {
        this.version = detectVersion();
    }
    
    public static VersionManager getInstance() {
        if (instance == null) {
            instance = new VersionManager();
        }
        return instance;
    }
    
    private MinecraftVersion detectVersion() {
        String version = Bukkit.getVersion();
        
        // Парсим версию из строки типа "git-Paper-XXX (MC: 1.21.4)"
        if (version.contains("1.16")) {
            return MinecraftVersion.V1_16;
        } else if (version.contains("1.17")) {
            return MinecraftVersion.V1_17;
        } else if (version.contains("1.18")) {
            return MinecraftVersion.V1_18;
        } else if (version.contains("1.19")) {
            return MinecraftVersion.V1_19;
        } else if (version.contains("1.20")) {
            return MinecraftVersion.V1_20;
        } else if (version.contains("1.21")) {
            return MinecraftVersion.V1_21;
        }
        
        // Альтернативный способ через package name
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        if (packageName.contains("v1_16")) {
            return MinecraftVersion.V1_16;
        } else if (packageName.contains("v1_17")) {
            return MinecraftVersion.V1_17;
        } else if (packageName.contains("v1_18")) {
            return MinecraftVersion.V1_18;
        } else if (packageName.contains("v1_19")) {
            return MinecraftVersion.V1_19;
        } else if (packageName.contains("v1_20")) {
            return MinecraftVersion.V1_20;
        } else if (packageName.contains("v1_21")) {
            return MinecraftVersion.V1_21;
        }
        
        return MinecraftVersion.UNKNOWN;
    }
    
    public MinecraftVersion getVersion() {
        return version;
    }
    
    public boolean isVersion(MinecraftVersion version) {
        return this.version == version;
    }
    
    public boolean isAtLeast(MinecraftVersion version) {
        return this.version.isAtLeast(version);
    }
    
    public boolean isBelow(MinecraftVersion version) {
        return this.version.isBelow(version);
    }
    
    public String getVersionString() {
        return version.name();
    }
}