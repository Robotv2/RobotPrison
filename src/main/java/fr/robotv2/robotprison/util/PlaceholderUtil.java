package fr.robotv2.robotprison.util;

import fr.robotv2.robotprison.enchant.PrisonItem;
import org.bukkit.entity.Player;

public class PlaceholderUtil {

    private PlaceholderUtil() { }

    @FunctionalInterface
    public interface InternalPlaceholder<T> {
        String apply(T value, String input);
    }

    public final static InternalPlaceholder<Player> PLAYER_PLACEHOLDER = ((value, input) -> input
            .replace("%player%", value.getName())
    );

    public final static InternalPlaceholder<PrisonItem> PRISON_ITEM_PLACEHOLDER = ((value, input) -> input
            .replace("%pickaxe_level%", String.valueOf(value.getLevel()))
            .replace("%pickaxe_exp%", String.valueOf(value.getExp()))
            .replace("%pickaxe_block_mined%", String.valueOf(value.getBlockMined()))
    );
}
