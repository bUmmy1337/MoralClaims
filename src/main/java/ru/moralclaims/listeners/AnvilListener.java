package ru.moralclaims.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.moralclaims.MoralClaimsPlugin;
import ru.moralclaims.version.MaterialAdapter;

import java.util.Arrays;

public class AnvilListener implements Listener {
    private final MoralClaimsPlugin plugin;
    private static final String SELECTION_TOOL_NAME = ""; // Will be loaded from lang file
    
    public AnvilListener(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack firstItem = inventory.getItem(0);
        
        if (firstItem == null) {
            return;
        }
        
        MaterialAdapter materialAdapter = plugin.getMaterialAdapter();
        Material selectionTool = materialAdapter.getSelectionTool();
        
        if (firstItem.getType() != selectionTool) {
            return;
        }
        
        String renameText = inventory.getRenameText();
        String selectionToolName = plugin.getLangManager().getMessage("anvil.tool_name");
        if (renameText == null || !renameText.equals(selectionToolName)) {
            return;
        }
        
        ItemStack result = event.getResult();
        if (result == null) {
            result = firstItem.clone();
        }
        
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6" + plugin.getLangManager().getMessage("anvil.tool_name"));
            meta.setLore(Arrays.asList(
                plugin.getLangManager().getMessage("anvil.lore_line_1"),
                plugin.getLangManager().getMessage("anvil.lore_line_2"),
                plugin.getLangManager().getMessage("anvil.lore_line_3"),
                plugin.getLangManager().getMessage("anvil.lore_line_4"),
                plugin.getLangManager().getMessage("anvil.lore_line_5")
            ));
            result.setItemMeta(meta);
            event.setResult(result);
        }
    }
}