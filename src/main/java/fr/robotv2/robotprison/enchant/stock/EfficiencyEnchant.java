package fr.robotv2.robotprison.enchant.stock;

import fr.robotv2.robotprison.enchant.PrisonEnchant;
import fr.robotv2.robotprison.enchant.PrisonItem;
import fr.robotv2.robotprison.player.PrisonPlayer;
import org.bukkit.enchantments.Enchantment;

import java.util.EnumSet;
import java.util.concurrent.ThreadLocalRandom;

public class EfficiencyEnchant extends PrisonEnchant {

    public EfficiencyEnchant() {
        super("efficiency");
    }

    @Override
    public void onEquip(PrisonPlayer player, PrisonItem item, int level, ThreadLocalRandom random) {
        final int amplifier = (int) Math.round(level * 0.5);
        item.getItem().editMeta(meta -> meta.addEnchant(Enchantment.DIG_SPEED, amplifier, true));
    }

    @Override
    public EnumSet<EnchantType> getType() {
        return EnumSet.of(EnchantType.HOLD);
    }
}
