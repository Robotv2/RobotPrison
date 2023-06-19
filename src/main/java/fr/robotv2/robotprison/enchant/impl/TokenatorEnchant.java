package fr.robotv2.robotprison.enchant.impl;

import fr.robotv2.robotprison.enchant.PrisonEnchant;
import fr.robotv2.robotprison.events.CurrencyValueChangeEvent;
import fr.robotv2.robotprison.player.Currency;
import fr.robotv2.robotprison.player.PrisonPlayer;
import fr.robotv2.robotprison.util.NumberUtil;
import org.bukkit.ChatColor;

import java.util.EnumSet;
import java.util.concurrent.ThreadLocalRandom;

public final class TokenatorEnchant extends PrisonEnchant {

    private final double minToken;
    private final double maxToken;

    public TokenatorEnchant() {
        super("tokenator");
        this.minToken = getConfigurationSection().getDouble("min-token");
        this.maxToken = getConfigurationSection().getDouble("max-token");
    }

    @Override
    public void execute(PrisonPlayer player, int level, ThreadLocalRandom random) {
        final double value = NumberUtil.roundDecimal(random.nextDouble(minToken, maxToken), 2);
        player.setCurrency(Currency.TOKEN, player.getCurrency(Currency.TOKEN) + value, CurrencyValueChangeEvent.CurrencyChangeReason.ENCHANT);
        player.getPlayer().sendMessage(ChatColor.GREEN + "You've just won " + value + " token(s).");
    }

    @Override
    public EnumSet<EnchantType> getType() {
        return EnumSet.of(EnchantType.MINE);
    }
}

