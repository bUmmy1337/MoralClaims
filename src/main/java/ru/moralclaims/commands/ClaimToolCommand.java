package ru.moralclaims.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.moralclaims.MoralClaimsPlugin;

public class ClaimToolCommand implements CommandExecutor {
    private final MoralClaimsPlugin plugin;
    
    public ClaimToolCommand(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLangManager().getMessage("error.no_player"));
            return true;
        }
        
        Player player = (Player) sender;
        sendToolInstructions(player);
        return true;
    }
    
    private void sendToolInstructions(Player player) {
        String toolName = getSelectionToolName();
        player.sendMessage(plugin.getLangManager().getMessage("claim.tool_instructions_header"));
        player.sendMessage(plugin.getLangManager().getMessage("claim.tool_instructions_1", toolName));
        player.sendMessage(plugin.getLangManager().getMessage("claim.tool_instructions_2"));
        player.sendMessage(plugin.getLangManager().getMessage("claim.tool_instructions_3"));
        player.sendMessage(plugin.getLangManager().getMessage("claim.tool_instructions_success"));
        player.sendMessage(plugin.getLangManager().getMessage("claim.tool_instructions_usage"));
    }
    
    private String getSelectionToolName() {
        if (plugin.getVersionManager().isAtLeast(plugin.getVersionManager().getVersion().V1_20)) {
            return plugin.getLangManager().getMessage("tool_name.brush");
        } else {
            return plugin.getLangManager().getMessage("tool_name.golden_shovel");
        }
    }
}