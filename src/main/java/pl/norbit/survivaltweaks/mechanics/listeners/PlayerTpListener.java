package pl.norbit.survivaltweaks.mechanics.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import pl.norbit.survivaltweaks.mechanics.MechanicsLoader;
import pl.norbit.survivaltweaks.mechanics.model.Mechanic;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static pl.norbit.survivaltweaks.utils.TaskUtils.*;

public class PlayerTpListener implements Listener {
    private final Map<UUID, Horse> horses = new HashMap<>();
    private final Map<UUID, Pig> pigs = new HashMap<>();

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if(MechanicsLoader.isDisabled(Mechanic.HORSE_TP)){
            return;
        }

        if(e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL){
            return;
        }

        Player p = e.getPlayer();
        Location loc = e.getTo();

        if (horses.containsKey(p.getUniqueId())) {
            Horse horse = horses.get(p.getUniqueId());

            if(horse.isDead()) return;

            horse.teleport(loc);
            syncLater(() -> horse.addPassenger(p), 6);
        }

        if(pigs.containsKey(p.getUniqueId())){
            Pig pig = pigs.get(p.getUniqueId());

            if(pig.isDead()) return;

            pig.teleport(loc);
            syncLater(()->pig.addPassenger(p),6);
        }
    }

    @EventHandler
    public void onMount(EntityMountEvent e) {
        if(MechanicsLoader.isDisabled(Mechanic.HORSE_TP)){
            return;
        }

        if (e.getMount() instanceof Horse horse && e.getEntity() instanceof Player p) {
            horses.put(p.getUniqueId(), horse);
        }

        if (e.getMount() instanceof Pig pig && e.getEntity() instanceof Player p) {
            pigs.put(p.getUniqueId(), pig);
        }
    }

    @EventHandler
    public void onDismount(PlayerToggleSneakEvent e) {
        if(MechanicsLoader.isDisabled(Mechanic.HORSE_TP)){
            return;
        }

        Player p = e.getPlayer();

        if(p.getVehicle() == null) return;

        if(p.getVehicle() instanceof Pig){
            pigs.remove(p.getUniqueId());
        }

        if(p.getVehicle() instanceof Horse){
            horses.remove(p.getUniqueId());
        }
    }
}
