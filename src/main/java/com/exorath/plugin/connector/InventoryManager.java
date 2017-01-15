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

package com.exorath.plugin.connector;

import com.exorath.plugin.connector.config.ConfigProvider;
import com.exorath.plugin.connector.config.GamesItem;
import com.exorath.plugin.connector.inv.InventoryProvider;
import com.exorath.service.translation.api.TranslatableString;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by toonsev on 1/14/2017.
 */
public class InventoryManager implements Listener {
    private GamesItem gamesItem;
    private ConfigProvider configProvider;
    private InventoryProvider inventoryProvider;

    public InventoryManager(ConfigProvider configProvider, InventoryProvider inventoryProvider) {
        this.configProvider = configProvider;
        this.inventoryProvider = inventoryProvider;
        this.gamesItem = configProvider.getGamesItem();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        decoratePlayer(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(gamesItem == null)
            return;
        if(event.getPlayer().getInventory().getHeldItemSlot() == gamesItem.getSlot()){
            event.setCancelled(true);
            Inventory inventory = inventoryProvider.getGameInv(event.getPlayer());
            event.getPlayer().openInventory(inventory);
        }
    }

    private void decoratePlayer(Player player) {
        putGamesItem(player);
    }

    private void putGamesItem(Player player) {
        if (gamesItem == null)
            return;
        if (gamesItem.getSlot() < 0 || gamesItem.getSlot() >= 9)
            return;
        ItemStack is = gamesItem.getPlainItem();
        if (is == null)
            is = DEFAULT_GAMES_ITEM_IS;

        TranslatableString transName = gamesItem.getName() == null ? DEFAULT_NAME : gamesItem.getName();
        String name = transName.translate(Main.TRANSLATE_PACKAGE_ID, player.getUniqueId().toString());

        ItemMeta im = is.getItemMeta();
        im.setDisplayName(name);
        is.setItemMeta(im);
        player.getInventory().setItem(gamesItem.getSlot(), is);
    }

    private static final TranslatableString DEFAULT_NAME = new TranslatableString("item.games.name", ChatColor.GREEN + "Join Game");

    private static final ItemStack DEFAULT_GAMES_ITEM_IS = new ItemStack(Material.COMPASS);
}
