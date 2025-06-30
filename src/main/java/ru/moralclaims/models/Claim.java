package ru.moralclaims.models;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Claim {
    private final UUID id;
    private final UUID ownerId;
    private final String ownerName;
    private final World world;
    private final int minX;
    private final int minZ;
    private final int maxX;
    private final int maxZ;
    private final long createdAt;
    private final Set<UUID> trustedPlayers;
    
    public Claim(UUID ownerId, String ownerName, Location pos1, Location pos2) {
        this.id = UUID.randomUUID();
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.world = pos1.getWorld();
        this.minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
        this.createdAt = System.currentTimeMillis();
        this.trustedPlayers = new HashSet<>();
    }
    
    public Claim(UUID id, UUID ownerId, String ownerName, World world, int minX, int minZ, int maxX, int maxZ, long createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.world = world;
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
        this.createdAt = createdAt;
        this.trustedPlayers = new HashSet<>();
    }
    
    public Claim(UUID id, UUID ownerId, String ownerName, World world, int minX, int minZ, int maxX, int maxZ, long createdAt, Set<UUID> trustedPlayers) {
        this.id = id;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.world = world;
        this.minX = minX;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxZ = maxZ;
        this.createdAt = createdAt;
        this.trustedPlayers = trustedPlayers != null ? new HashSet<>(trustedPlayers) : new HashSet<>();
    }
    
    public boolean contains(Location location) {
        if (!location.getWorld().equals(world)) {
            return false;
        }
        
        int x = location.getBlockX();
        int z = location.getBlockZ();
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }
    
    public boolean overlaps(Claim other) {
        if (!world.equals(other.world)) {
            return false;
        }
        
        return !(maxX < other.minX || minX > other.maxX || maxZ < other.minZ || minZ > other.maxZ);
    }
    
    public boolean isOwner(Player player) {
        return ownerId.equals(player.getUniqueId());
    }
    
    public boolean isTrusted(Player player) {
        return isOwner(player) || trustedPlayers.contains(player.getUniqueId());
    }
    
    public boolean addTrustedPlayer(UUID playerId) {
        return trustedPlayers.add(playerId);
    }
    
    public boolean removeTrustedPlayer(UUID playerId) {
        return trustedPlayers.remove(playerId);
    }
    
    public Set<UUID> getTrustedPlayers() {
        return new HashSet<>(trustedPlayers);
    }
    
    public Location getCenterLocation() {
        int centerX = (minX + maxX) / 2;
        int centerZ = (minZ + maxZ) / 2;
        return new Location(world, centerX, world.getHighestBlockYAt(centerX, centerZ) + 1, centerZ);
    }
    
    public int getArea() {
        return (maxX - minX + 1) * (maxZ - minZ + 1);
    }
    
    public int getWidth() {
        return maxX - minX + 1;
    }
    
    public int getLength() {
        return maxZ - minZ + 1;
    }
    
    // Getters
    public UUID getId() { return id; }
    public UUID getOwnerId() { return ownerId; }
    public String getOwnerName() { return ownerName; }
    public World getWorld() { return world; }
    public int getMinX() { return minX; }
    public int getMinZ() { return minZ; }
    public int getMaxX() { return maxX; }
    public int getMaxZ() { return maxZ; }
    public long getCreatedAt() { return createdAt; }
    
    @Override
    public String toString() {
        return String.format("Claim{owner=%s, area=(%d,%d) to (%d,%d), size=%dx%d}", 
                ownerName, minX, minZ, maxX, maxZ, getWidth(), getLength());
    }
}