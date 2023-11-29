# Sign Logger
This simple server side mod allows for the tracking of sign editions in the world. It can log these events to the server's console, as well as store them to a database. The mod features an inspection mode to quickly see the sign=edit logs associated with a specific coordinate.

## Features

### Inspection mode

If you have OP permissions in the server, you are able to toggle inspection mode via `/sign-logger inspect true` and `/sign-logger inspect false`, respectively. Once in inspection mode, you will not be able to break or place any blocks. Left click any currently placed block to see the sign-edit logs of the coordinates of the clicked block. You can also "place" a block in an empty space to see the sign-edit logs at that location.

Inspection mode will be automatically disabled upon dying or leaving the server.

### Sign-edit logging

Whenever a player successfully edits the text of any sign, this event will be logged to the console and stored for future querying. This information includes:
- The player who edited the sign.
- The date of the editing.
- Wwhether the text edited was on the front or back side of the sign.
- The coordinates and the world where the sign was edited.
- The text before and after the edition was made.

You can also toggle the logging of sign-edits in the console via `/sign-logger settings do_console_logs true|false`.

### Database

The sign-edit logs get stored in a simple database. You can configure the time in days that will be used to purge logs older than the time specified via `/sign-logger database purge`. You can specify the amount of days with `/sign-logger database purge_older_than_x_days <whole number value>`.

## Configuration  File

When the server is started, the mod will look for an existing `sign-logger.toml` file for the config folder of your game. If it exists, it will read the values from there. If not, it will create a new config file in `/config/sign-logger.toml`. You can then edit this file to configure the mod, and restart the server to apply the changes, or use the `/sign-logger reload_config` in-game command. 

The following is the default configuration file generated upon first mod initialization or whenever the mod fails to find the config file during server or world shutdown.

```toml
#Settings related to the behaviour of the database.
[database]
	#(Default = 30) Configure the time in days that will be used to purge log entries older than the time specified.
	#Cannot be set to a value lower than 1 or a decimal value.
	purge_logs_older_than_x_amount = 1
#Toggleable settings to customize the behaviour of the mod.
[settings]
	#(Default = true) Toggle the logging of sign edit instances in the console.
	do_console_logging = true
```

## Support

If you would like to report a bug, or make a suggestion, you can do so via the mod's [issue tracker]([https://github.com/ArkoSammy12/creeper-healing/issues](https://github.com/ArkoSammy12/sign-logger/issues)) or join my [Discord server](https://discord.gg/UKr8n3b3ze). 

## Building

Clone this repository on your PC, then open your command line prompt on the main directory of the mod, and run the command: `gradlew build`. Once the build is successful, you can find the mod under `/sign-logger/build/libs`. Use the .jar file without the `"sources"`.
