# Swofty Dungeons
![badge](https://img.shields.io/github/v/release/Swofty-Developments/SwoftyDungeons)
![badge](https://img.shields.io/github/last-commit/Swofty-Developments/SwoftyDungeons)
[![badge](https://img.shields.io/discord/830345347867476000?label=discord)](https://discord.gg/atlasmc)
[![badge](https://img.shields.io/github/license/Swofty-Developments/SwoftyDungeons)](https://github.com/Swofty-Developments/SwoftyParkour/blob/master/LICENSE.txt)

**[JavaDoc 1.0.0](https://swofty-developments.github.io/SwoftyDungeons/)**

A custom dungeon creator for 1.18.x! Supports custom entity spawning and advanced configuration. All message are configurable. Current supported commands are;
- **/dungeon create <name>**

  *Requires permission "dungeon.admin.create"*

  Used to create a clean dungeon, note that dungeons are not playable until they have been fully set up so don't worry about players joining just yet.

- **/dungeon edit <dungeon>**

  *Requires permission "dungeon.admin.edit"*

  Opens a GUI that allows you to set the spawners, start location, and the controls to add the dungeon into production..

- **/dungeon start <dungeon>**

  *Requires permission "dungeon.play"*

  Puts you into an actual game of the dungeon, note that you will not be able to do this until you have clicked "Finalize" inside of the edit menu from the command previous.

- **/dungeon leave <dungeon>**

  *Requires permission "dungeon.play"*

  Exits you outside of the dungeon, note that you will not be able to do this while being inside of combat.

- **/dungeon info <dungeon>**

  *Requires permission "dungeon.info"*

  Shows you the longest session times and most kills per session leaderboards for the given dungeon.

- **/dungeon stats <player>**

  *Requires permission "dungeon.stats"*

  Shows you the specific players previous sessions as well as their best times for every dungeon.


## Table of contents

* [Getting started](#getting-started)
* [Setting up the configuration files](#setting-up-the-configuration-file)
* [Listening to plugin events](#listening-to-plugin-events)
* [License](#license)

## Getting started

This API does not support stand-alone usage, and you will need to add the project jar into your **plugins** folder. An updated version of the API jar can be found inside of the releases tab on the right of this readme. This projects JavaDoc (documentation for every method) can be found [here](https://swofty-developments.github.io/SwoftyParkour/).

### Add SwoftyDungeons to your project

![badge](https://img.shields.io/github/v/release/Swofty-Developments/SwoftyDungeons)

First, you need to setup the dependency inside of your Java project. Replace **VERSION** with the version of the release.

> Maven
```xml
<dependency>
    <groupId>net.swofty</groupId>
    <artifactId>dungeon</artifactId>
    <version>VERSION</version>
</dependency>
```

> Gradle
```gradle
dependencies {
    implementation 'net.swofty:dungeon:VERSION'
}
```

## Setting up the configuration file

> CONFIG.yml
```yaml
#
#      Swofty Dungeons
#       Swofty#0001
#
#      Plugin Config
#

hologram-split-size: 0.3
# Must not be less than 1 and wouldn't recommend being over 10
leaderboard-display-size: 10
scoreboard-enabled: TRUE
```

> MESSAGES.yml
```yaml
#
#      Swofty Dungeons
#       Swofty#0001
#
#     Messages Config
#

#
#  To use hex colors inside of messages, merely add the variable name and its
#  hex color value here. After you have done this you can use the variable
#  inside any of the messages
#
#  So to use this example hex color, you should use the $HEXCOLOREXAMPLE variable
#
hex-colors:
  HEXCOLOREXAMPLE: "#3CEC5D"

messages:
  # Supports the variables;
  # $NAME - Dungeon Name
  # $TIME - Elapsed Time
  # $DAMAGE_DEALT - Damage dealt
  # $DAMAGE_RECIEVED - Damage recieved
  # $ENTITIES_KILLED - The amount of mobs that have been killed
  # $BEST_TIME - Players best time
  scoreboard:
    - "§e§l Dungeons"
    - "§f "
    - "§fDungeon: §a$NAME"
    - "§fTime: §b$TIME"
    - "§fDamage Dealt: §c$DAMAGE_DEALT"
    - "§fEntities Killed: §e$ENTITIES_KILLED"
    - "§fBest Time: §e$BEST_TIME"
    - "§e "
    - "§ewww.example.com"
  command:
    no-permission: "%%red%%You do not have permission for this command!"
    usage-command: "%%red%%Usage: $USAGE"
    cooldown: "%%red%%You are currently under cooldown for another $SECONDS seconds"
    usage-overall:
      - "&cSwoftyDungeons &7- &fMade by Swofty#0001"
      - "§8- §e/dungeon create <name>"
      - "§8- §e/dungeon edit <dungeon>"
      - "§8- §e/dungeon start <dungeon>"
      - "§8- §e/dungeon leave <dungeon>"
      - "§8- §e/dungeon info <dungeon>"
      - "§8- §e/dungeon stats [player]"
    name-already-taken: "§cThere is already a dungeon with the name §e$NAME"
    creation-message:
      - "§aCreated a dungeon with the name §e$NAME"
    dungeon-not-found: "§cCould not find a dungeon named §e$NAME"
    player-not-found:  "§cCould not find a player named §e$NAME"
    invalid-number-input: "§e$INPUT §cis not a valid number!"
    not-in-a-dungeon: "§cYou are not currently in a dungeon"
    started-dungeon-message:
      - "§aWelcome to the §e$NAME §adungeon"
    dungeon-not-finished: "§aThe §e$NAME §adungeon has not been finished"
    started-dungeon-title: "§e$NAME"
    started-dungeon-subtitle: "§adungeon"
  dungeons:
    holograms:
      spawner:
        - "§f$NAME"
        - "§eSpawner §f#$SPAWNER"
      leaderboard-top:
        - "§b$NAME§a's Leaderboard"
        - "§7Dungeons"
      leaderboard-entry: "§7$NUMBER. §e$USERNAME §8- §e$TIME"
      leaderboard-bottom:
        - "§bYour Longest Time: §e$PLAYERTIME"
```

## Listening to Plugin Events

Not currently supported, although you are able to override the listener classes and implement your own methods.

## License
SwoftyDungeons is licensed under the permissive MIT license. Please see [`LICENSE.txt`](https://github.com/Swofty-Developments/SwoftyDungeons/blob/master/LICENSE.txt) for more information.
