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

import com.exorath.service.connector.api.ConnectorServiceAPI;
import com.exorath.service.connector.res.Filter;
import com.exorath.service.translation.api.TranslatableString;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.HumanEntity;

/**
 * Created by toonsev on 1/15/2017.
 */
public class PlayerJoiner {
    private ConnectorServiceAPI connectorServiceAPI;

    public PlayerJoiner(ConnectorServiceAPI connectorServiceAPI) {
        this.connectorServiceAPI = connectorServiceAPI;
    }

    public String join(HumanEntity player, TranslatableString name, Filter filter) {
        String uuid = player.getUniqueId().toString();
        String nameString = name.translate(Main.TRANSLATE_PACKAGE_ID, player.getUniqueId().toString());

        player.sendMessage(ChatColor.GREEN + JOINING_TRANS.translate(Main.TRANSLATE_PACKAGE_ID, uuid).replaceAll("\\{\\{0\\}\\}", nameString));
        String serverId = connectorServiceAPI.joinServer(uuid, filter);
        if (serverId == null) {
            player.sendMessage(ChatColor.RED + NO_SERVER_FOUND_TRANS.translate(Main.TRANSLATE_PACKAGE_ID, uuid));
            return null;
        }
        player.sendMessage(ChatColor.GREEN + JOINED_GAME_TRANS.translate(Main.TRANSLATE_PACKAGE_ID, uuid).replaceAll("\\{\\{0\\}\\}", nameString));
        return serverId;
    }

    private static final TranslatableString JOINING_TRANS = new TranslatableString("joining", "Joining {{0}}...");
    private static final TranslatableString JOINED_GAME_TRANS = new TranslatableString("joinedgame", "You've joined {{0}}, the system is sending you now!");
    private static final TranslatableString NO_SERVER_FOUND_TRANS = new TranslatableString("noserverfound", "No server was found, try again or contact an administrator.");
}
