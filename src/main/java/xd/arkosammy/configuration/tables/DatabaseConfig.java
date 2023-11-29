package xd.arkosammy.configuration.tables;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import xd.arkosammy.SignLogger;
import xd.arkosammy.configuration.ConfigEntry;

import java.util.Arrays;

public enum DatabaseConfig {

    PURGE_LOGS_OLDER_THAN_X_AMOUNT(new ConfigEntry<>("purge_logs_older_than_x_amount", 30, """
            (Default = 30) Configure the time in days that will be used to purge log entries older than the time specified.
            Cannot be set to a value lower than 1 or a decimal value."""));

    private final ConfigEntry<Integer> entry;

    DatabaseConfig(ConfigEntry<Integer> entry){
        this.entry = entry;
    }

    public ConfigEntry<Integer> getEntry(){
        return this.entry;
    }

    private static final String TABLE_NAME = "database";
    private static final String TABLE_COMMENT = """
            Settings related to the behaviour of the database.""";

    public static void saveToFileWithDefaultValues(CommentedFileConfig fileConfig){
        for(ConfigEntry<Integer> configEntry : Arrays.stream(DatabaseConfig.values()).map(DatabaseConfig::getEntry).toList()){
            configEntry.resetValue();
        }
        saveSettingsToFile(fileConfig);
    }

    public static void saveSettingsToFile(CommentedFileConfig fileConfig){
        for(ConfigEntry<Integer> entry : Arrays.stream(DatabaseConfig.values()).map(DatabaseConfig::getEntry).toList()){
            fileConfig.set(TABLE_NAME + "." + entry.getName(), entry.getValue());
            String entryComment = entry.getComment();
            if(entryComment != null) fileConfig.setComment(TABLE_NAME + "." + entry.getName(), entryComment);
        }
        fileConfig.setComment(TABLE_NAME, TABLE_COMMENT);
    }

    public static void loadSettingsToMemory(CommentedFileConfig fileConfig){
        for(ConfigEntry<Integer> configEntry : Arrays.stream(DatabaseConfig.values()).map(DatabaseConfig::getEntry).toList()){
            Object value = fileConfig.getOrElse(TABLE_NAME + "." + configEntry.getName(), configEntry.getDefaultValue());
            if(value instanceof Number numberValue && numberValue.intValue() > 0){
                configEntry.setValue(numberValue.intValue());
            } else {
                SignLogger.LOGGER.error("Invalid value in config file for setting: " + configEntry.getName());
            }
        }
    }

}
