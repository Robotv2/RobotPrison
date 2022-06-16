package fr.robotv2.robotprison.listeners;

import fr.robotv2.robotprison.enchant.PrisonEnchant;
import fr.robotv2.robotprison.enchant.PrisonItem;
import fr.robotv2.robotprison.player.PrisonPlayer;
import fr.robotv2.robotprison.profile.SellProfile;
import fr.robotv2.robotprison.ui.GuiManager;
import fr.robotv2.robotprison.ui.stock.PickaxeEnchantGui;
import fr.robotv2.robotprison.util.dependencies.VaultAPI;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public class PickaxeListeners extends RobotListener {

    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    public PickaxeListeners(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        final Action action = event.getAction();
        if(!action.isRightClick()) {
            return;
        }

        final ItemStack item = event.getItem();

        if(!PrisonItem.isPrisonItem(item)) {
            return;
        }

        GuiManager.open(event.getPlayer(), PickaxeEnchantGui.class, PrisonItem.toPrisonItem(item));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onBreak(final BlockBreakEvent event) {

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        final PrisonPlayer prisonPlayer = PrisonPlayer.getPrisonPlayer(player);
        final PrisonItem prisonItem = PrisonItem.toPrisonItem(player.getInventory().getItemInMainHand());

        if(prisonPlayer == null || prisonItem == null) {
            return;
        }

        event.setDropItems(false);

        prisonItem.getEnchants().stream()
                .filter(PrisonEnchant::isEnabled)
                .filter(enchant -> enchant.getType().contains(PrisonEnchant.EnchantType.MINE))
                .filter(enchant -> enchant.test(prisonItem, random))
                .forEach(enchant -> enchant.execute(prisonPlayer, prisonItem.getEnchantLevel(enchant), random));

        prisonItem.incrementBlockMined();
        prisonItem.actualizeItem();

        if(prisonPlayer.hasAutoSell()) {
            final SellProfile profile = SellProfile.getSellProfile(player);
            final double price = profile.getPrice(block.getType());
            VaultAPI.giveMoney(player, price);
        } else {
            final ItemStack stack = new ItemStack(block.getType(), 1);
            player.getInventory().addItem(stack);
        }
    }

    @EventHandler(ignoreCancelled = true)
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
}
