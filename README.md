# MoralClaims Plugin

<details><summary>Описание на Русском</summary>

   🔄 Поддержка версий 1.16-1.21.6

Плагин, реализующий систему "моральных" приватов с голограммами и Telegram уведомлениями.

## Особенности

- **Моральные приваты**: Вместо обычных приватов показываются голограммы с сообщениями
- **Выделение территории**: Используйте кисточку археолога для выделения прямоугольных областей
- **Умные уведомления**: Группировка повторяющихся действий с счетчиками (x1, x2, x3...)
- **Telegram уведомления**: Получайте уведомления о действиях в ваших приватах
- **Настраиваемые сообщения**: Разные рандомные сообщения для разных действий
- **Гибкие лимиты**: Настройка максимальной площади и размеров приватов

## Как создать приват

1. Скрафтите кисточку археолога (либо золотую лопату, в зависимости от версии сервера)
2. Переименуйте её в наковальне на "Инструмент привата"
3. Левый клик по блоку - установить первую точку
4. Правый клик по блоку - установить вторую точку
5. Выполните команду `/claim` для создания привата
6. При необходимости очистите выделение: `/clearselection`

## Команды

- `/claim` - Создать приват из выделенной области
- `/unclaim` - Удалить приват в текущем месте
- `/claimlist` - Список ваших приватов
- `/claimtrust <игрок>` - Добавить игрока в приват
- `/claimuntrust <игрок>` - Удалить игрока из привата
- `/clearselection` - Очистить текущее выделение
- `/telegram` - Привязать/отвязать Telegram аккаунт

## Права доступа

- `moralclaims.claim` - Создание и удаление приватов
- `moralclaims.unclaim` - Удаление приватов
- `moralclaims.list` - Просмотр списка приватов
- `moralclaims.trust` - Управление участниками приватов
- `moralclaims.telegram` - Привязка Telegram
- `moralclaims.admin` - Административные права

## Настройка Telegram бота

1. Создайте бота через @BotFather
2. Получите токен и username бота
3. Добавьте их в `config.yml`
4. Перезапустите сервер
5. Игроки могут привязать аккаунты командой `/telegram`

## Установка

1. Скопируйте файл `build/libs/MoralClaims-*.*.jar` в папку `plugins/` вашего сервера
2. Перезапустите сервер
3. Настройте `plugins/MoralClaims/config.yml`:
   - Добавьте токен и username Telegram бота
   - Настройте лимиты приватов по желанию
   - Измените сообщения голограмм
   - Настройте задержку группировки уведомлений (`group_delay_seconds`)
4. Перезапустите сервер или выполните `/reload`

## Требования

- Версия сервера 1.16-1.21.6
- Java 21+
- Доступ к интернету для Telegram бота (опционально)

</details>

<details><summary>Description in English</summary>

   🔄 Support for versions 1.16-1.21.6

A plugin that implements a system of "moral" privates with holograms and Telegram notifications.

## Features

- **Moral privates**: Instead of regular privates, holograms with messages are shown
- **Highlighting the territory**: Use the archaeologist's brush to highlight rectangular areas
- **Smart notifications**: Grouping recurring actions with counters (x1, x2, x3...)
- **Telegram notifications**: Receive notifications about actions in your privates
- **Customizable messages**: Different random messages for different actions
- **Flexible limits**: Setting the maximum area and size of privates

## How to create a private

1. Craft a brush (or a golden shovel, depending on the server version)
2. Rename it in the anvil to "Claim Tool"
3. Left click on the block - set the first point
4. Right click on the block - set the second point
5. Run the `/claim` command to create a private
6. If necessary, clear the selection: `/clearselection`

## Commands

- `/claim` - Create a private from the selected area
- `/unclaim` - Delete a private in the current location
- `/claimlist` - List of your privates
- `/claimtrust <player>` - Add a player to a private
- `/claimuntrust <player>` - Delete a player from a private
- `/clearselection` - Clear the current selection
- `/telegram` - Link/unlink a Telegram account

## Access rights

- `moralclaims.claim` - Create and delete privates
- `moralclaims.unclaim` - Delete privates
- `moralclaims.list` - View the list of privates
- `moralclaims.trust` - Managing private participants
- `moralclaims.telegram` - Telegram binding
- `moralclaims.admin` - Administrative rights

## Telegram bot setup

1. Create a bot via @BotFather
2. Get the bot token and username
3. Add them to `config.yml`
4. Restart the server
5. Players can bind accounts with the command `/telegram`

## Installation

1. Copy the file `build/libs/MoralClaims-*.*.jar` to the `plugins/` folder of your server
2. Restart the server
3. Configure `plugins/MoralClaims/config.yml`:
- Add the token and username of the Telegram bot
- Configure private limits as desired
- Change hologram messages
- Configure notification grouping delay (`group_delay_seconds`)
4. Restart the server or run `/reload`

## Requirements

- Server version 1.16-1.21.6
- Java 21+
- Internet access for Telegram bot (optional)

</details>

[![CatShade STD](https://cdn.modrinth.com/data/cached_images/004972d5352cd7d5a54208338286866d05863f4c_0.webp)](https://catshade.ru/)
