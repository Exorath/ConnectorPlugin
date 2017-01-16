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
import com.exorath.plugin.connector.config.JoinCommand;
import com.exorath.service.connector.api.ConnectorServiceAPI;
import com.exorath.service.translation.api.TranslatableString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by toonsev on 1/15/2017.
 */
public class CommandManager implements Listener {
    private static final int THROTTLED_TIMEOUT = 40;
    private ConfigProvider configProvider;
    private PlayerJoiner playerJoiner;
    private Set<String> recentIds = new HashSet<>();

    public CommandManager(ConfigProvider configProvider, PlayerJoiner playerJoiner) {
        this.configProvider = configProvider;
        this.playerJoiner = playerJoiner;
    }

    @EventHandler
    public void onPreCMD(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        if (!msg.startsWith("/"))
            return;
        if (msg.length() > 30)
            return;
        String uuid = event.getPlayer().getUniqueId().toString();
        if (containsId(uuid))
            return;
        String sendCmd = msg.substring(1);
        System.out.println("Sendcmd: " + sendCmd);
        for (JoinCommand joinCommand : configProvider.getJoinCommands()) {
            for (TranslatableString translatableCmd : joinCommand.getCmds()) {
                String cmd = translatableCmd.translate(Main.TRANSLATE_PACKAGE_ID, uuid);
                System.out.println(cmd);
                if (cmd.equals(sendCmd)) {
                    event.setCancelled(true);
                    handleCmd(event.getPlayer(), joinCommand, cmd);

                    return;
                }
            }
        }
    }

    private void handleCmd(Player player, JoinCommand joinCommand, String cmd) {
        addId(player.getUniqueId().toString());
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> playerJoiner.join(player, joinCommand.getName(), joinCommand.getFilter()));
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getInstance(), () -> removeId(player.getUniqueId().toString()), THROTTLED_TIMEOUT);
    }

    private synchronized void addId(String id) {
        recentIds.add(id);
    }

    private synchronized boolean containsId(String id) {
        return recentIds.contains(id);
    }

    private synchronized void removeId(String id) {
        recentIds.remove(id);
    }
}
