package net.fexcraft.mod.fsmm.util;

import net.fexcraft.lib.common.utils.Formatter;
import net.fexcraft.lib.mc.utils.Print;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

/**
 * @author Ferdinand Calo' (FEX___96)
 * @author FatalMerlin (merlin.brandes@gmail.com)
 */
public class Command extends CommandBase{

	public static final String PREFIX = Formatter.format("&0[&bFSMM-OC&0]&7 ");
  
    public Command(){ return; }
    
    @Override 
    public String getName(){ 
        return "fsmm-oc";
    } 

    @Override         
    public String getUsage(ICommandSender sender){ 
        return "/fsmm-oc <args>";
    }
    
    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender){
    	return true;
    }

    @Override 
    public void execute(MinecraftServer server, ICommandSender sender, String[] args){
    	switch(args[0]){
	    	case "help":{
	        	Print.chat(sender, PREFIX + "= = = = = = = = = = =");
	        	Print.chat(sender, "&bArguments:");
	        	Print.chat(sender, "&7/fsmm-oc accept");
	        	Print.chat(sender, "&7/fsmm-oc reject");
	    		return;
	    	}
			case "accept": {
				if (!TransferManager.getInstance().hasTransferRequest(sender.getName())) {
					Print.chat(sender, "&bNo Transfer Requests.");
					return;
				}
				TransferManager.getInstance().acceptTransferRequest(sender.getName());
				Print.chat(sender, "&aTransfer Request accepted.");
				return;
			}
			case "reject": {
				if (!TransferManager.getInstance().hasTransferRequest(sender.getName())) {
					Print.chat(sender, "&bNo Transfer Requests.");
					return;
				}
				TransferManager.getInstance().rejectTransferRequest(sender.getName());
				Print.chat(sender, "&cTransfer Request rejected.");
				return;
			}
    		default:{
    			Print.chat(sender, "&cInvalid Argument.");
    			return;
    		}
    	}
    }

	@Override 
    public boolean isUsernameIndex(String[] var1, int var2){ 
    	return false;
    }
    
}

