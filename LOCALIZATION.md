# MoralClaims - Система локализации

## Обзор
Плагин MoralClaims поддерживает полную локализацию с возможностью изменения любой строки через языковые файлы.

## Настройка языка

### В config.yml
```yaml
# Locale setting (e.g., en_us, ru_ru)
# You can edit the language files in the lang/ folder
locale: en_us
```

Поддерживаемые языки:
- `en_us` - English (US) - по умолчанию
- `ru_ru` - Русский

## Языковые файлы

### Расположение
Языковые файлы находятся в папке `plugins/MoralClaims/lang/`:
- `en_us.json` - английская локализация
- `ru_ru.json` - русская локализация

### Автоматическое создание
При первом запуске плагин автоматически создает оба языковых файла из встроенных шаблонов.

### Структура файла
```json
{
  "error.no_player": "§cThis command is for players only!",
  "claim.tool_instructions_header": "§6=== How to create a selection tool ===",
  "claim.created": "§aClaim created! Area: %d blocks",
  ...
}
```

## Ключи локализации

### Общие ошибки
- `error.no_player` - Команда только для игроков

### Команды привата
- `claim.tool_instructions_header` - Заголовок инструкции
- `claim.tool_instructions_1` - Шаг 1 (принимает параметр: название инструмента)
- `claim.tool_instructions_2` - Шаг 2
- `claim.tool_instructions_3` - Шаг 3
- `claim.tool_instructions_success` - Успешное завершение
- `claim.tool_instructions_usage` - Инструкция по использованию
- `claim.no_selection` - Нет выделения
- `claim.too_large` - Слишком большая область (параметр: максимум)
- `claim.too_small` - Слишком маленькая область (параметр: минимум)
- `claim.created` - Приват создан (параметр: площадь)
- `claim.protection_info` - Информация о защите
- `claim.max_claims` - Достигнут лимит приватов (пар��метр: максимум)
- `claim.already_claimed` - Территория уже занята

### Удаление привата
- `unclaim.success` - Приват удален
- `unclaim.not_your_claim` - Не ваш приват

### Список приватов
- `claimlist.no_claims` - Нет приватов
- `claimlist.header` - Заголовок списка
- `claimlist.item` - Элемент списка (параметры: номер, координаты, размеры, площадь)

### Доверенные игроки
- `claimtrust.usage` - Использование команды
- `claimtrust.not_in_claim` - Не в привате
- `claimtrust.not_owner` - Не владелец
- `claimtrust.player_not_found` - Игрок не найден
- `claimtrust.added` - Игрок добавлен (параметр: имя игрока)
- `claimtrust.already_trusted` - Игрок уже доверен
- `claimtrust.notification_receiver` - Уведомление получателю (параметр: имя владельца)
- `claimuntrust.usage` - Использование команды
- `claimuntrust.removed` - Игрок удален (параметр: имя игрока)
- `claimuntrust.not_trusted` - Игрок не был доверен
- `claimuntrust.notification_receiver` - Уведомление об удалении (параметр: имя владельца)

### Очистка выделения
- `clearselection.success` - Выделение очищено

### Наковальня
- `anvil.tool_name` - Название инструмента
- `anvil.lore_line_1` - Строка описания 1
- `anvil.lore_line_2` - Строка описания 2
- `anvil.lore_line_3` - Строка описания 3
- `anvil.lore_line_4` - Пустая строка
- `anvil.lore_line_5` - Строка описания 5

### Названия инструментов
- `tool_name.brush` - Кисточка археолога
- `tool_name.golden_shovel` - Золотая лопата

### Выделение территории
- `selection.first_point` - Первая точка установлена (параметр: координаты)
- `selection.second_point` - Вторая точка установлена (параметр: координаты)
- `selection.area_selected` - Область выделена (параметры: ширина, длина, площадь)
- `selection.cleared` - Выделение очищено

### Telegram команды
- `telegram.disabled` - Telegram отключен
- `telegram.unlink_success` - Успешная отвязка
- `telegram.unlink_info` - Информация об отвязке
- `telegram.unlink_error` - Ошибка отвязки
- `telegram.link_header` - Заголовок привязки
- `telegram.link_step1` - Шаг 1 (параметр: имя бота)
- `telegram.link_step2` - Шаг 2
- `telegram.link_step3` - Шаг 3 (параметр: код)
- `telegram.link_code_expires` - Срок действия кода
- `telegram.usage` - Использование команды
- `telegram.confirm_unlink` - Подтверждение отвязки
- `telegram.confirm_repeat` - Повторить команду
- `telegram.confirm_warning` - Предупреждение
- `telegram.confirm_expired` - Время истекло
- `telegram.confirm_timeout` - Таймаут подтверждения

## Параметры в сообщениях

Некоторые сообщения поддерживают параметры через `%s` (строка) или `%d` (число):

```java
// Пример использования в коде
plugin.getLangManager().getMessage("claim.created", area);
plugin.getLangManager().getMessage("claimtrust.added", playerName);
```

## Изменение локализации

1. Откройте нужный языковой файл в папке `lang/`
2. Измените значения ключей (правая час��ь после двоеточия)
3. Сохраните файл
4. Перезапустите сервер или перезагрузите плагин

## Добавление нового языка

1. Скопируйте `en_us.json` в новый файл (например, `de_de.json`)
2. Переведите все значения на нужный язык
3. Измените `locale` в `config.yml` на новое значение
4. Перезапустите сервер

## ASCII Art при запуске

При запуске плагин выводит ASCII-арт:
```
  __  __  ___  ___    _   _     
 |  \/  |/ _ \| _ \  /_\ | |    
 | |\/| | (_) |   / / _ \| |__  
 |_|  |_|\___/|_|_\/_/ \_\____| 
                               
  ___ _      _   ___ __  __ ___ 
 / __| |    /_\ |_ _|  \/  / __|
| (__| |__ / _ \ | || |\/| \__ \
 \___|____/_/ \_\___|_|  |_|___/
```

Это показывает "MORAL CLAIMS" в стилизованном виде.