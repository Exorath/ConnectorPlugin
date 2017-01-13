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

import com.exorath.plugin.connector.config.GameDescription;
import com.exorath.service.connector.res.Filter;
import com.exorath.service.translation.api.TranslatableString;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by toonsev on 1/13/2017.
 */
public class SimpleGameDescription implements GameDescription {
    private TranslatableString name;
    private TranslatableString shortDescription;
    private List<TranslatableString> longDescription;
    private Filter filter;
    private ItemStack plainItemStack;

    public SimpleGameDescription() {}

    public SimpleGameDescription(TranslatableString name, TranslatableString shortDescription, List<TranslatableString> longDescription, Filter filter, ItemStack plainItemStack) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.filter = filter;
        this.plainItemStack = plainItemStack;
    }

    public void setName(TranslatableString name) {
        this.name = name;
    }

    public void setShortDescription(TranslatableString shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setLongDescription(List<TranslatableString> longDescription) {
        this.longDescription = longDescription;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public void setPlainItemStack(ItemStack plainItemStack) {
        this.plainItemStack = plainItemStack;
    }

    @Override
    public TranslatableString getName() {
        return name;
    }

    @Override
    public TranslatableString getShortDescription() {
        return shortDescription;
    }

    @Override
    public List<TranslatableString> getLongDescription() {
        return longDescription;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public ItemStack getPlainItemStack() {
        return plainItemStack;
    }
}
