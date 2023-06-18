package fr.robotv2.robotprison.profile;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SellProfile {

    private final String permission;
    private final int priority;

    private final Map<Material, Double> sells = new ConcurrentHashMap<>();

    public SellProfile(String permission, int priority) {
        this.permission = permission;
        this.priority = priority;
    }

    public String getPermission() {
        return permission;
    }

    public int getPriority() {
        return priority;
    }

    public void addMaterial(Material material, double price) {
        this.sells.put(material, price);
    }

    public void removeMaterial(Material material) {
        this.sells.remove(material);
    }

    public double getPrice(Material material) {
        return sells.getOrDefault(material, 0D);
    }

    //<<- STATIC METHOD ->>

    final static List<SellProfile> profiles = Collections.synchronizedList(new ArrayList<>());

    public static void registerProfile(SellProfile profile) {
        profiles.add(profile);
    }

    @Nullable
    public static SellProfile getSellProfile(Player player) {
        return profiles.stream()
                .filter(profile -> player.hasPermission(profile.getPermission()))
                .max((profile1, profile2) -> {

                    if(profile2.priority == profile1.priority) {
                        return 0;
                    }

                    return profile1.priority > profile2.priority ? 1 : -1;
                })
                .orElse(null);
    }
}
