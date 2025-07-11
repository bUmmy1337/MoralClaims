# Changelog - MoralClaims v1.0.0

## ✨ Новые возможности

### 🔄 Поддержка версий 1.16-1.21.5
- **Универсальная совместимость**: Один JAR файл работает на всех версиях от 1.16 до 1.21.5
- **Автоматическое определение версии**: Плагин сам определяет версию сервера при запуске
- **Адаптивные материалы**: Автоматический выбор инструментов в зависимости от версии
- **Умные частицы**: Fallback система для частиц с поддержкой старых и новых названий
- **Совместимые звуки**: Автоматический выбор доступных звуков для каждой версии

### 🛠️ Система адаптеров
- **VersionManager**: Определение и управление версией Minecraft
- **MaterialAdapter**: Адаптация материалов и блоков под версию
- **ParticleAdapter**: Умная работа с частицами для всех версий  
- **SoundAdapter**: Совместимость звуков между версиями

### 📦 Новые команды
- **`/claimtool`**: Получить инструмент выделения (адаптируется под версию)
  - 1.16-1.19: Золотая лопата
  - 1.20+: Кисточка археолога

## 🔧 Технические улучшения

### 📋 Совместимость API
- Базовый API: Spigot 1.16.5 для максимальной совместимости
- Java 8 совместимость для старых серверов
- Безопасные вызовы с fallback механизмами

### 🎯 Адаптивные материалы
- **Инструмент выделения**:
  - 1.16-1.19: `GOLDEN_SHOVEL`
  - 1.20+: `BRUSH` (с fallback на золотую лопату)
- **Интерактивные блоки**: Универсальная проверка для всех версий

### ✨ Умные частицы
- **Выделение территории**:
  - 1.16: `VILLAGER_HAPPY`
  - 1.17+: `HAPPY_VILLAGER`
  - Fallback: `HEART` → `FLAME` → `SMOKE_NORMAL`
- **Границы приват��в**:
  - Современные версии: `DUST` с `DustOptions`
  - Старые версии: `REDSTONE`
  - Fallback: `FLAME` → `SMOKE_NORMAL` → `CRIT`

### 🔊 Адаптивные звуки
- **Успех**: `ENTITY_EXPERIENCE_ORB_PICKUP` → `ORB_PICKUP`
- **Ошибка**: `ENTITY_VILLAGER_NO` → `VILLAGER_NO`
- **Клик**: `UI_BUTTON_CLICK` → `CLICK`
- **Уведомление**: `BLOCK_NOTE_BLOCK_PLING` → `NOTE_PLING`

## 🏗️ Архитектурные изменения

### 📁 Новая структура пакетов
```
ru.moralclaims.version/
├── VersionManager.java      # Определение версии
├── MaterialAdapter.java     # Адаптация материалов
├── ParticleAdapter.java     # Адаптация частиц
└── SoundAdapter.java        # Адаптация звуков
```

### 🔄 Инициализация версий
- Система версий инициализируется первой при запуске плагина
- Все менеджеры получают доступ к адаптерам через главный класс
- Логирование определенной версии для диагностики

## 📝 Обновления конфигурации

### 🏷️ Plugin.yml
- `api-version: '1.16'` для совместимости
- Обновленное описание с указанием поддерживаемых версий
- Новая команда `/claimtool` с правами `moralclaims.tool`

### 🔨 Build.gradle
- Переход на Spigot API 1.16.5 для лучшей совместимости
- Java 8 target для поддержки старых серверов
- Оптимизированные зависимости

## 🐛 Исправления

### 🔧 Совместимость кода
- Исправлены enum switch statements для Java 8
- Убраны версионно-зависимые прямые обращения к API
- Добавлены безопасные fallback механизмы

### 📦 Сборка проекта
- Исправлены ошибки компиляции для старых версий API
- Оптимизирована структура зависимостей
- Улучшена обработка deprecated методов

## 🎯 Результат

✅ **Один JAR файл** работает на всех версиях 1.16-1.21.5  
✅ **Автоматическая адаптация** под версию сервера  
✅ **Безопасные fallback** механизмы  
✅ **Полная совместимость** функционала  
✅ **Логичная архитектура** с четким разделением ответственности  

Плагин теперь универсален и готов к использованию на любом сервере в диапазоне версий 1.16-1.21.5!