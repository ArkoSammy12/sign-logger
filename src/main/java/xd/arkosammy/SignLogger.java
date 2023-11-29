package xd.arkosammy;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xd.arkosammy.util.EventRegistrar;

//TODO: HANDLE NPE WARNINGS
//TODO: CREATE CONFIG
//TODO: IMPLEMENT DATABASE PURGE
public class SignLogger implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("sign-logger");

	@Override
	public void onInitializeServer() {

		EventRegistrar.registerEvents();

	}

}