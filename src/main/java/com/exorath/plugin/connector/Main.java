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
import com.exorath.plugin.connector.config.YamlConfigProvider;
import com.exorath.plugin.connector.inv.InventoryProvider;
import com.exorath.plugin.connector.inv.SimpleInventoryProvider;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by toonsev on 1/11/2017.
 */
public class Main extends JavaPlugin {
    public static final String TRANSLATE_PACKAGE_ID = "plugin.connector";

    private static InventoryRegistry inventoryRegistry;

    @Override
    public void onEnable() {
        ConfigProvider configProvider = new YamlConfigProvider(getConfig());
        InventoryProvider inventoryProvider = new SimpleInventoryProvider(configProvider);

        InventoryManager inventoryManager = new InventoryManager(configProvider, inventoryProvider);
        Bukkit.getPluginManager().registerEvents(inventoryManager, this);

        inventoryRegistry = new InventoryRegistry();//using a static inventory event registry for now.
        Bukkit.getPluginManager().registerEvents(inventoryRegistry, this);
    }


    public static InventoryRegistry getInventoryRegistry() {
        return inventoryRegistry;
    }
}
