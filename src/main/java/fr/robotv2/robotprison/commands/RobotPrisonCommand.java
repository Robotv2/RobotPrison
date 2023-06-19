package fr.robotv2.robotprison.commands;

import fr.robotv2.robotprison.RobotPrison;
import fr.robotv2.robotprison.enchant.PrisonEnchant;
import fr.robotv2.robotprison.enchant.PrisonItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"robotprison", "rp"})
public class RobotPrisonCommand {

    @Dependency
    private RobotPrison instance;

    @Subcommand("reload")
    @Usage("reload")
    @CommandPermission("robotprison.command.reload")
    public void onPluginReload(BukkitCommandActor actor) {
        instance.onReload();
        actor.reply(ChatColor.GREEN + "The plugin has been reloaded successfully.");
    }

    @Subcommand("prisonitem")
    @Usage("prisonitem <material> [<target>]")
    @CommandPermission("robotprison.command.prisonitem")
    public void givePrisonItem(BukkitCommandActor actor, Material material, @Optional Player optional) {

        if(actor.isConsole() && optional == null) {
            actor.reply(ChatColor.RED + "Can't be done from the console.");
            return;
        }

        final Player target = optional == null ? actor.requirePlayer() : optional;

        final ItemStack item = new ItemStack(material, 1);
        final PrisonItem prisonItem = PrisonItem.newPrisonItem(item);

        target.getInventory().addItem(prisonItem.getItem());
        target.updateInventory();
    }

    @Subcommand("enchant")
    @Usage("enchant <enchant-id> <level>")
    @AutoComplete("@enchants")
    @CommandPermission("robotprison.command.enchant")
    public void onEnchant(BukkitCommandActor actor, PrisonEnchant enchant, int level) {
        final Player player = actor.requirePlayer();
        final ItemStack item = player.getInventory().getItemInMainHand();

        if(!PrisonItem.isPrisonItem(item)) {
            actor.reply(ChatColor.RED + "The item you're holding isn't a prison item.");
            return;
        }

        if(enchant == null) {
            actor.reply(ChatColor.RED + "This enchantment doesn't exist.");
            return;
        }

        final PrisonItem prisonItem = PrisonItem.toPrisonItem(item);
        prisonItem.setEnchantLevel(enchant.getId(), level);
        prisonItem.actualizeItem(item);

        actor.reply(ChatColor.GREEN + "The item has been successfully enchanted.");
    }
}
