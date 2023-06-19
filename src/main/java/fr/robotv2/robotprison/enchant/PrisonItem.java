package fr.robotv2.robotprison.enchant;

import fr.robotv2.robotprison.RobotPrison;
import fr.robotv2.robotprison.SpecialKeys;
import fr.robotv2.robotprison.util.ColorUtil;
import fr.robotv2.robotprison.util.PlaceholderUtil;
import fr.robotv2.robotprison.util.config.ConfigAPI;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PrisonItem {

    static Map<UUID, PrisonItem> items = new ConcurrentHashMap<>();

    @Nullable
    public static PrisonItem toPrisonItem(final ItemStack item) {
        final UUID uuid = SpecialKeys.ITEM_UUID.getValue(item, UUID.class);

        if(uuid == null) {
            return null;
        }

        if(items.containsKey(uuid)) {
            return items.get(uuid);
        }

        final PrisonItem prisonItem = new PrisonItem(uuid, item);
        items.put(uuid, prisonItem);

        return prisonItem;
    }

    public static boolean isPrisonItem(final ItemStack item) {
        return SpecialKeys.ITEM_UUID.hasValue(item);
    }

    public static PrisonItem newPrisonItem(ItemStack item, UUID uniqueID) {
        SpecialKeys.ITEM_UUID.setValue(item, uniqueID);
        SpecialKeys.ITEM_LEVEL.setValue(item, 1);
        SpecialKeys.ITEM_EXP.setValue(item, 0D);
        SpecialKeys.BLOCK_MINED.setValue(item, 0);

        final PrisonItem prisonItem = PrisonItem.toPrisonItem(item);
        prisonItem.actualizeItem(item);
        return prisonItem;
    }

    public static PrisonItem newPrisonItem(ItemStack item) {
        return newPrisonItem(item, UUID.randomUUID());
    }

    private final UUID itemUUID;
    private final ItemStack from;

    private int level;
    private double exp;
    private int blockMined;

    private final Map<PrisonEnchant, Integer> enchants = new ConcurrentHashMap<>();

    public PrisonItem(final UUID itemUUID, final ItemStack item) {

        this.itemUUID = itemUUID;
        this.from = item;

        this.level = SpecialKeys.ITEM_LEVEL.getValue(item, Integer.class, 0);
        this.exp = SpecialKeys.ITEM_EXP.getValue(item, Double.class,0D);
        this.blockMined = SpecialKeys.BLOCK_MINED.getValue(item, Integer.class, 0);

        final String enchantsJSON = SpecialKeys.PRISON_ENCHANT.getValue(item, String.class);
        if(enchantsJSON != null) {
            final JSONObject json = (JSONObject) JSONValue.parse(enchantsJSON);
            for(Object object : json.entrySet()) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
                final String enchantID = (String) entry.getKey();
                final int level = ((Number) entry.getValue()).intValue();
                if(RobotPrison.get().getEnchantManager().exist(enchantID)) {
                    this.setEnchantLevel(enchantID, level);
                }
            }
        }

        actualizeItem(item);
    }

    public UUID getItemUniqueId() {
        return itemUUID;
    }

    public ItemStack getItem() {
        final ItemStack stack = from.clone();
        actualizeItem(stack);
        return stack;
    }


    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getExp() {
        return exp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    public int getBlockMined() {
        return blockMined;
    }

    public void incrementBlockMined() {
        ++this.blockMined;
    }

    public Set<PrisonEnchant> getEnchants() {
        return enchants.keySet();
    }

    public int getEnchantLevel(String enchantID) {
        return getEnchantLevel(RobotPrison.get().getEnchantManager().getEnchant(enchantID));
    }

    public int getEnchantLevel(PrisonEnchant enchant) {
        return enchants.getOrDefault(enchant, 0);
    }

    public void setEnchantLevel(String enchantID, int level) {
        final PrisonEnchant enchant = RobotPrison.get().getEnchantManager().getEnchant(enchantID);
        Objects.requireNonNull(enchant, "enchantment doesn't exist");
        enchants.put(enchant, level);
    }

    public void saveItem(ItemStack item) {
        SpecialKeys.ITEM_LEVEL.setValue(item, level);
        SpecialKeys.ITEM_EXP.setValue(item, exp);
        SpecialKeys.BLOCK_MINED.setValue(item, blockMined);

        final JSONObject json = new JSONObject();
        getEnchants().forEach(enchant -> json.put(enchant.getId(), getEnchantLevel(enchant)));
        SpecialKeys.PRISON_ENCHANT.setValue(item, json.toJSONString());
    }

    public void actualizeItem(ItemStack item) {
        item.editMeta(meta -> {

            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

            String name = ConfigAPI.getConfig("configuration").get().getString("pickaxe-meta.name");
            name = ColorUtil.colorize(name);
            name = PlaceholderUtil.PRISON_ITEM_PLACEHOLDER.apply(this, name);
            meta.setDisplayName(name);

            final List<String> lore = ConfigAPI.getConfig("configuration").get()
                    .getStringList("pickaxe-meta.lore").stream()
                    .map(ColorUtil::colorize)
                    .map(input -> PlaceholderUtil.PRISON_ITEM_PLACEHOLDER.apply(this, input))
                    .toList();
            meta.setLore(lore);
        });
    }
}
