package xd.arkosammy.signlogger;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xd.arkosammy.signlogger.configuration.SignLoggerConfig;
import xd.arkosammy.signlogger.util.EventRegistrar;

// TODO: Parse world registry keys from DB back into actual world reg keys objects
// TODO: Parse blockpos string from DB back into actual blockpos object
// TODO: Create search command feature for customizable queries
// TODO: Expand config system to customize which sign edit events to log
public class SignLogger implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("sign-logger");
	
	@Override
	public void onInitializeServer() {

		SignLoggerConfig.initializeConfig();
		EventRegistrar.registerEvents();
		LOGGER.info("I will try my best to log your sign edit events :)");

	}

}