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
            sender.sendMessage("§cЭта команда только для игроков!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!plugin.getConfigManager().isTelegramEnabled()) {
            player.sendMessage("§cTelegram уведомления отключены!");
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
                    player.sendMessage("§a✓ Telegram аккаунт успешно отвязан!");
                    player.sendMessage("§7Уведомления больше не будут приходить в Telegram");
                } else {
                    player.sendMessage("§cОшибка при отвязке аккаунта");
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
            player.sendMessage("§6=== Привязка Telegram ===");
            player.sendMessage("§71. Найди бота: §f@" + plugin.getConfigManager().getTelegramBotUsername());
            player.sendMessage("§72. Отправь боту команду: §f/start");
            player.sendMessage("§73. Отправь боту этот код: §a" + code);
            player.sendMessage("§7Код действует 5 минут");
        } else {
            player.sendMessage("§cИспользуй: /telegram");
        }
        
        return true;
    }
}