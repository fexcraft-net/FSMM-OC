package net.fexcraft.mod.fsmm.util;

import com.mojang.authlib.GameProfile;
import li.cil.oc.api.machine.Context;
import net.fexcraft.lib.mc.utils.Print;
import net.fexcraft.mod.fsmm.data.Account;
import net.fexcraft.mod.fsmm.data.Bank;
import net.fexcraft.mod.fsmm.data.TransferRequest;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author FatalMerlin (merlin.brandes@gmail.com)
 */
public class TransferManager {
    private static TransferManager instance;
    private final HashMap<String, TransferRequest> requests = new HashMap<>();
    // TODO: Remove transfers after 60 seconds

    private TransferManager() {}

    public static TransferManager getInstance() {
        if (instance == null) {
            instance = new TransferManager();
        }
        return instance;
    }

    /**
     * Requests a transfer from one player to another, and stores the transfer for use with the
     * /fsmm accept and /fsmm reject commands.
     * Also notifies the player that a transfer request has been sent.
     *
     * @param  fromPlayer  the name of the player sending the transfer
     * @param  toPlayer    the name of the player receiving the transfer
     * @param  amount      the amount of the transfer
     * @param  context     the context of the transfer
     */
    public void requestTransfer(String fromPlayer, String toPlayer, double amount, Context context) {
        requests.put(fromPlayer, new TransferRequest(fromPlayer, toPlayer, amount, context));

        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(fromPlayer);

        if (player != null) {
            Print.chat(player, "=== &bNew transfer request&r ===");
            Print.chat(player, "&bTo&r: &a" + toPlayer);
            Print.chat(player, "&bAmount&r: &a" + amount + "&7F$");
            Print.chat(player, "Use &a`/fsmm accept`&r to accept or &c`/fsmm reject`&r to reject it");
            Print.chat(player, "Use &b`/fsmm`&r to check your current balance");
            Print.chat(player, "The request will be automatically &crejected&r in &b60&r seconds");
        }
    }

    /**
     * Replies to a transfer request, either accepting or rejecting it, and removes the request from the list.
     * If the request has been accepted, it will execute the transfer.
     *
     * @param  fromPlayer   the player who sent the request
     * @param  accepted     whether the request has been accepted or not
     */
    private void replyToTransferRequest(String fromPlayer, boolean accepted) {
        TransferRequest request = requests.get(fromPlayer);
        if (request == null) return;
        if (accepted) {
            accepted = executeTransferRequest(request.getFromPlayer(), request.getToPlayer(), request.getAmount());
        }
        request.getContext().signal("fsmm_transfer", request.getFromPlayer(), request.getToPlayer(), request.getAmount(), accepted);
        requests.remove(request.getFromPlayer());
    }

    /**
     * Executes a transfer request from one player to another.
     *
     * @param  fromPlayer  the username of the player making the transfer
     * @param  toPlayer    the username of the player receiving the transfer
     * @param  amount      the amount to be transferred
     * @return             true if the transfer was successful, false otherwise
     */
    private boolean executeTransferRequest(String fromPlayer, String toPlayer, double amount) {
        boolean success = false;
        long actualAmount = (long) (amount * 1000);

        Map<String, Account> accounts = DataManager.getAccountsOfType("player");
        if (accounts == null) return false;

        String fromUUID = getUuidFromUsername(fromPlayer);
        String toUUID = getUuidFromUsername(toPlayer);

        Account fromAccount = accounts.get(fromUUID);
        Account toAccount = accounts.get(toUUID);

        EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(fromPlayer);

        // bank should do the null-check on toAccount
        if (fromAccount != null) {
            fixAccountBank(fromAccount);
            fixAccountBank(toAccount);

            Bank bank = fromAccount.getBank();
            success = bank.processAction(Bank.Action.TRANSFER, player, fromAccount, actualAmount, toAccount, false);
        }

        if (!success) {
            Print.chat(player, "&cError executing transaction: Check available balance.");
        } else {
            Print.chat(player, "&aTransaction successful! New balance: &r" + Config.getWorthAsString(fromAccount.getBalance()));
        }

        return success;
    }

    /**
     * Fixes the bank of the given account if it is null by setting it to the default bank.
     * The default bank is retrieved by ID because @see DataManager#getDefaultBank() can return null in certain cases.
     *
     * @param  account  the account to fix the bank for
     */
    private void fixAccountBank(Account account) {
        if (account != null && account.getBank() == null)
            account.setBank(DataManager.getBank("0"));
    }

    /**
     * Retrieves the UUID associated with the given username.
     *
     * @param  username  the username for which to retrieve the UUID
     * @return           the UUID associated with the given username, or null if the username is not found
     */
    private String getUuidFromUsername(String username){
        GameProfile profile = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getGameProfileForUsername(username);
        return profile == null ? null : profile.getId().toString();
    }

    /**
     * Accepts a transfer request from a player.
     * Used by the `/fsmm accept` command.
     *
     * @param  fromPlayer  the player who sent the transfer request
     * @return             void
     */
    public void acceptTransferRequest(String fromPlayer) {
        replyToTransferRequest(fromPlayer, true);
    }

    /**
     * Rejects a transfer request from a player.
     * Used by the `/fsmm reject` command.
     *
     * @param  fromPlayer  the player who sent the transfer request
     */
    public void rejectTransferRequest(String fromPlayer) {
        replyToTransferRequest(fromPlayer, false);
    }

    /**
     * Checks if there is a transfer request for the given player.
     *
     * @param  fromPlayer  the name of the player to check for a transfer request
     * @return             true if there is a transfer request for the player, false otherwise
     */
    public boolean hasTransferRequest(String fromPlayer) {
        return requests.containsKey(fromPlayer);
    }
}
