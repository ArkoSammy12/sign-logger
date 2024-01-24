# Sign Logger
This server side mod allows for the tracking of sign editions in the world. It can log these events to the server's console, as well as store them to a database. The mod features an inspection mode to quickly see the sign=edit logs associated with a specific coordinate.

## Features

### Inspection mode

If you have OP permissions in the server, you are able to toggle inspection mode via `/sign-logger inspect true` and `/sign-logger inspect false`, respectively. Once in inspection mode, you will not be able to break or place any blocks. Left click any currently placed block to see the sign-edit logs of the coordinates of the clicked block. You can also right click a block to to see the sign-edit logs of the coordinate adjacent to the face of the block you right clicked.

Inspection mode will be automatically disabled upon dying or leaving the server.

### Sign-edit logging

There are currently four types of sign edit events that get stored, all of which include information about the author, the position, the world, and the time at which this event happened. These are:
- **Changed Text event**: Corresponds to when a player changes the text of a sign to another. Includes information about the old and new text of the sign, as well as the side of the sign where the text edition happened.
- **Waxed Sign event**: Corresponds to when a player waxes a sign, preventing it from being edited further.
- **Dyed Sign event**: Corresponds to when a player changes the color of the text of a sign by applying a dye. Includes information about the old and new color of the text, and the side of the sign where the color change happened.
- **Glowed Sign event**: Corresponds to when a player applies or removes the glow effect from the text of a sign, by using a glow ink sac or an ink sac, respectively. Includes information about the side of the sign where this action was performed.

You can also toggle the logging of sign-edits in the console via `/sign-logger settings do_console_logs true|false`.

### Database

The sign-edit logs get stored in a simple database. You can configure the time in days that will be used to purge logs older than the time specified via `/sign-logger database purge`. You can specify the amount of days with `/sign-logger database purge_older_than_x_days <whole number value>`.
The database will also be automatically purged upon server shutdown, using the `purge_logs_older_than_x_amount` value specified in the configuration file.

## Configuration  File

When the server is started, the mod will look for an existing `sign-logger.toml` file for the config folder of your game. If it exists, it will read the values from there. If not, it will create a new config file in `/config/sign-logger.toml`. You can then edit this file to configure the mod, and restart the server to apply the changes, or use the `/sign-logger reload_config` in-game command. 

The following is the default configuration file generated upon first mod initialization or whenever the mod fails to find the config file during server or world shutdown.

```toml
#Settings related to the behavior of the database.
[database]
	#(Default = 30) Configure the time in days that will be used to purge log entries older than the time specified.
	#Cannot be set to a value lower than 1 or a decimal value.
	purge_logs_older_than_x_amount = 1
#Toggleable settings to customize the behavior of the mod.
[settings]
	#(Default = true) Toggle the logging of sign edit instances in the console.
	do_console_logging = true
```

## Support

If you would like to report a bug, or make a suggestion, you can do so via the mod's [issue tracker](https://github.com/ArkoSammy12/sign-logger/issues) or join my [Discord server](https://discord.gg/UKr8n3b3ze). 

## Building

Clone this repository on your PC, then open your command line prompt on the main directory of the mod, and run the command: `gradlew build`. Once the build is successful, you can find the mod under `/sign-logger/build/libs`. Use the .jar file without the `"sources"`.
