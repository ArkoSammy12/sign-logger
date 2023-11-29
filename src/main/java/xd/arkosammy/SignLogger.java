package xd.arkosammy;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xd.arkosammy.configuration.SignLoggerConfig;
import xd.arkosammy.util.EventRegistrar;

public class SignLogger implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("sign-logger");
	
	@Override
	public void onInitializeServer() {

		SignLoggerConfig.initializeConfig();
		EventRegistrar.registerEvents();
		LOGGER.info("I will try my best to log your sign edit events :)");

	}

}