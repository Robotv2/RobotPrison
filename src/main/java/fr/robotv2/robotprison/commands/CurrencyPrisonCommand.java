package fr.robotv2.robotprison.commands;

import fr.robotv2.robotprison.events.CurrencyValueChangeEvent;
import fr.robotv2.robotprison.player.Currency;
import fr.robotv2.robotprison.player.PrisonPlayer;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

@Command({"robotprison", "rp"})
public class CurrencyPrisonCommand {

    @Command({"token", "tokens"})
    public void token(BukkitCommandActor actor, @Optional OfflinePlayer target) {

        String targetName;
        UUID targetUUID;

        if(target == null) {
            targetName = actor.requirePlayer().getName();
            targetUUID = actor.requirePlayer().getUniqueId();
        } else {
            targetName = target.getName();
            targetUUID = target.getUniqueId();
        }

        final PrisonPlayer prisonPlayer = PrisonPlayer.getPrisonPlayer(targetUUID, true);
        actor.reply(ChatColor.GREEN + targetName + "'s token(s): " + prisonPlayer.getCurrency(Currency.TOKEN));
    }

    @Command({"gem", "gems"})
    public void gem(BukkitCommandActor actor, @Optional OfflinePlayer target) {

        String targetName;
        UUID targetUUID;

        if(target == null) {
            targetName = actor.requirePlayer().getName();
            targetUUID = actor.requirePlayer().getUniqueId();
        } else {
            targetName = target.getName();
            targetUUID = target.getUniqueId();
        }

        final PrisonPlayer prisonPlayer = PrisonPlayer.getPrisonPlayer(targetUUID, true);
        actor.reply(ChatColor.GREEN + targetName + "'s gem(s): " + prisonPlayer.getCurrency(Currency.GEM));
    }

    @Subcommand("give")
    @Usage("give <currency> <value> [<target>]")
    @CommandPermission("robotprison.command.give")
    public void giveCurrency(BukkitCommandActor actor, Currency currency, double value, @Optional Player optional) {

        if(actor.isConsole() && optional == null) {
            actor.reply(ChatColor.RED + "Can't be done from the console.");
            return;
        }

        final Player target = optional == null ? actor.requirePlayer() : optional;
        final PrisonPlayer prisonPlayer = PrisonPlayer.getPrisonPlayer(target);
        final double current = prisonPlayer.getCurrency(currency);

        prisonPlayer.setCurrency(currency, current + value, CurrencyValueChangeEvent.CurrencyChangeReason.COMMAND);
    }

    @Subcommand("take")
    @Usage("take <currency> <value> [<target>]")
    @CommandPermission("robotprison.command.take")
    public void takeCurrency(BukkitCommandActor actor, Currency currency, double value, @Optional Player optional) {

        if(actor.isConsole() && optional == null) {
            actor.reply(ChatColor.RED + "Can't be done from the console.");
            return;
        }

        final Player target = optional == null ? actor.requirePlayer() : optional;
        final PrisonPlayer prisonPlayer = PrisonPlayer.getPrisonPlayer(target);
        final double current = prisonPlayer.getCurrency(currency);

        if(current - value < 0) {
            prisonPlayer.setCurrency(currency, 0, CurrencyValueChangeEvent.CurrencyChangeReason.COMMAND);
        } else {
            prisonPlayer.setCurrency(currency, current - value, CurrencyValueChangeEvent.CurrencyChangeReason.COMMAND);
        }
    }

    @Subcommand("set")
    @Usage("set <currency> <value> [<target>]")
    @CommandPermission("robotprison.command.set")
    public void setCurrency(BukkitCommandActor actor, Currency currency, double value, @Optional Player optional) {

        if(actor.isConsole() && optional == null) {
            actor.reply(ChatColor.RED + "Can't be done from the console.");
            return;
        }

        final Player target = optional == null ? actor.requirePlayer() : optional;
        final PrisonPlayer prisonPlayer = PrisonPlayer.getPrisonPlayer(target);

        if(value < 0) {
            actor.reply(ChatColor.RED + "Can't be less than 0.");
            return;
        }

        prisonPlayer.setCurrency(currency, value, CurrencyValueChangeEvent.CurrencyChangeReason.COMMAND);
    }
}
