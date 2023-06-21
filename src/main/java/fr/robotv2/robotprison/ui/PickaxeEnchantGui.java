package fr.robotv2.robotprison.ui;

import fr.mrmicky.fastinv.FastInvScheme;
import fr.mrmicky.fastinv.ItemBuilder;
import org.bukkit.Material;

public class PickaxeEnchantGui extends FastInvScheme {

    private final static String[] GUI_MASKS = {
            "111111111",
            "110101011",
            "222232222"
    };

    public PickaxeEnchantGui() {
        super(9 * 3, "FastInvScheme");
        this.addMasks(GUI_MASKS);
        this.setSchemeItem('1', new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).build(), null);
        this.setSchemeItem('0', new ItemBuilder(Material.PLAYER_HEAD).build(), event -> event.getWhoClicked().sendMessage("You just clicked on your player head."));
        this.setSchemeItem('2', new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).build(), null);
        this.setSchemeItem('3', new ItemBuilder(Material.BARRIER).build(), event -> event.getWhoClicked().closeInventory());
    }
}
