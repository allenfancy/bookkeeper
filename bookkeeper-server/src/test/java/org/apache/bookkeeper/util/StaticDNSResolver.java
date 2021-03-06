/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.bookkeeper.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.bookkeeper.net.AbstractDNSToSwitchMapping;
import org.apache.bookkeeper.net.DNSToSwitchMapping;
import org.apache.bookkeeper.net.NetworkTopology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements {@link DNSToSwitchMapping} via static mappings. Used in test cases to simulate racks.
 */
public class StaticDNSResolver extends AbstractDNSToSwitchMapping {

    static final Logger LOG = LoggerFactory.getLogger(StaticDNSResolver.class);

    private static final ConcurrentMap<String, String> name2Racks = new ConcurrentHashMap<String, String>();

    public static void addNodeToRack(String name, String rack) {
        name2Racks.put(name, rack);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Add node {} to rack {}.", name, rack);
        }
    }

    public static String getRack(String name) {
        String rack = name2Racks.get(name);
        if (null == rack) {
            rack = NetworkTopology.DEFAULT_RACK;
        }
        return rack;
    }

    public static void reset() {
        name2Racks.clear();
    }

    @Override
    public List<String> resolve(List<String> names) {
        List<String> racks = new ArrayList<String>();
        for (String n : names) {
            String rack = name2Racks.get(n);
            if (null == rack) {
                rack = NetworkTopology.DEFAULT_RACK;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Resolve name {} to rack {}.", n, rack);
            }
            racks.add(rack);
        }
        return racks;
    }

    @Override
    public void reloadCachedMappings() {
        // nop
    }

}
