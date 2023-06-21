package fr.robotv2.robotprison.profile;

import fr.robotv2.robotprison.enums.SellMode;
import fr.robotv2.robotprison.player.PrisonPlayer;
import fr.robotv2.robotprison.util.InventoryUtil;
import fr.robotv2.robotprison.util.dependencies.VaultAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellManager {

    public double sell(PrisonPlayer prisonPlayer, SellMode mode) {

        final Player player = prisonPlayer.getPlayer();
        final SellProfile profile = SellProfile.getSellProfile(prisonPlayer.getPlayer());

        if(profile == null) {
            return 0D;
        }

        return mode == SellMode.HAND ? sellHand(player, profile) : sellInventory(player, profile);
    }

    private double sellHand(Player player, SellProfile profile) {

        final int heldItemSlot = player.getInventory().getHeldItemSlot();
        final ItemStack itemInHand = player.getInventory().getItem(heldItemSlot);

        if(itemInHand == null ||profile.getPrice(itemInHand.getType()) == 0D) {
            return 0D;
        }

        InventoryUtil.removeAmountFromSlot(player, heldItemSlot, itemInHand.getAmount());
        final double money = profile.getPrice(itemInHand.getType()) * itemInHand.getAmount();
        VaultAPI.giveMoney(player, money);

        return money;
    }

    private double sellInventory(Player player, SellProfile profile) {

        double money = 0;

        for(ItemStack content : player.getInventory().getContents()) {
            if(content != null && profile.getPrice(content.getType()) != 0D) {
                money = money + (profile.getPrice(content.getType()) * content.getAmount());
                player.getInventory().removeItemAnySlot(content);
            }
        }

        VaultAPI.giveMoney(player, money);
        return money;
    }
}
