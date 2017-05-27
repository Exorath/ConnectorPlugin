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

import com.google.gson.JsonObject;

/**
 * Created by toonsev on 5/27/2017.
 */
public class ArmorStandArmor {
    private JsonObject helmet;
    private JsonObject chestPlate;
    private JsonObject leggings;
    private JsonObject boots;
    private JsonObject hand;

    public ArmorStandArmor() {
    }

    public JsonObject getHelmet() {
        return helmet;
    }

    public JsonObject getChestPlate() {
        return chestPlate;
    }

    public JsonObject getLeggings() {
        return leggings;
    }

    public JsonObject getBoots() {
        return boots;
    }

    public JsonObject getHand() {
        return hand;
    }
}
