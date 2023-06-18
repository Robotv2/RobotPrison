package fr.robotv2.robotprison.ui.stock;

import fr.robotv2.robotprison.RobotPrison;
import fr.robotv2.robotprison.enchant.PrisonEnchant;
import fr.robotv2.robotprison.enchant.PrisonItem;
import fr.robotv2.robotprison.ui.Gui;
import fr.robotv2.robotprison.util.ColorUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PickaxeEnchantGui extends Gui {

    @Override
    public String getName(Player player, Object... objects) {
        return "Enchantment's menu.";
    }

    @Override
    public int getSize() {
        return 54;
    }

    @Override
    public void contents(Player player, Inventory inv, Object... objects) {
        final ItemStack empty = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
        for(int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, empty);
        }

        final PrisonItem item = (PrisonItem) objects[0];
        final int count = 0;

        for(PrisonEnchant enchant : RobotPrison.get().getEnchantManager().getEnchants()) {
            inv.setItem(count, getBookItem(item, enchant));
        }
    }

    @Override
    public void onClick(Player player, Inventory inv, ItemStack current, int slot, @NotNull ClickType click) {

    }

    @Override
    public void onClose(Player player, InventoryCloseEvent event) {

    }

    private ItemStack getBookItem(PrisonItem item, PrisonEnchant enchant) {
        final ItemStack book = new ItemStack(Material.BOOK, 1);
        final ItemMeta meta = book.getItemMeta();

        meta.setDisplayName(enchant.getDisplay());
        final List<String> lore =
                Stream.of(
                        "",
                        "&fNiveau actuel: &b" + item.getEnchantLevel(enchant),
                        "&fNiveau requis: &b" + enchant.getRequiredLevel(),
                        "",
                        "&eClic-droit pour monter d'un niveau.")
                .map(ColorUtil::colorize).collect(Collectors.toList());
        meta.setLore(lore);

        book.setItemMeta(meta);
        return book;
    }
}
