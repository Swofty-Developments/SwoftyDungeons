# Swofty Dungeons
![badge](https://img.shields.io/github/v/release/Swofty-Developments/SwoftyDungeons)
![badge](https://img.shields.io/github/last-commit/Swofty-Developments/SwoftyDungeons)
[![badge](https://img.shields.io/discord/830345347867476000?label=discord)](https://discord.gg/atlasmc)
[![badge](https://img.shields.io/github/license/Swofty-Developments/SwoftyDungeons)](https://github.com/Swofty-Developments/SwoftyParkour/blob/master/LICENSE.txt)

**[JavaDoc 1.0.0](https://swofty-developments.github.io/SwoftyDungeons/)**

A custom dungeon creator for 1.18.x! Supports custom entity spawning and advanced configuration. All message are configurable. Current supported commands are;
- /dungeon create <name>
- /dungeon spawners <dungeon> [add|list|remove] [spawnertype]
- /dungeon spawnlocation <dungeon>
- /dungeon markfinished <dungeon>
- /dungeon start <dungeon>
- /dungeon leave <dungeon>
- /dungeon info <dungeon>
- /dungeon stats <dungeon>
- /dungeon setleaderboard <dungeon>
- /dungeon delleaderboard <dungeon>


## Table of contents

* [Images](#images)
* [Getting started](#getting-started)
* [Setting up the configuration files](#setting-up-the-configuration-file)
* [Listening to plugin events](#listening-to-plugin-events)
* [License](#license)

## Images

![Image 1](https://cdn.discordapp.com/attachments/923387135111872552/1020654212880810025/unknown.png)
![Image 2](https://cdn.discordapp.com/attachments/923387135111872552/1020654274268647466/unknown.png)
![Image 3](https://cdn.discordapp.com/attachments/923387135111872552/1020654348549759066/unknown.png)

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
hologram-split-size: 0.3
# Must not be less than 1 and wouldn't recommend being over 10
leaderboard-display-size: 10
```

> MESSAGES.yml
```yaml
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
  command:
    no-permission: "%%red%%You do not have permission for this command!"
    usage-command: "%%red%%Usage: $USAGE"
    cooldown: "%%red%%You are currently under cooldown for another $SECONDS seconds"
    usage-overall:
      - "&cSwoftyDungeons &7- &fMade by Swofty#0001"
      - "§8- §e/dungeon create <name>"
      - "§8- §e/dungeon spawners <dungeon> [add|list|remove] [spawnertype]"
      - "§8- §e/dungeon spawnlocation <dungeon>"
      - "§8- §e/dungeon markfinished <dungeon>"
      - "§8- §e/dungeon start <dungeon>"
      - "§8- §e/dungeon leave <dungeon>"
      - "§8- §e/dungeon info <dungeon>"
      - "§8- §e/dungeon stats [dungeon]"
      - "§8- §e/dungeon setleaderboard <dungeon>"
      - "§8- §e/dungeon delleaderboard <dungeon>"
```

## Listening to Plugin Events

// ToDo

## License
SwoftyDungeons is licensed under the permissive MIT license. Please see [`LICENSE.txt`](https://github.com/Swofty-Developments/SwoftyDungeons/blob/master/LICENSE.txt) for more information.
