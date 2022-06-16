package fr.robotv2.robotprison;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

public enum SpecialKeys {

    ITEM_UUID("item-uuid"),
    ITEM_LEVEL("item-level"),
    ITEM_EXP("item-exp"),
    BLOCK_MINED("block-mined"),
    PRISON_ENCHANT("prison-enchant");

    private final String key;

    SpecialKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setValue(final ItemStack item, final Serializable value) {
        new NBTItem(item, true).setObject(getKey(), value);
    }

    public <T> T getValue(final ItemStack item, Class<T> type) {
        return getValue(item, type, null);
    }

    @Nullable
    public <T> T getValue(final ItemStack item, Class<T> type, @Nullable final T defaultValue) {
        final NBTItem nbtItem = new NBTItem(item);
        final T obj = nbtItem.getObject(getKey(), type);
        return obj != null ? obj : defaultValue;
    }

    public boolean hasValue(final ItemStack item) {

        if(item == null || item.getType() == Material.AIR) {
            return false;
        }

        return new NBTItem(item).hasKey(getKey());
    }
}
