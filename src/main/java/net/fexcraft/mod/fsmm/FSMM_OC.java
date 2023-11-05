package net.fexcraft.mod.fsmm;

import net.fexcraft.mod.fsmm.oc.driver.ATMDriver;
import net.fexcraft.mod.fsmm.util.Command;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

/**
 * @author Ferdinand Calo' (FEX___96)
 * @author FatalMerlin (merlin.brandes@gmail.com)
 */
@Mod(modid = FSMM_OC.MODID, name = "FSMM-OC", version = FSMM_OC.VERSION,
	acceptableRemoteVersions = "*", acceptedMinecraftVersions = "*", dependencies = "required-after:fsmm;after:opencomputers")
public class FSMM_OC {

	public static final String MODID = "fsmm_oc";
	public static final String VERSION = "1.0.0";
	
	@Mod.EventHandler
    public void init(FMLInitializationEvent event){
		if (Loader.isModLoaded("opencomputers")) {
			ATMDriver.register();
		}
    }

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event){
		event.registerServerCommand(new Command());
	}

}