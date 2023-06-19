package fr.robotv2.robotprison.enchant.impl;

import fr.robotv2.robotprison.enchant.PrisonEnchant;
import fr.robotv2.robotprison.enchant.PrisonItem;
import fr.robotv2.robotprison.player.PrisonPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumSet;
import java.util.concurrent.ThreadLocalRandom;

public class HasteEnchant extends PrisonEnchant {

    public HasteEnchant() {
        super("haste");
    }

    @Override
    public void onEquip(PrisonPlayer prisonPlayer, PrisonItem item, int level, ThreadLocalRandom random) {
        final Player player = prisonPlayer.getPlayer();
        if(player == null) {
            return;
        }
        final PotionEffect effect = new PotionEffect(PotionEffectType.FAST_DIGGING, 9999, level, false, false, false);
        player.addPotionEffect(effect);
    }

    @Override
    public void onUnEquip(PrisonPlayer prisonPlayer, PrisonItem item, int level, ThreadLocalRandom random) {
        final Player player = prisonPlayer.getPlayer();
        if(player == null) {
            return;
        }
        player.removePotionEffect(PotionEffectType.FAST_DIGGING);
    }

    @Override
    public EnumSet<EnchantType> getType() {
        return EnumSet.of(EnchantType.HOLD);
    }
}
