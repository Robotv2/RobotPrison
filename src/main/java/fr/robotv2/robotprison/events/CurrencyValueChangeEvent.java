package fr.robotv2.robotprison.events;

import fr.robotv2.robotprison.player.Currency;
import fr.robotv2.robotprison.player.PrisonPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CurrencyValueChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean cancel = false;

    private final Currency currency;
    private final CurrencyValueChangeEvent.CurrencyChangeReason reason;
    private final PrisonPlayer prisonPlayer;

    private final double from;
    private double to;

    public CurrencyValueChangeEvent(Currency currency, CurrencyValueChangeEvent.CurrencyChangeReason reason, PrisonPlayer prisonPlayer, double from, double to) {
        this.currency = currency;
        this.reason = reason;
        this.prisonPlayer = prisonPlayer;
        this.from = from;
        this.to = to;
    }

    public enum CurrencyChangeReason {
        UNKNOWN, ENCHANT, MINE, COMMAND;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public Currency getCurrency() {
        return currency;
    }

    public CurrencyValueChangeEvent.CurrencyChangeReason getReason() {
        return reason;
    }

    public PrisonPlayer getPrisonPlayer() {
        return prisonPlayer;
    }

    public double getFrom() {
        return from;
    }

    public double getTo() {
        return to;
    }

    public void setTo(double value) {
        this.to = value;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
}
