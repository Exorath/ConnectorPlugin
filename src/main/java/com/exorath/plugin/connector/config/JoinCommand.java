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

import java.util.Collection;

/**
 * Created by toonsev on 1/15/2017.
 */
public class JoinCommand {
    private TranslatableString name;
    private Filter filter;
    private Collection<TranslatableString> cmds;

    public JoinCommand(TranslatableString name, Filter filter, Collection<TranslatableString> cmds) {
        this.name = name;
        this.filter = filter;
        this.cmds = cmds;
    }

    public TranslatableString getName() {
        return name;
    }

    public Filter getFilter() {
        return filter;
    }

    public Collection<TranslatableString> getCmds() {
        return cmds;
    }
}
