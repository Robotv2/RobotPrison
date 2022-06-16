package fr.robotv2.robotprison.enchant;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class PrisonEnchantManager {

    private final Map<String, PrisonEnchant> enchants = new ConcurrentHashMap<>();

    /**
     * Clear all registered enchantments.
     */
    public void clearEnchants() {
        this.enchants.clear();
    }

    public boolean exist(String id) {
        return enchants.containsKey(id.toLowerCase());
    }

    @Nullable
    public PrisonEnchant getEnchant(String id) {
        return enchants.get(id.toLowerCase());
    }

    public Collection<PrisonEnchant> getEnchants() {
        return enchants.values();
    }

    public List<String> getEnchantsIds() {
        return getEnchants().stream().map(PrisonEnchant::getId).collect(Collectors.toList());
    }

    public void registerPrisonEnchant(PrisonEnchant enchant) {
        this.enchants.put(enchant.getId().toLowerCase(), enchant);
    }
}
