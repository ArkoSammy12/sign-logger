package xd.arkosammy.configuration;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import xd.arkosammy.SignLogger;

import java.util.Arrays;

public enum SettingsConfig {
    DO_CONSOLE_LOGGING(new ConfigEntry<>("do_console_logging", true, """
            (Default = true) Toggle the logging of sign edit instances in the console."""));

    private final ConfigEntry<Boolean> entry;

    SettingsConfig(ConfigEntry<Boolean> entry){
        this.entry = entry;
    }

    public ConfigEntry<Boolean> getEntry(){
        return this.entry;
    }

    private static final String TABLE_NAME = "settings";
    private static final String TABLE_COMMENT = """
            Toggleable settings to customize the behaviour of the mod.""";

     static void saveToFileWithDefaultValues(CommentedFileConfig fileConfig){

        for(ConfigEntry<Boolean> configEntry : Arrays.stream(SettingsConfig.values()).map(SettingsConfig::getEntry).toList()){
            configEntry.resetValue();
        }

        saveSettingsToFile(fileConfig);

    }

     static void saveSettingsToFile(CommentedFileConfig fileConfig){
        for(ConfigEntry<Boolean> entry : Arrays.stream(SettingsConfig.values()).map(SettingsConfig::getEntry).toList()){
            fileConfig.set(TABLE_NAME + "." + entry.getName(), entry.getValue());
            String entryComment = entry.getComment();
            if(entryComment != null) fileConfig.setComment(TABLE_NAME + "." + entry.getName(), entryComment);
        }
        fileConfig.setComment(TABLE_NAME, TABLE_COMMENT);
    }

     static void loadSettingsToMemory(CommentedFileConfig fileConfig){
        for(ConfigEntry<Boolean> configEntry : Arrays.stream(SettingsConfig.values()).map(SettingsConfig::getEntry).toList()){
            Object value = fileConfig.getOrElse(TABLE_NAME + "." + configEntry.getName(), configEntry.getDefaultValue());
            if(value instanceof Boolean boolValue){
                configEntry.setValue(boolValue);
            } else {
                SignLogger.LOGGER.error("Invalid value in config file for setting: " + configEntry.getName());
            }
        }
    }

}
