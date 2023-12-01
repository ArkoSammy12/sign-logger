package xd.arkosammy.configuration;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.GenericBuilder;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.Nullable;
import xd.arkosammy.SignLogger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SignLoggerConfig {

    private SignLoggerConfig(){}
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("sign-logger.toml");
    @Nullable
    private static final GenericBuilder<CommentedConfig, CommentedFileConfig> CONFIG_BUILDER;

    static {
        System.setProperty("nightconfig.preserveInsertionOrder", "true");
        GenericBuilder<CommentedConfig, CommentedFileConfig> builder;

        try{
            builder = CommentedFileConfig.builder(CONFIG_PATH, TomlFormat.instance())
                    .preserveInsertionOrder()
                    .concurrent()
                    .sync();
        } catch (Throwable throwable){
            SignLogger.LOGGER.info("Unable to initialize config: {}", throwable.getMessage());
            SignLogger.LOGGER.info("The config will be unable to be used.");
            builder = null;
        }
        CONFIG_BUILDER = builder;
    }

    public static void initializeConfig(){
        if(CONFIG_BUILDER != null) {
            try(CommentedFileConfig fileConfig = CONFIG_BUILDER.build()){
                if (!Files.exists(CONFIG_PATH)) {
                    SignLogger.LOGGER.warn("Found no preexisting config to load settings from. Creating a new config with default values in " + CONFIG_PATH);
                    SignLogger.LOGGER.warn("Change the settings in the config file, then reload the config by using /sign-logger reload_config, or restart the server.");
                    saveDefaultConfigSettingsToFile(fileConfig);
                    fileConfig.save();
                } else {
                    fileConfig.load();
                    loadConfigSettingsToMemory(fileConfig);
                    updateConfigFile();
                    SignLogger.LOGGER.info("Applied custom config settings");
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void updateConfigFile() throws FileNotFoundException {
        if(CONFIG_BUILDER != null){
            try(CommentedFileConfig fileConfig = CONFIG_BUILDER.build()){
                if(Files.exists(CONFIG_PATH)){
                    fileConfig.load();
                    saveConfigSettingsToFile(fileConfig);
                    try(PrintWriter printWriter = new PrintWriter(String.valueOf(CONFIG_PATH))){
                        printWriter.write("");
                    }
                    fileConfig.save();
                } else {
                    SignLogger.LOGGER.warn("Found no preexisting config to load settings from. Creating a new config with default values in " + CONFIG_PATH);
                    SignLogger.LOGGER.warn("Change the settings in the config file, then reload the config by using /sign-logger reload_config, or restart the server.");
                    saveDefaultConfigSettingsToFile(fileConfig);
                    fileConfig.save();
                }
            }
        }
    }

    public static boolean reloadConfigSettingsInMemory(CommandContext<ServerCommandSource> ctx){
        if(CONFIG_BUILDER != null){
            try(CommentedFileConfig fileConfig = CONFIG_BUILDER.build()){
                if(Files.exists(CONFIG_PATH)){
                    fileConfig.load();
                    loadConfigSettingsToMemory(fileConfig);
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private static void saveDefaultConfigSettingsToFile(CommentedFileConfig fileConfig){
        SettingsConfig.saveToFileWithDefaultValues(fileConfig);
        DatabaseConfig.saveToFileWithDefaultValues(fileConfig);
    }

    private static void saveConfigSettingsToFile(CommentedFileConfig fileConfig){
        SettingsConfig.saveSettingsToFile(fileConfig);
        DatabaseConfig.saveSettingsToFile(fileConfig);
    }

    private static void loadConfigSettingsToMemory(CommentedFileConfig fileConfig){
        SettingsConfig.loadSettingsToMemory(fileConfig);
        DatabaseConfig.loadSettingsToMemory(fileConfig);
    }

}