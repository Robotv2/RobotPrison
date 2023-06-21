package fr.robotv2.robotprison.listeners;

import fr.robotv2.robotprison.enchant.PrisonEnchant;
import fr.robotv2.robotprison.enchant.PrisonItem;
import fr.robotv2.robotprison.player.PrisonPlayer;
import fr.robotv2.robotprison.profile.SellProfile;
import fr.robotv2.robotprison.ui.PickaxeEnchantGui;
import fr.robotv2.robotprison.util.dependencies.VaultAPI;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public class PickaxeListeners implements Listener {

    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final JavaPlugin instance;

    public PickaxeListeners(JavaPlugin plugin) {
        this.instance = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        if(!event.getAction().isRightClick()) {
            return;
        }

        final ItemStack item = event.getItem();

        if(!PrisonItem.isPrisonItem(item)) {
            return;
        }

        new PickaxeEnchantGui().open(event.getPlayer());
        // GuiManager.open(event.getPlayer(), PickaxeEnchantGui.class, PrisonItem.toPrisonItem(item));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBreak(final BlockBreakEvent event) {

        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        final ItemStack itemInHand = player.getInventory().getItemInMainHand();

        final PrisonPlayer prisonPlayer = PrisonPlayer.getPrisonPlayer(player);
        final PrisonItem prisonItem = PrisonItem.toPrisonItem(itemInHand);

        if(prisonPlayer == null || prisonItem == null) {
            return;
        }

        event.setDropItems(false);

        prisonItem.getEnchants().stream()
                .filter(PrisonEnchant::isEnabled)
                .filter(enchant -> enchant.getType().contains(PrisonEnchant.EnchantType.MINE))
                .filter(enchant -> enchant.test(prisonItem, random))
                .forEach(enchant -> enchant.execute(prisonPlayer, prisonItem.getEnchantLevel(enchant), random));

        prisonPlayer.getAutoSellRecord().incrementBlockMined(1);
        prisonItem.incrementBlockMined();
        prisonItem.actualizeItem(itemInHand);

        if(prisonPlayer.hasAutoSell()) {
            final SellProfile profile = SellProfile.getSellProfile(player);
            final double price = profile.getPrice(block.getType());
            VaultAPI.giveMoney(player, price);
            prisonPlayer.getAutoSellRecord().incrementEarnings(price);
        } else {
            final ItemStack stack = new ItemStack(block.getType(), 1);
            player.getInventory().addItem(stack);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHeld(PlayerItemHeldEvent event) {

        final Player player = event.getPlayer();

        final PlayerInventory inventory = player.getInventory();
        final ItemStack previous = inventory.getItem(event.getPreviousSlot());
        final ItemStack current = inventory.getItem(event.getNewSlot());

        final PrisonPlayer prisonPlayer = PrisonPlayer.getPrisonPlayer(player);

        if(PrisonItem.isPrisonItem(previous)) {
            final PrisonItem prisonItem = PrisonItem.toPrisonItem(previous);
            prisonItem.getEnchants().stream()
                    .filter(PrisonEnchant::isEnabled)
                    .filter(enchant -> enchant.getType().contains(PrisonEnchant.EnchantType.HOLD))
                    .forEach(enchant -> enchant.onUnEquip(prisonPlayer, prisonItem, prisonItem.getEnchantLevel(enchant), random));
        }

        if(PrisonItem.isPrisonItem(current)) {
            final PrisonItem prisonItem = PrisonItem.toPrisonItem(current);
            prisonItem.getEnchants().stream()
                    .filter(PrisonEnchant::isEnabled)
                    .filter(enchant -> enchant.getType().contains(PrisonEnchant.EnchantType.HOLD))
                    .forEach(enchant -> enchant.onEquip(prisonPlayer, prisonItem, prisonItem.getEnchantLevel(enchant), random));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {

        final Player player = event.getPlayer();
        final PrisonPlayer prisonPlayer = PrisonPlayer.getPrisonPlayer(player);

        final PlayerInventory inventory = player.getInventory();
        final ItemStack itemInHand = inventory.getItem(EquipmentSlot.HAND);

        if(itemInHand == null) {
            return;
        }

        final PrisonItem prisonItem = PrisonItem.toPrisonItem(itemInHand);

        if(prisonPlayer == null) {
            return;
        }

        prisonItem.getEnchants().stream()
                .filter(PrisonEnchant::isEnabled)
                .filter(enchant -> enchant.getType().contains(PrisonEnchant.EnchantType.HOLD))
                .forEach(enchant -> enchant.onEquip(prisonPlayer, prisonItem, prisonItem.getEnchantLevel(enchant), random));
    }
}
