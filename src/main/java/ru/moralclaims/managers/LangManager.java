package ru.moralclaims.managers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.moralclaims.MoralClaimsPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class LangManager {
    private final MoralClaimsPlugin plugin;
    private final Map<String, String> messages = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LangManager(MoralClaimsPlugin plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    private void loadMessages() {
        // Create lang directory if it doesn't exist
        File langDir = new File(plugin.getDataFolder(), "lang");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        // Create both language files if they don't exist
        createLanguageFileIfNotExists("en_us.json");
        createLanguageFileIfNotExists("ru_ru.json");

        // Load the selected locale
        String locale = plugin.getConfigManager().getLocale();
        File langFile = new File(langDir, locale + ".json");

        if (!langFile.exists()) {
            plugin.getLogger().warning("Language file not found: " + locale + ".json. Using en_us.json as fallback");
            langFile = new File(langDir, "en_us.json");
        }

        try {
            messages.putAll(objectMapper.readValue(langFile, new TypeReference<Map<String, String>>() {}));
            plugin.getLogger().info("Loaded " + messages.size() + " messages from " + langFile.getName());
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load messages from " + langFile.getName() + ": " + e.getMessage());
        }
    }

    private void createLanguageFileIfNotExists(String fileName) {
        File langFile = new File(plugin.getDataFolder(), "lang/" + fileName);
        if (!langFile.exists()) {
            try (InputStream in = plugin.getResource("lang/" + fileName)) {
                if (in != null) {
                    Files.copy(in, langFile.toPath());
                    plugin.getLogger().info("Created language file: " + fileName);
                } else {
                    plugin.getLogger().warning("Default language file (" + fileName + ") not found in JAR!");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create language file " + fileName + ": " + e.getMessage());
            }
        }
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, "§cMissing message: " + key);
    }

    public String getMessage(String key, Object... args) {
        String message = getMessage(key);
        return String.format(message, args);
    }
}
