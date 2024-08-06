package net.slqmy.rank_system_plugin.events.custom_events;

import javax.annotation.Nullable;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import net.slqmy.rank_system_plugin.types.Rank;

public final class RankChangeEvent extends Event implements Cancellable {
	private static final HandlerList HANDLERS = new HandlerList();

	private final OfflinePlayer player;
	private final Rank previousRank;
	private Rank newRank;

	private boolean cancelled;

	public RankChangeEvent(@NotNull final OfflinePlayer player, @Nullable final Rank previousRank,
			@NotNull final Rank newRank) {
		this.player = player;

		this.previousRank = previousRank;
		this.newRank = newRank;
	}

	@Nullable
	public Rank getPreviousRank() {
		return previousRank;
	}

	@NotNull
	public Rank getNewRank() {
		return newRank;
	}

	@NotNull
	public OfflinePlayer getPlayer() {
		return player;
	}

	public void setNewRank(@NotNull final Rank newRank) {
		this.newRank = newRank;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(final boolean cancel) {
		cancelled = cancel;
	}

	@Override
	@NotNull
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	@NotNull
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
