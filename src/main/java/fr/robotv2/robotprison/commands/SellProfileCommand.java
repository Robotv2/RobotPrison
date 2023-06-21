package fr.robotv2.robotprison.commands;

import fr.robotv2.robotprison.RobotPrison;
import fr.robotv2.robotprison.enums.SellMode;
import fr.robotv2.robotprison.player.PrisonPlayer;
import fr.robotv2.robotprison.util.NumberUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.BukkitCommandActor;

public class SellProfileCommand {

    private final RobotPrison instance;

    public SellProfileCommand(RobotPrison instance) {
        this.instance = instance;
    }

    @Command({"robotsell", "rs"})
    public void sell(BukkitCommandActor actor, SellMode mode) {

        final Player player = actor.requirePlayer();
        final PrisonPlayer prisonPlayer = PrisonPlayer.getPrisonPlayer(player);

        final double value = instance.getSellManager().sell(prisonPlayer, mode);

        if(value <= 0D) {
            actor.reply(ChatColor.RED + "Vous n'avez rien à vendre.");
            return;
        }

        actor.reply(ChatColor.GREEN + "Vous avez reçu " + NumberUtil.formatNumber(value) + "$ pour la vente de vos items.");
    }
}
