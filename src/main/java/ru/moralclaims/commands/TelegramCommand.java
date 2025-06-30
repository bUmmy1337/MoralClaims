package ru.moralclaims.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.moralclaims.MoralClaimsPlugin;
import ru.moralclaims.managers.ConfirmationManager;

public class TelegramCommand implements CommandExecutor {
    private final MoralClaimsPlugin plugin;
    
    public TelegramCommand(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
    sender.sendMessage(plugin.getLangManager().getMessage("error.no_player"));
    return true;
    }
    
    Player player = (Player) sender;
    
    if (!plugin.getConfigManager().isTelegramEnabled()) {
    player.sendMessage(plugin.getLangManager().getMessage("telegram.disabled"));
    return true;
    }
        
        // Проверяем, привязан ли уже аккаунт
        if (plugin.getTelegramManager().isPlayerLinked(player.getUniqueId())) {
            return handleLinkedAccount(player);
        } else {
            return handleUnlinkedAccount(player, args);
        }
    }
    
    private boolean handleLinkedAccount(Player player) {
        // Проверяем, есть ли ожидающее подтверждение отвязки
        if (plugin.getConfirmationManager().hasPendingConfirmation(player, ConfirmationManager.ConfirmationType.TELEGRAM_UNLINK)) {
            // Подтверждаем отвязку
            if (plugin.getConfirmationManager().confirmTelegramUnlink(player)) {
                if (plugin.getTelegramManager().unlinkPlayer(player.getUniqueId())) {
                    player.sendMessage(plugin.getLangManager().getMessage("telegram.unlink_success"));
                    player.sendMessage(plugin.getLangManager().getMessage("telegram.unlink_info"));
                } else {
                    player.sendMessage(plugin.getLangManager().getMessage("telegram.unlink_error"));
                }
            }
        } else {
            // Запрашиваем подтверждение отвязки
            plugin.getConfirmationManager().requestTelegramUnlink(player);
        }
        
        return true;
    }
    
    private boolean handleUnlinkedAccount(Player player, String[] args) {
        if (args.length == 0) {
            // Generate new code
            String code = plugin.getTelegramManager().generateLinkCode(player);
            player.sendMessage(plugin.getLangManager().getMessage("telegram.link_header"));
            player.sendMessage(plugin.getLangManager().getMessage("telegram.link_step1", plugin.getConfigManager().getTelegramBotUsername()));
            player.sendMessage(plugin.getLangManager().getMessage("telegram.link_step2"));
            player.sendMessage(plugin.getLangManager().getMessage("telegram.link_step3", code));
            player.sendMessage(plugin.getLangManager().getMessage("telegram.link_code_expires"));
        } else {
            player.sendMessage(plugin.getLangManager().getMessage("telegram.usage"));
        }
        
        return true;
    }
}