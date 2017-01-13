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

import com.exorath.plugin.connector.SimpleGameDescription;
import com.exorath.service.connector.res.Filter;
import com.exorath.service.translation.api.TranslatableString;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Lovely mapping without pojo -_- let's never do this again <3
 * Created by toonsev on 1/12/2017.
 */
public class YamlConfigProvider implements ConfigProvider {
    private FileConfiguration fileConfiguration;

    private HashMap<Integer, GameDescription> gameBySlot = new HashMap<>();

    public YamlConfigProvider(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }

    @Override
    public void reload() {
        gameBySlot.clear();
        ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection("games");
        if(configurationSection == null){
            Bukkit.getLogger().log(Level.WARNING, "Did not find games (connectorplugin)");
            return;
        }
        for (String key : configurationSection.getKeys(false))
            loadGameSection(configurationSection.getConfigurationSection(key));
    }

    private void loadGameSection(ConfigurationSection config) {
        if(config == null)
            return;

        SimpleGameDescription simpleGameDescription = new SimpleGameDescription();

        String name = config.getString("name", "Unknown name");
        simpleGameDescription.setName(new TranslatableString(name, name));

        String shortDescription = config.getString("description.short", null);
        if (shortDescription != null)
            simpleGameDescription.setShortDescription(new TranslatableString(name, name));

        List<String> longDescription = config.getStringList("description.long");
        if (longDescription != null)
            simpleGameDescription.setLongDescription(
                    longDescription.stream()
                            .map(line -> new TranslatableString(line, line))
                            .collect(Collectors.toList()));

        simpleGameDescription.setFilter(getFilter(config.getConfigurationSection("filter")));
        simpleGameDescription.setPlainItemStack(config.getItemStack("plainStack", DEFAULT_PLAIN_IS));
    }

    private Filter getFilter(ConfigurationSection filterSection){
        Filter filter = new Filter();
        if(filterSection == null)
            return filter;
        filter.withGameId(filterSection.getString("gameId"))
                .withMapId(filterSection.getString("mapId"))
                .withFlavorId(filterSection.getString("flavorId"));
        return filter;
    }
    @Override
    public HashMap<Integer, GameDescription> getGameBySlot() {
        return gameBySlot;
    }

    private static final ItemStack DEFAULT_PLAIN_IS = new ItemStack(Material.EMERALD);
}
