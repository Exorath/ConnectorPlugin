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
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by toonsev on 1/11/2017.
 */
public interface GameDescription {
    /**
     * Gets the name of this game, may contain Minecraft formatting (reset may reset to the original line color)
     * @return
     */
    TranslatableString getName();

    /**
     * Gets a one line description (less then 5 words) of this game (reset may reset to the original line color)
     * @return
     */
    TranslatableString getShortDescription();

    /**
     * Gets a longer description list (reset may reset to the original line color)
     * @return
     */
    List<TranslatableString> getLongDescription();

    /**
     * Gets the filter of this game type (the specification of the server type)
     * @return
     */
    Filter getFilter();

    /**
     * Gets an itemstack without name or lore.
     * @return
     */
    ItemStack getPlainItemStack();



}
