package fr.robotv2.robotprison.enchant;

import fr.robotv2.robotprison.RobotPrison;
import fr.robotv2.robotprison.player.PrisonPlayer;
import fr.robotv2.robotprison.util.ColorUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public abstract class PrisonEnchant {

    public enum EnchantType {
        HOLD,
        MINE,
        ;
    }

    private final ConfigurationSection section;
    private final boolean enabled;

    private final String id;
    private final String display;
    private final int maxLevel;

    private final double chance;
    private final double levelMultiplier;

    private final double price;
    private final double priceMultiplier;

    private final int requiredLevel;

    private PrisonEnchant(@NotNull ConfigurationSection section) {
        this.section = Objects.requireNonNull(section);
        this.id = section.getName().toLowerCase();
        this.enabled = section.getBoolean("enabled");
        this.display = Objects.requireNonNull(section.getString("display"));
        this.maxLevel = section.getInt("max-level");
        this.chance = section.getDouble("chance");
        this.levelMultiplier = section.getDouble("level-multiplier");
        this.price = section.getDouble("start-price");
        this.priceMultiplier = section.getDouble("price-multiplier");
        this.requiredLevel = section.getInt("required-level");
    }

    public PrisonEnchant(String enchantID) {
        this(RobotPrison.get().getEnchantConfiguration().getConfigurationSection(enchantID));
    }

    public ConfigurationSection getConfigurationSection() {
        return section;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getId() {
        return id;
    }

    public String getDisplay() {
        return ColorUtil.colorize(display);
    }

    public double getChance() {
        return chance;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public double getLevelMultiplier() {
        return levelMultiplier;
    }

    public double getPrice() {
        return price;
    }

    public double getPriceMultiplier() {
        return priceMultiplier;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public boolean test(PrisonItem item, ThreadLocalRandom random) {
        final int level = item.getEnchantLevel(this);
        return level != 0 && getChance() + (level - 1) * levelMultiplier > random.nextDouble(0, 100);
    }

    public void execute(PrisonPlayer player, int level, ThreadLocalRandom random) {}
    public void onEquip(PrisonPlayer player, PrisonItem item, int level, ThreadLocalRandom random) {}
    public void onUnEquip(PrisonPlayer player, PrisonItem item, int level, ThreadLocalRandom random) {}

    public abstract EnumSet<EnchantType> getType();

    @Override
    public boolean equals(Object otherEnchant) {
        if(otherEnchant == this) return true;
        if(!(otherEnchant instanceof PrisonEnchant enchant)) return false;
        return Objects.equals(enchant.getId(), this.getId());
    }
}
