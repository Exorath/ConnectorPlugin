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

import com.exorath.service.translation.api.TranslatableString;
import org.bukkit.inventory.ItemStack;

/**
 * Created by toonsev on 1/14/2017.
 */
public class GamesItem {
    private int slot;
    private TranslatableString name;
    private ItemStack plainItem;

    public GamesItem(int slot, ItemStack plainItem) {
        this.slot = slot;
        this.plainItem = plainItem;
    }

    public int getSlot() {
        return slot;
    }


    public void setSlot(int slot) {
        this.slot = slot;
    }

    public TranslatableString getName() {
        return name;
    }

    public void setName(TranslatableString name) {
        this.name = name;
    }

    public ItemStack getPlainItem() {
        return plainItem;
    }

    public void setPlainItem(ItemStack plainItem) {
        this.plainItem = plainItem;
    }
}
