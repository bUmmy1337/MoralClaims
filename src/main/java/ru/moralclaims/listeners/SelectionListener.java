package ru.moralclaims.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.moralclaims.MoralClaimsPlugin;
import ru.moralclaims.version.MaterialAdapter;

public class SelectionListener implements Listener {
    private final MoralClaimsPlugin plugin;
    
    public SelectionListener(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        // Проверяем, что это кисточка археолога с нужным именем
        if (!isSelectionTool(item)) {
            return;
        }
        
        if (event.getClickedBlock() == null) {
            return;
        }
        
        event.setCancelled(true); // Отменяем обычное взаимодействие
        
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            // Левый клик - первая точка
            plugin.getSelectionManager().setFirstPosition(player, event.getClickedBlock().getLocation());
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            // Правый клик - вторая точка
            plugin.getSelectionManager().setSecondPosition(player, event.getClickedBlock().getLocation());
        }
    }
    
    private boolean isSelectionTool(ItemStack item) {
        MaterialAdapter materialAdapter = plugin.getMaterialAdapter();
        
        if (item == null || item.getType() != materialAdapter.getSelectionTool()) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return false;
        }
        
        String toolNameKey = plugin.getConfig().getString("selection.tool_name_key", "anvil.tool_name");
        String toolName = plugin.getLangManager().getMessage(toolNameKey);
        return toolName.equals(meta.getDisplayName()) || 
               ("§6" + toolName).equals(meta.getDisplayName());
    }
}