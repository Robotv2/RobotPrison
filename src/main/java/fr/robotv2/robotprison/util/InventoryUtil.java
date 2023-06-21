package fr.robotv2.robotprison.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryUtil {

    private InventoryUtil() { }

    public static void removeAmountFromSlot(Player player, int slot, int amount) {

        final PlayerInventory inventory = player.getInventory();
        final ItemStack stack = inventory.getItem(slot);

        if(stack == null) {
            return;
        }

        final int oldAmount = stack.getAmount();

        if(oldAmount <= amount) {
            inventory.setItem(slot, new ItemStack(Material.AIR));
        } else {
            stack.setAmount(oldAmount - amount);
        }

        player.updateInventory();
    }

    public static void removeAmountFromHeldItem(Player player, int amount, boolean isOffHand) {
        removeAmountFromSlot(player, isOffHand ? 45 : player.getInventory().getHeldItemSlot(), amount);
    }
}
