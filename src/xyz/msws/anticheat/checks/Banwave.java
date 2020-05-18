package xyz.msws.anticheat.checks;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.data.CPlayer;
import xyz.msws.anticheat.utils.MSG;

public class Banwave {

	private NOPE plugin;

	private long lastBanwave;

	public Banwave(NOPE plugin) {
		this.plugin = plugin;
		if (plugin.getConfig().getInt("BanwaveRate", -1) == -1) {
			MSG.log("Banwaves are disabled, reset your config if this is not intended.");
			return;
		}
		runBanwave(false).runTaskTimer(this.plugin, 0, plugin.getConfig().getInt("BanwaveRate"));
	}

	public BukkitRunnable runBanwave(boolean forced) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				for (OfflinePlayer player : plugin.getPlayerManager().getLoadedPlayers()) {
					CPlayer cp = plugin.getCPlayer(player);
					if (!cp.hasSaveData("isBanwaved"))
						continue;
					cp.ban(cp.getSaveString("isBanwaved"), forced ? Timing.MANUAL_BANWAVE : Timing.BANWAVE);
					cp.removeSaveData("isBanwaved");
					cp.saveData();
				}
				if (!forced)
					lastBanwave = System.currentTimeMillis();
			}
		};
	}

	public long timeToNextBanwave() {
		return plugin.getConfig().getInt("BanwaveRate") * 50 + lastBanwave - System.currentTimeMillis();
	}
}