package ru.moralclaims.managers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.moralclaims.MoralClaimsPlugin;
import ru.moralclaims.models.Claim;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ClaimManager {
    private final MoralClaimsPlugin plugin;
    private final Map<UUID, Claim> claims = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File dataFile;
    
    public ClaimManager(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "claims.json");
        loadClaims();
    }
    
    public boolean createClaim(Player player, Location pos1, Location pos2) {
        ConfigManager config = plugin.getConfigManager();
        
        // Check limits
        if (getPlayerClaims(player).size() >= config.getMaxClaimsPerPlayer()) {
            return false;
        }
        
        // Check area limits
        int width = Math.abs(pos1.getBlockX() - pos2.getBlockX()) + 1;
        int length = Math.abs(pos1.getBlockZ() - pos2.getBlockZ()) + 1;
        int area = width * length;
        
        if (area > config.getMaxClaimArea() || area < config.getMinClaimArea()) {
            return false;
        }
        
        // Check for overlapping claims
        Claim newClaim = new Claim(player.getUniqueId(), player.getName(), pos1, pos2);
        if (hasOverlappingClaim(newClaim)) {
            return false;
        }
        
        claims.put(newClaim.getId(), newClaim);
        saveClaims();
        
        return true;
    }
    
    public boolean removeClaim(Player player, Location location) {
        Claim claim = getClaimAt(location);
        if (claim != null && claim.isOwner(player)) {
            claims.remove(claim.getId());
            saveClaims();
            return true;
        }
        return false;
    }
    
    public Claim getClaimAt(Location location) {
        return claims.values().stream()
                .filter(claim -> claim.contains(location))
                .findFirst()
                .orElse(null);
    }
    
    public List<Claim> getPlayerClaims(Player player) {
        return claims.values().stream()
                .filter(claim -> claim.getOwnerId().equals(player.getUniqueId()))
                .collect(Collectors.toList());
    }
    
    public boolean hasOverlappingClaim(Claim newClaim) {
        return claims.values().stream()
                .anyMatch(existingClaim -> existingClaim.overlaps(newClaim));
    }
    
    private void loadClaims() {
        if (!dataFile.exists()) {
            return;
        }
        
        try {
            List<Map<String, Object>> claimData = objectMapper.readValue(dataFile, new TypeReference<List<Map<String, Object>>>() {});
            
            for (Map<String, Object> data : claimData) {
                UUID id = UUID.fromString((String) data.get("id"));
                UUID ownerId = UUID.fromString((String) data.get("ownerId"));
                String ownerName = (String) data.get("ownerName");
                World world = Bukkit.getWorld((String) data.get("world"));
                int minX = (Integer) data.get("minX");
                int minZ = (Integer) data.get("minZ");
                int maxX = (Integer) data.get("maxX");
                int maxZ = (Integer) data.get("maxZ");
                long createdAt = ((Number) data.get("createdAt")).longValue();
                
                // Load trusted players
                Set<UUID> trustedPlayers = new HashSet<>();
                if (data.containsKey("trustedPlayers")) {
                    @SuppressWarnings("unchecked")
                    List<String> trustedList = (List<String>) data.get("trustedPlayers");
                    for (String trustedId : trustedList) {
                        try {
                            trustedPlayers.add(UUID.fromString(trustedId));
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning("Invalid trusted player UUID: " + trustedId);
                        }
                    }
                }
                
                if (world != null) {
                    Claim claim = new Claim(id, ownerId, ownerName, world, minX, minZ, maxX, maxZ, createdAt, trustedPlayers);
                    claims.put(id, claim);
                }
            }
            
            plugin.getLogger().info("Loaded " + claims.size() + " claims");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load claims: " + e.getMessage());
        }
    }
    
    public void saveClaims() {
        try {
            plugin.getDataFolder().mkdirs();
            
            List<Map<String, Object>> claimData = new ArrayList<>();
            for (Claim claim : claims.values()) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", claim.getId().toString());
                data.put("ownerId", claim.getOwnerId().toString());
                data.put("ownerName", claim.getOwnerName());
                data.put("world", claim.getWorld().getName());
                data.put("minX", claim.getMinX());
                data.put("minZ", claim.getMinZ());
                data.put("maxX", claim.getMaxX());
                data.put("maxZ", claim.getMaxZ());
                data.put("createdAt", claim.getCreatedAt());
                
                // Save trusted players
                List<String> trustedList = new ArrayList<>();
                for (UUID trustedId : claim.getTrustedPlayers()) {
                    trustedList.add(trustedId.toString());
                }
                data.put("trustedPlayers", trustedList);
                
                claimData.add(data);
            }
            
            objectMapper.writeValue(dataFile, claimData);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save claims: " + e.getMessage());
        }
    }
    
    public Collection<Claim> getAllClaims() {
        return claims.values();
    }
}