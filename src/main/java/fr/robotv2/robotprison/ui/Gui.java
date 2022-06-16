package fr.robotv2.robotprison.ui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class Gui implements InventoryHolder {
    private Inventory inv;

    public void startContents(Player player, Inventory inv, Object... objects) {
        this.inv = inv;
        contents(player, inv, objects);
    }

    public abstract String getName(Player player, Object... objects);
    public abstract int getSize();
    public abstract void contents(Player player, Inventory inv, Object... objects);
    public abstract void onClick(Player player, Inventory inv, ItemStack current, int slot, @NotNull ClickType click);
    public abstract void onClose(Player player, InventoryCloseEvent event);

    @Override
    public @NotNull Inventory getInventory() {
        return inv;
    }
}
