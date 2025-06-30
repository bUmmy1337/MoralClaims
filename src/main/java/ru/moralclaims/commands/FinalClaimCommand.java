package ru.moralclaims.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.moralclaims.MoralClaimsPlugin;
import ru.moralclaims.managers.ClaimManager;
import ru.moralclaims.managers.ConfigManager;
import ru.moralclaims.models.Claim;

import java.util.List;

public class FinalClaimCommand implements CommandExecutor {
    private final MoralClaimsPlugin plugin;

    public FinalClaimCommand(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLangManager().getMessage("error.no_player"));
            return true;
        }

        Player player = (Player) sender;
        ClaimManager claimManager = plugin.getClaimManager();
        ConfigManager configManager = plugin.getConfigManager();

        switch (command.getName().toLowerCase()) {
            case "claim":
                return handleClaim(player, claimManager, configManager);
            case "unclaim":
                return handleUnclaim(player, claimManager);
            case "claimlist":
                return handleClaimList(player, claimManager);
            case "claimtrust":
                return handleClaimTrust(player, args, claimManager);
            case "claimuntrust":
                return handleClaimUntrust(player, args, claimManager);
            case "clearselection":
                return handleClearSelection(player);
            case "claimtool":
                sendToolInstructions(player);
                return true;
            default:
                return false;
        }
    }

    private boolean handleClaim(Player player, ClaimManager claimManager, ConfigManager configManager) {
        if (!plugin.getSelectionManager().hasCompleteSelection(player)) {
            player.sendMessage(plugin.getLangManager().getMessage("claim.no_selection"));
            return true;
        }

        Location pos1 = plugin.getSelectionManager().getFirstPosition(player);
        Location pos2 = plugin.getSelectionManager().getSecondPosition(player);

        int area = plugin.getSelectionManager().getSelectionArea(player);
        if (area > configManager.getMaxClaimArea()) {
            player.sendMessage(plugin.getLangManager().getMessage("claim.too_large", configManager.getMaxClaimArea()));
            return true;
        }

        if (area < configManager.getMinClaimArea()) {
            player.sendMessage(plugin.getLangManager().getMessage("claim.too_small", configManager.getMinClaimArea()));
            return true;
        }

        if (claimManager.createClaim(player, pos1, pos2)) {
            player.sendMessage(plugin.getLangManager().getMessage("claim.created", area));
            player.sendMessage(plugin.getLangManager().getMessage("claim.protection_info"));
            plugin.getSelectionManager().clearSelection(player);
        } else {
            List<Claim> playerClaims = claimManager.getPlayerClaims(player);
            if (playerClaims.size() >= configManager.getMaxClaimsPerPlayer()) {
                player.sendMessage(plugin.getLangManager().getMessage("claim.max_claims", configManager.getMaxClaimsPerPlayer()));
            } else {
                player.sendMessage(plugin.getLangManager().getMessage("claim.already_claimed"));
            }
        }

        return true;
    }

    private boolean handleUnclaim(Player player, ClaimManager claimManager) {
        if (claimManager.removeClaim(player, player.getLocation())) {
            player.sendMessage(plugin.getLangManager().getMessage("unclaim.success"));
        } else {
            player.sendMessage(plugin.getLangManager().getMessage("unclaim.not_your_claim"));
        }
        return true;
    }

    private boolean handleClaimList(Player player, ClaimManager claimManager) {
        List<Claim> claims = claimManager.getPlayerClaims(player);

        if (claims.isEmpty()) {
            player.sendMessage(plugin.getLangManager().getMessage("claimlist.no_claims"));
            return true;
        }

        player.sendMessage(plugin.getLangManager().getMessage("claimlist.header"));
        for (int i = 0; i < claims.size(); i++) {
            Claim claim = claims.get(i);
            player.sendMessage(plugin.getLangManager().getMessage("claimlist.item", 
                    i + 1, claim.getCenterLocation().toString(), 
                    claim.getWidth(), claim.getLength(), claim.getArea()));
        }

        return true;
    }

    private boolean handleClaimTrust(Player player, String[] args, ClaimManager claimManager) {
        if (args.length != 1) {
            player.sendMessage(plugin.getLangManager().getMessage("claimtrust.usage"));
            return true;
        }

        Claim claim = claimManager.getClaimAt(player.getLocation());
        if (claim == null) {
            player.sendMessage(plugin.getLangManager().getMessage("claimtrust.not_in_claim"));
            return true;
        }

        if (!claim.isOwner(player)) {
            player.sendMessage(plugin.getLangManager().getMessage("claimtrust.not_owner"));
            return true;
        }

        org.bukkit.OfflinePlayer targetPlayer = org.bukkit.Bukkit.getOfflinePlayer(args[0]);
        if (!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline()) {
            player.sendMessage(plugin.getLangManager().getMessage("claimtrust.player_not_found"));
            return true;
        }

        if (claim.addTrustedPlayer(targetPlayer.getUniqueId())) {
            claimManager.saveClaims();
            player.sendMessage(plugin.getLangManager().getMessage("claimtrust.added", targetPlayer.getName()));

            if (targetPlayer.isOnline()) {
                targetPlayer.getPlayer().sendMessage(plugin.getLangManager().getMessage("claimtrust.notification_receiver", player.getName()));
            }
        } else {
            player.sendMessage(plugin.getLangManager().getMessage("claimtrust.already_trusted"));
        }

        return true;
    }

    private boolean handleClaimUntrust(Player player, String[] args, ClaimManager claimManager) {
        if (args.length != 1) {
            player.sendMessage(plugin.getLangManager().getMessage("claimuntrust.usage"));
            return true;
        }

        Claim claim = claimManager.getClaimAt(player.getLocation());
        if (claim == null) {
            player.sendMessage(plugin.getLangManager().getMessage("claimtrust.not_in_claim"));
            return true;
        }

        if (!claim.isOwner(player)) {
            player.sendMessage(plugin.getLangManager().getMessage("claimtrust.not_owner"));
            return true;
        }

        org.bukkit.OfflinePlayer targetPlayer = org.bukkit.Bukkit.getOfflinePlayer(args[0]);
        if (!targetPlayer.hasPlayedBefore() && !targetPlayer.isOnline()) {
            player.sendMessage(plugin.getLangManager().getMessage("claimtrust.player_not_found"));
            return true;
        }

        if (claim.removeTrustedPlayer(targetPlayer.getUniqueId())) {
            claimManager.saveClaims();
            player.sendMessage(plugin.getLangManager().getMessage("claimuntrust.removed", targetPlayer.getName()));

            if (targetPlayer.isOnline()) {
                targetPlayer.getPlayer().sendMessage(plugin.getLangManager().getMessage("claimuntrust.notification_receiver", player.getName()));
            }
        } else {
            player.sendMessage(plugin.getLangManager().getMessage("claimuntrust.not_trusted"));
        }

        return true;
    }

    private boolean handleClearSelection(Player player) {
        plugin.getSelectionManager().clearSelection(player);
        player.sendMessage(plugin.getLangManager().getMessage("clearselection.success"));
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
