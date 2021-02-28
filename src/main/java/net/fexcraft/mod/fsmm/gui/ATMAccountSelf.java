package net.fexcraft.mod.fsmm.gui;

import static net.fexcraft.mod.fsmm.gui.Processor.LISTENERID;

import java.io.IOException;
import java.util.ArrayList;

import net.fexcraft.lib.mc.gui.GenericGui;
import net.fexcraft.lib.mc.utils.Formatter;
import net.fexcraft.mod.fsmm.util.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class ATMAccountSelf extends GenericGui<ATMContainer> {
	
	private static final ResourceLocation texture = new ResourceLocation("fsmm:textures/gui/account_self.png");
	private ArrayList<String> tooltip = new ArrayList<>();
	private BasicButton action, cancel, exit, expand;
	private BasicButton[] numbers = new BasicButton[12];
	private BasicText acc0, acc1, bal, fee, tot, amount;
	private boolean expanded, mode;
	private TextField amount_field;

	public ATMAccountSelf(EntityPlayer player, boolean bool){
		super(texture, new ATMContainer(player), player);
		this.deftexrect = false;
		this.mode = bool;
		this.xSize = 256;
		this.ySize = 147;
	}

	@Override
	protected void init(){
		this.texts.put("acc0", acc0 = new BasicText(guiLeft + 6, guiTop + 6, 244, null, "Synchronizing....").autoscale());
		this.texts.put("acc1", acc1 = new BasicText(guiLeft + 6, guiTop + 16, 244, null, "Please wait.").autoscale());
		this.texts.put("balance", bal = new BasicText(guiLeft + 6, guiTop + 32, 244, null, "").autoscale());
		this.texts.put("amount", amount = new BasicText(guiLeft + 6, guiTop + 44, 244, null, "").autoscale());
		this.fields.put("amount", amount_field = new TextField(0, fontRenderer, guiLeft + 5, guiTop + 43, 246, 10).setEnableBackground(false));
		this.texts.put("fee", fee = new BasicText(guiLeft + 6, guiTop + 56, 244, null, "").autoscale());
		this.texts.put("total", tot = new BasicText(guiLeft + 6, guiTop + 68, 233, null, "").autoscale());
		this.buttons.put("confirm", action = new BasicButton("action", guiLeft + 241, guiTop + 67, mode ? 0 : 10, 246, 10, 10, true));
		this.buttons.put("expand", expand = new BasicButton("expand", guiLeft + 191, guiTop + 79, 191, 148, 51, 8, true));
		for(int i = 0; i < numbers.length; i++){
			int x = 192 + ((i % 3) * 17), y = 79 + ((i / 3) * 16);
			String id = i < 9 ? "n" + i : i == 9 ? "cancel" : i == 10 ? "n0" : "exit";
			this.buttons.put(id, numbers[i] = new BasicButton(id, guiLeft + x, guiTop + y, x, y, 15, 15, true));
		}
		this.container.sync("account", "bank");
	}

	@Override
	protected void predraw(float pticks, int mouseX, int mouseY){
		amount_field.setVisible(expand.visible = !(amount.visible = expanded));
		for(BasicButton button : numbers) button.visible = expanded;
		if(container.bank != null){
			//show fee
		}
		if(container.account != null){
			acc0.string = container.account.getName();
			acc1.string = container.account.getType() + ":" + container.account.getId();
			bal.string = Config.getWorthAsString(container.account.getBalance());
		}
	}

	@Override
	protected void drawbackground(float pticks, int mouseX, int mouseY){
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, 86);
		if(expanded){
			this.drawTexturedModalRect(guiLeft + 184, guiTop + 79, 184, 79, 64, 68);
		}
		else{
			this.drawTexturedModalRect(guiLeft + 184, guiTop + 79, 184, 148, 64, 12);
		}
	}
	
	@Override
	protected void drawlast(float pticks, int mouseX, int mouseY){
		tooltip.clear();
		if(expand.visible && expand.hovered) tooltip.add(Formatter.format("&7Open number pad."));
		if(action.hovered) tooltip.add(Formatter.format("&9Confirm " + (mode ? "&6Deposit" : "&eWidthdraw")));
		if(expanded && numbers[9].hovered) tooltip.add(Formatter.format("&cCancel Input"));
		if(expanded && numbers[11].hovered) tooltip.add(Formatter.format("&7Close number pad."));
	    if(tooltip.size() > 0) this.drawHoveringText(tooltip, mouseX, mouseY, mc.fontRenderer);
	}

	@Override
	protected boolean buttonClicked(int mouseX, int mouseY, int mouseButton, String key, BasicButton button){
		switch(button.name){
			case "action":{
				
				return true;
			}
			case "expand": return expanded = true;
			case "exit": return !(expanded = false);
		}
		return false;
	}

	@Override
	protected void scrollwheel(int am, int x, int y){
		//
	}

	@Override
    public void keyTyped(char typedChar, int keyCode) throws IOException{
        if(keyCode == 1){
			openGui(GuiHandler.ATM_MAIN, new int[]{ 0, 0, 0 }, LISTENERID);
            return;
        }
        else super.keyTyped(typedChar, keyCode);
    }

}
