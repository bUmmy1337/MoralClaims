package ru.moralclaims.managers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.moralclaims.MoralClaimsPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TelegramManager {
    private final MoralClaimsPlugin plugin;
    private final Map<UUID, Long> playerChatIds = new HashMap<>();
    private final Map<String, UUID> pendingCodes = new HashMap<>();
    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File dataFile;
    private TelegramBotsApi botsApi;
    private MoralClaimsBot bot;
    
    public TelegramManager(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "telegram_links.json");
        loadLinks();
    }
    
    public void startBot() {
        if (!plugin.getConfigManager().isTelegramEnabled()) {
            return;
        }
        
        String token = plugin.getConfigManager().getTelegramBotToken();
        String username = plugin.getConfigManager().getTelegramBotUsername();
        
        if (token.isEmpty() || username.isEmpty()) {
            plugin.getLogger().warning("Telegram bot token or username not configured!");
            return;
        }
        
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            bot = new MoralClaimsBot(token, username);
            botsApi.registerBot(bot);
            plugin.getLogger().info("Telegram bot started successfully!");
        } catch (TelegramApiException e) {
            plugin.getLogger().severe("Failed to start Telegram bot: " + e.getMessage());
        }
    }
    
    public void stopBot() {
        if (bot != null) {
            bot.onClosing();
        }
    }
    
    public String generateLinkCode(Player player) {
        String code = String.format("%04d", random.nextInt(10000));
        pendingCodes.put(code, player.getUniqueId());
        
        // Remove code after 5 minutes
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            pendingCodes.remove(code);
        }, 6000L);
        
        return code;
    }
    
    public boolean linkPlayer(String code, long chatId) {
        UUID playerId = pendingCodes.remove(code);
        if (playerId != null) {
            playerChatIds.put(playerId, chatId);
            saveLinks();
            return true;
        }
        return false;
    }
    
    public boolean isPlayerLinked(UUID playerId) {
        return playerChatIds.containsKey(playerId);
    }
    
    public boolean unlinkPlayer(UUID playerId) {
        Long chatId = playerChatIds.remove(playerId);
        if (chatId != null) {
            saveLinks();
            return true;
        }
        return false;
    }
    
    public void sendNotification(UUID playerId, String message) {
        if (!plugin.getConfigManager().areTelegramNotificationsEnabled()) {
            return;
        }
        
        Long chatId = playerChatIds.get(playerId);
        if (chatId != null && bot != null) {
            bot.sendNotification(chatId, message);
        }
    }
    
    private void loadLinks() {
        if (!dataFile.exists()) {
            return;
        }
        
        try {
            Map<String, Long> data = objectMapper.readValue(dataFile, new TypeReference<Map<String, Long>>() {});
            for (Map.Entry<String, Long> entry : data.entrySet()) {
                playerChatIds.put(UUID.fromString(entry.getKey()), entry.getValue());
            }
            plugin.getLogger().info("Loaded " + playerChatIds.size() + " Telegram links");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load Telegram links: " + e.getMessage());
        }
    }
    
    private void saveLinks() {
        try {
            plugin.getDataFolder().mkdirs();
            Map<String, Long> data = new HashMap<>();
            for (Map.Entry<UUID, Long> entry : playerChatIds.entrySet()) {
                data.put(entry.getKey().toString(), entry.getValue());
            }
            objectMapper.writeValue(dataFile, data);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save Telegram links: " + e.getMessage());
        }
    }
    
    private class MoralClaimsBot extends TelegramLongPollingBot {
        private final String botToken;
        private final String botUsername;
        
        public MoralClaimsBot(String botToken, String botUsername) {
            this.botToken = botToken;
            this.botUsername = botUsername;
        }
        
        @Override
        public String getBotToken() {
            return botToken;
        }
        
        @Override
        public String getBotUsername() {
            return botUsername;
        }
        
        @Override
        public void onUpdateReceived(Update update) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();
                
                if (messageText.startsWith("/start")) {
                    sendMessage(chatId, "Привет! Отправь код привязки из игры для подключения уведомлений.");
                } else if (messageText.matches("\\d{4}")) {
                    if (linkPlayer(messageText, chatId)) {
                        sendMessage(chatId, "✅ Аккаунт успешно привязан! Теперь ты будешь получать уведомления о действиях в твоих приватах.");
                    } else {
                        sendMessage(chatId, "❌ Неверный код или код истек. Получи новый код в игре командой /telegram");
                    }
                } else {
                    sendMessage(chatId, "Отправь 4-значный код привязки из игры.");
                }
            }
        }
        
        private void sendMessage(long chatId, String text) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(text);
            
            try {
                execute(message);
            } catch (TelegramApiException e) {
                plugin.getLogger().warning("Failed to send Telegram message: " + e.getMessage());
            }
        }
        
        public void sendNotification(long chatId, String text) {
            sendMessage(chatId, text);
        }
    }
}