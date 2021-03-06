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

import com.exorath.clickents.ClickEntAPI;
import com.exorath.plugin.connector.config.ConfigProvider;
import com.exorath.plugin.connector.config.YamlConfigProvider;
import com.exorath.plugin.connector.inv.InventoryProvider;
import com.exorath.plugin.connector.inv.SimpleInventoryProvider;
import com.exorath.plugin.connector.world.WorldLoadHandler;
import com.exorath.service.connector.api.ConnectorServiceAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by toonsev on 1/11/2017.
 */
public class Main extends JavaPlugin {
    public static final String TRANSLATE_PACKAGE_ID = "plugin.connector";

    private static Main instance;
    private static InventoryRegistry inventoryRegistry;
    private ConnectorServiceAPI connectorServiceAPI;

    @Override
    public void onEnable() {
        instance = this;
        String address = getConnectorServiceAddress();
        if(address == null) {
            System.out.println("ConnectorPlugin no CONNECTOR_SERVICE_ADDRESS env var defined.");
            return;
        }
        this.connectorServiceAPI = new ConnectorServiceAPI(address);
        PlayerJoiner playerJoiner = new PlayerJoiner(connectorServiceAPI);

        //Loads in the ymlconfig as a config provider
        ConfigProvider configProvider = new YamlConfigProvider(getConfig());
        //creates an inventoryprovider with the config, this is responsible for creating inventories
        InventoryProvider inventoryProvider = new SimpleInventoryProvider(configProvider, playerJoiner);
        //create an inventorymanager with the config, this is responsible for calling the inventoryprovider
        InventoryManager inventoryManager = new InventoryManager(configProvider, inventoryProvider);
        Bukkit.getPluginManager().registerEvents(inventoryManager, this);
        //The commandManager is responsible for the /hub commands etc.
        CommandManager commandManager = new CommandManager(configProvider, playerJoiner);
        Bukkit.getPluginManager().registerEvents(commandManager, this);
        //Set up a static inventory registry for event propagation
        inventoryRegistry = new InventoryRegistry();
        Bukkit.getPluginManager().registerEvents(inventoryRegistry, this);
        ClickEntAPI clickEntAPI = new ClickEntAPI(this);

        Bukkit.getPluginManager().registerEvents(new WorldLoadHandler(playerJoiner, clickEntAPI), this);
    }
    private String getConnectorServiceAddress(){
       return System.getenv("CONNECTOR_SERVICE_ADDRESS");
    }
    public static InventoryRegistry getInventoryRegistry() {
        return inventoryRegistry;
    }

    public static Main getInstance() {
        return instance;
    }
}
