package org.mswsplex.anticheat.checks.movement;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.mswsplex.anticheat.checks.Check;
import org.mswsplex.anticheat.checks.CheckType;
import org.mswsplex.anticheat.data.CPlayer;
import org.mswsplex.anticheat.msws.AntiCheat;

public class Step1 implements Check, Listener {

	private AntiCheat plugin;

	@Override
	public CheckType getType() {
		return CheckType.MOVEMENT;
	}

	@Override
	public void register(AntiCheat plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.isFlying())
			return;
		if (cp.timeSince("lastLiquid") < 400)
			return;

		if (cp.timeSince("lastSlimeBlock") < 1000)
			return;

		Location to = event.getTo(), from = event.getFrom();

		if (to.getY() == from.getY())
			return;

		if (!cp.isOnGround() || cp.isInWeirdBlock())
			return;

		if (cp.isBlockAbove() && cp.distanceToGround() < 2)
			return;

		double diff = to.getY() - from.getY();

		double[] regular = {
				// Regular movement
				0.41999998688697815, -0.015555072702198913, -0.07840000152587834, 0.2000000476837016,
				0.20000004768311896, 0.12160004615724063, 0.20000004768371582, 0.20000004768371582, 0.2000000476836732,
				0.20000004768365898,

				// Slab interactions
				.5, -0.03584062504455687,

				// Climbing interactions
				0.1176000022888175, 0.07248412919149416, 0.11760000228882461 };

		boolean normal = false;

		for (double d : regular) {
			if (diff == d) {
				normal = true;
				break;
			}
		}

		if (normal)
			return;

		cp.flagHack(this, 5);
	}

	@Override
	public String getCategory() {
		return "Step";
	}

	@Override
	public String getDebugName() {
		return "Step#1";
	}
}
