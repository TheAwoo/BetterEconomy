package me.SuperRonanCraft.BetterEconomy.events;

import me.SuperRonanCraft.BetterEconomy.BetterEconomy;
import me.SuperRonanCraft.BetterEconomy.web.Updater;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {

    public void load(boolean reload) {
        if (!reload)
            Bukkit.getPluginManager().registerEvents(this, getPl());
        for (Player p : Bukkit.getOnlinePlayers())
            loadPlayer(p);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        loadPlayer(e.getPlayer());
        update(e);
    }

    public void update(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (getPl().getPerms().getUpdate(player))
            if (Updater.updatedVersion != null && !getPl().getDescription().getVersion().equals(Updater.updatedVersion))
                getPl().getMessages().sms(player, "&7There is currently an update for &6BetterEconony &7version &e#" +
                        Updater.updatedVersion + " &7you have version &e#" + getPl().getDescription().getVersion());
    }

    public void loadPlayer(Player p) {
        boolean newPlayer = getPl().getDatabase().playerCreate(p.getUniqueId(), p, 25.0);
        getPl().getEconomy().createPlayerAccount(p);
        if (newPlayer)
            getPl().getEconomy().depositPlayer(p, 25.0);
        else {
            double bal = getPl().getDatabase().playerBalance(p.getUniqueId());
            getPl().getEconomy().depositPlayer(p, bal);
        }
    }

    @EventHandler
    private void onLeave(PlayerQuitEvent e) {
        unloadPlayer(e.getPlayer());
    }

    public void unloadPlayer(Player p) {
        double amt = getPl().getEconomy().getBalance(p);
        getPl().getDatabase().playerSetBalance(p.getUniqueId(), amt);
        getPl().getDatabase().playerClean(p.getUniqueId());
        getPl().getEconomy().playerRemove(p.getUniqueId());
    }

    private BetterEconomy getPl() {
        return BetterEconomy.getInstance();
    }
}
