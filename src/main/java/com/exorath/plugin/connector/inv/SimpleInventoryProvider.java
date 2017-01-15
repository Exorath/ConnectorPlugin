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

package com.exorath.plugin.connector.inv;

import com.exorath.plugin.connector.InventoryListener;
import com.exorath.plugin.connector.config.ConfigProvider;
import com.exorath.plugin.connector.Main;
import com.exorath.plugin.connector.config.GameDescription;
import com.exorath.service.connector.api.ConnectorServiceAPI;
import com.exorath.service.translation.api.TranslatableString;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Todo: Maybe rate limit and cache inventory creation?
 * TODO: Create static inventory registry.
 * TODO: Inject player count and live game data
 * Created by toonsev on 1/13/2017.
 */
public class SimpleInventoryProvider implements InventoryProvider {
    private ConfigProvider configProvider;
    private ConnectorServiceAPI connectorServiceAPI;
    private static final int THROTTLE_TIMEOUT = 40;

    public SimpleInventoryProvider(ConfigProvider configProvider, ConnectorServiceAPI connectorServiceAPI) {
        this.configProvider = configProvider;
        this.connectorServiceAPI = connectorServiceAPI;
    }

    /**
     * This inventory will not be opened, as this should be done by the invoker.
     *
     * @param player
     * @return
     */
    @Override
    public Inventory getGameInv(Player player) {
        GamesInventoryListener gamesInventoryListener = new GamesInventoryListener(player);
        return gamesInventoryListener.getInventory();
    }

    private static void decorateIS(ItemStack itemStack, GameDescription gameDescription, Player player) {
        ItemMeta im = itemStack.getItemMeta();

        TranslatableString translatableName = gameDescription.getName() == null ? DEFAULT_IS_NAME : gameDescription.getName();
        String name = translatableName.translate(Main.TRANSLATE_PACKAGE_ID, player.getUniqueId().toString());
        String coloredName = containsColor(name.substring(0, 2)) ? name : ChatColor.GREEN + name;

        TranslatableString translatableShortDesc = gameDescription.getShortDescription();
        if (translatableShortDesc != null) {
            String shortDesc = translatableShortDesc.translate(Main.TRANSLATE_PACKAGE_ID, player.getUniqueId().toString());
            String coloredShortDesc = containsColor(shortDesc.substring(0, 2)) ? shortDesc : ChatColor.GRAY + shortDesc;
            coloredName += " " + coloredShortDesc;
        }
        im.setDisplayName(coloredName);//Name + Short desc

        if (gameDescription.getLongDescription() != null && gameDescription.getLongDescription().size() > 0) {
            List<String> lore = gameDescription.getLongDescription().stream()
                    .map(translatableString -> translatableString.translate(Main.TRANSLATE_PACKAGE_ID, player.getUniqueId().toString()))
                    .map(str -> containsColor(str.substring(0, 2)) ? str : ChatColor.GRAY + str).collect(Collectors.toList());
            lore.add(0, "");
            im.setLore(lore);
        }

        itemStack.setItemMeta(im);
    }

    private class GamesInventoryListener implements InventoryListener {
        private Inventory inventory;
        private boolean throttled = false;

        public GamesInventoryListener(Player player) {
            inventory = Bukkit.createInventory(null, 45);
            Main.getInventoryRegistry().register(this);
            for (Map.Entry<Integer, GameDescription> entry : configProvider.getGamesBySlot().entrySet()) {
                if (entry.getKey() < 0 || entry.getKey() >= inventory.getSize()) {
                    Bukkit.getLogger().log(Level.WARNING, "GameDescription " + entry.getValue().getName() + " slot " + entry.getKey() + " out of bounds.");
                    continue;
                }
                ItemStack itemStack = entry.getValue().getPlainItemStack();
                if (itemStack == null)
                    itemStack = DEFAULT_PLAIN_IS;
                decorateIS(itemStack, entry.getValue(), player);

                inventory.setItem(entry.getKey(), itemStack);
            }
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }

        @Override
        public void onClick(InventoryClickEvent event) {
            event.setCancelled(true);
            if (event.getClickedInventory().equals(event.getView().getBottomInventory()))
                return;
            GameDescription gameDescription = configProvider.getGamesBySlot().get(event.getSlot());
            if (gameDescription == null)
                return;
            if (throttled) {
                event.getWhoClicked().sendMessage(ChatColor.RED + "Don't click so fast.");
                return;
            }
            throttled = true;
            String name = gameDescription.getName().translate(Main.TRANSLATE_PACKAGE_ID, event.getWhoClicked().getUniqueId().toString());
            event.getWhoClicked().sendMessage(ChatColor.GREEN + JOINING_TRANS.translate(Main.TRANSLATE_PACKAGE_ID, event.getWhoClicked().getUniqueId().toString()).replaceAll("\\{\\{0\\}\\}", name));
            Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(),
                    () -> {
                        String serverId = connectorServiceAPI.joinServer(event.getWhoClicked().getUniqueId().toString(), gameDescription.getFilter());
                        if (serverId == null) {
                            event.getWhoClicked().sendMessage(ChatColor.RED + NO_SERVER_FOUND_TRANS.translate(Main.TRANSLATE_PACKAGE_ID, event.getWhoClicked().getUniqueId().toString()));
                            return;
                        }
                        event.getWhoClicked().sendMessage(ChatColor.GREEN + JOINED_GAME_TRANS.translate(Main.TRANSLATE_PACKAGE_ID, event.getWhoClicked().getUniqueId().toString()).replaceAll("\\{\\{0\\}\\}", name));
                    });//TODO: Check if this is thread safe (if it's not: check severity)
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> throttled = false, THROTTLE_TIMEOUT);
        }

        @Override
        public void onClose(Player player) {
            Main.getInventoryRegistry().unregister(this);
        }
    }

    private static boolean containsColor(String str) {
        return !ChatColor.stripColor(str).equals(str);
    }

    private static final TranslatableString DEFAULT_IS_NAME = new TranslatableString("is.name.default", "Unknown Name");
    private static final TranslatableString JOINING_TRANS = new TranslatableString("joining", "Joining {{0}}...");
    private static final TranslatableString JOINED_GAME_TRANS = new TranslatableString("joinedgame", "You've joined {{0}}, the system is sending you now!");
    private static final TranslatableString NO_SERVER_FOUND_TRANS = new TranslatableString("noserverfound", "No server was found, try again or contact an administrator.");
    private static final ItemStack DEFAULT_PLAIN_IS = new ItemStack(Material.EMERALD);
}
