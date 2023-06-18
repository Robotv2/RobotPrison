package fr.robotv2.robotprison.enchant;

import fr.robotv2.robotprison.RobotPrison;
import fr.robotv2.robotprison.SpecialKeys;
import fr.robotv2.robotprison.util.ColorUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        prisonItem.actualizeItem();
        return prisonItem;
    }

    public static PrisonItem newPrisonItem(ItemStack item) {
        return newPrisonItem(item, UUID.randomUUID());
    }

    private final UUID itemUUID;
    private final ItemStack item;

    private int level;
    private double exp;
    private int blockMined;

    private final Map<PrisonEnchant, Integer> enchants = new ConcurrentHashMap<>();

    public PrisonItem(final UUID itemUUID, final ItemStack item) {
        this.itemUUID = itemUUID;
        this.item = item;
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

        actualizeItem();
    }

    public UUID getItemUniqueId() {
        return itemUUID;
    }

    public ItemStack getItem() {
        return item;
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

    public void saveItem() {
        SpecialKeys.ITEM_LEVEL.setValue(item, level);
        SpecialKeys.ITEM_EXP.setValue(item, exp);
        SpecialKeys.BLOCK_MINED.setValue(item, blockMined);

        final JSONObject json = new JSONObject();
        getEnchants().forEach(enchant -> json.put(enchant.getId(), getEnchantLevel(enchant)));
        SpecialKeys.PRISON_ENCHANT.setValue(item, json.toJSONString());
    }

    public void actualizeItem() {
        this.item.editMeta(meta -> {
            meta.setLore(this.getDefaultLore());
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        });
    }

    private List<String> getDefaultLore() {
        final List<String> lines = Stream.of(
                "&8&l==============",
                "&7LEVEL: " + level,
                "&7EXP: " + exp,
                "&7BLOCK MINED: " + blockMined,
                "&8&l=============="
        ).map(ColorUtil::colorize).collect(Collectors.toList());
        for(PrisonEnchant enchant : getEnchants()) {
            lines.add(ColorUtil.colorize(enchant.getDisplay() + ": " + getEnchantLevel(enchant)));
        }
        lines.add(ColorUtil.colorize("&8&l=============="));
        return lines;
    }
}
