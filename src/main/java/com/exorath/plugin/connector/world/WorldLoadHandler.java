/*
 * Copyright 2017 Exorath
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.exorath.plugin.connector.world;

import com.exorath.clickents.ClickEntAPI;
import com.exorath.clickents.api.ClickableEntity;
import com.exorath.exoHUD.DisplayProperties;
import com.exorath.exoHUD.HUDText;
import com.exorath.exoHUD.locations.row.HologramLocation;
import com.exorath.exoHUD.removers.NeverRemover;
import com.exorath.exoHUD.texts.ChatColorText;
import com.exorath.exoHUD.texts.PlainText;
import com.exorath.plugin.connector.Main;
import com.exorath.plugin.connector.PlayerJoiner;
import com.exorath.service.translation.api.TranslatableString;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Created by toonsev on 5/27/2017.
 */
public class WorldLoadHandler implements Listener {
    private static final Gson GSON = new Gson();
    private ClickEntAPI clickEntAPI;
    private PlayerJoiner playerJoiner;

    private HashMap<Entity, ConnectorNPC> npcs = new HashMap<>();

    public WorldLoadHandler(PlayerJoiner playerJoiner, ClickEntAPI clickEntAPI) {
        this.playerJoiner = playerJoiner;
        this.clickEntAPI = clickEntAPI;
        Bukkit.getWorlds().forEach(world -> loadWorld(world));
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        loadWorld(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity().hasMetadata("connector"))
            if (event.isCancelled())
                event.setCancelled(false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Map.Entry<Entity, ConnectorNPC> entry : npcs.entrySet())
            loadArmorStand(entry.getKey().getWorld(), entry.getValue());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onChunkUnloadEvent(ChunkUnloadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (entity.hasMetadata("connector")) {
                entity.remove();
                npcs.get(entity).getArmorStand().setLoaded(false);
                clickEntAPI.removeEntity(entity);
            }
        }
    }

    private void loadWorld(World world) {
        File config = new File(world.getWorldFolder(), "connector.json");
        if (config == null || !config.exists() || !config.isFile())
            return;
        System.out.println("ConnectorPlugin loading world " + world.getName() + "...");
        try {
            WorldConfiguration worldConfiguration = GSON.fromJson(new JsonReader(new FileReader(config)), WorldConfiguration.class);
            loadConnectorWorld(world, worldConfiguration);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadConnectorWorld(World world, WorldConfiguration worldConfiguration) {
        List<ConnectorNPC> npcs = worldConfiguration.getNpcs();
        if (npcs == null)
            return;
        npcs.forEach(connectorNPC -> loadNpc(world, connectorNPC));
    }

    private Set<String> clickSpam = new HashSet<>();

    private void loadNpc(World world, ConnectorNPC npc) {
        System.out.println("Loading an npc");
        if (npc.getArmorStand() != null) {
            addHologram(world, npc);
            loadArmorStand(world, npc);
        }
    }

    private ArmorStand loadArmorStand(World world, ConnectorNPC npc) {
        if (npc.getArmorStand().isLoaded())
            return null;
        System.out.println("loading an armorstand");
        ArmorStand armorStand = npc.getArmorStand().load(world);
        if (armorStand == null) {
            System.out.println("Armorstand loading halted (chunk not loaded)");
            return null;
        }
        armorStand.setGravity(false);
        armorStand.setAI(false);
        armorStand.setSilent(true);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);
        makeClickable(armorStand, npc);
        return armorStand;
    }

    private void addHologram(World world, ConnectorNPC npc) {
        Location headLoc = new Location(world, npc.getArmorStand().getX(), npc.getArmorStand().getY() + 2.5d, npc.getArmorStand().getZ());
        HologramLocation hologram = new HologramLocation(headLoc);
        addText(hologram, ChatColorText.markup(PlainText.plain(npc.getName())).color(ChatColor.AQUA), 0);
        addText(hologram, ChatColorText.markup(PlainText.plain("X Players")).color(ChatColor.YELLOW), -2d);
        if (npc.getShortDescription() != null)
            addText(hologram, ChatColorText.markup(PlainText.plain(npc.getShortDescription())).color(ChatColor.GRAY), -1d);
        if (npc.getUpdate() != null)
            addText(hologram, ChatColorText.markup(PlainText.plain(npc.getUpdate())).color(ChatColor.YELLOW).bold(true), 1d);

    }

    private void addText(HologramLocation hologramLocation, HUDText text, double priority) {
        hologramLocation.addText(
                text, DisplayProperties.create(priority, NeverRemover.never()));
        hologramLocation.teleport(hologramLocation.getLocation().clone().add(0, 0.25d, 0));
    }

    private void makeClickable(ArmorStand armorStand, ConnectorNPC npc) {
        ClickableEntity<ArmorStand> clickable = clickEntAPI.makeClickable(armorStand);
        clickable.getInteractObservable().subscribe(event -> {
            String uuid = event.getPlayer().getUniqueId().toString();
            if (clickSpam.contains(uuid))
                event.getPlayer().sendMessage("Don't click so fast...");
            clickSpam.add(uuid);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> clickSpam.remove(uuid), 40);
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
                    () -> playerJoiner.join(event.getPlayer(), new TranslatableString(npc.getName(), npc.getName()), npc.getFilter()));
            ;
        });
    }
}
