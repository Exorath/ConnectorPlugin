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

package com.exorath.plugin.connector.config;

import com.exorath.service.connector.res.Filter;
import com.exorath.service.translation.api.TranslatableString;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Lovely mapping without pojo -_- let's never do this again <3
 * Created by toonsev on 1/12/2017.
 */
public class YamlConfigProvider implements ConfigProvider {
    private FileConfiguration fileConfiguration;
    private GamesItem gamesItem;

    private HashMap<Integer, GameDescription> gamesBySlot = new HashMap<>();

    private Collection<JoinCommand> joinCommands = new HashSet<>();

    public YamlConfigProvider(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
        reload();
    }

    @Override
    public void reload() {
        loadInvSection(fileConfiguration.getConfigurationSection("inv"));
        loadCommandsSection(fileConfiguration.getConfigurationSection("joincommands"));
        //cmd /hub
        //->  hub:
        //      filter:
        //        gameId: hub
        //      cmds: (translated)
        //      - hub
        //      - lobby
        //      - back
    }

    private void loadCommandsSection(ConfigurationSection commandsSection){
        joinCommands.clear();
        if(commandsSection == null)
            return;
        for(String key : commandsSection.getKeys(false)){
            ConfigurationSection commandSection = commandsSection.getConfigurationSection(key);
            if(!commandSection.isList("cmds"))
                continue;
            TranslatableString name = convert(commandSection.getString("name", "Unknown"));
            Set<TranslatableString> cmds = commandSection.getStringList("cmds").stream().map(cmd -> convert(cmd)).collect(Collectors.toSet());
            Filter filter = getFilter(commandSection.getConfigurationSection("filter"));
            joinCommands.add(new JoinCommand(name, filter, cmds));
        }

    }

    private void loadInvSection(ConfigurationSection invSection) {
        gamesBySlot.clear();
        gamesItem = null;
        if (invSection == null)
            return;
        loadGamesItem(invSection.getConfigurationSection("gamesitem"));
        ConfigurationSection gamesSection = invSection.getConfigurationSection("games");

        if (invSection == null) {
            Bukkit.getLogger().log(Level.WARNING, "Did not find games (connectorplugin)");
            return;
        }
        for (String key : gamesSection.getKeys(false))
            loadGameSection(gamesSection.getConfigurationSection(key));
    }

    @Override
    public GamesItem getGamesItem() {
        return gamesItem;
    }

    @Override
    public Collection<JoinCommand> getJoinCommands() {
        return joinCommands;
    }

    @Override
    public HashMap<Integer, GameDescription> getGamesBySlot() {
        return gamesBySlot;
    }


    private void loadGamesItem(ConfigurationSection config) {
        if (config == null)
            return;
        if (!config.isInt("slot"))
            return;
        this.gamesItem = new GamesItem(config.getInt("slot"), config.getItemStack("plainitem"));
    }

    private void loadGameSection(ConfigurationSection config) {
        if (config == null)
            return;
        if (!config.isInt("slot"))
            return;

        SimpleGameDescription simpleGameDescription = new SimpleGameDescription();

        String name = config.getString("name", null);
        simpleGameDescription.setName(convert(name));


        String shortDescription = config.getString("description.short", null);
        if (shortDescription != null)
            simpleGameDescription.setShortDescription(convert(shortDescription));

        List<String> longDescription = config.getStringList("description.long");
        if (longDescription != null)
            simpleGameDescription.setLongDescription(
                    longDescription.stream()
                            .map(line -> convert(line))
                            .collect(Collectors.toList()));

        simpleGameDescription.setFilter(getFilter(config.getConfigurationSection("filter")));
        simpleGameDescription.setPlainItemStack(config.getItemStack("plainStack", null));

        gamesBySlot.put(config.getInt("slot"), simpleGameDescription);
    }

    private Filter getFilter(ConfigurationSection filterSection) {
        Filter filter = new Filter();
        if (filterSection == null)
            return filter;
        filter.withGameId(filterSection.getString("gameId"))
                .withMapId(filterSection.getString("mapId"))
                .withFlavorId(filterSection.getString("flavorId"));
        return filter;
    }

    private TranslatableString convert(String text){
        return new TranslatableString(text, text);
    }

}
