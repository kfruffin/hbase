/**
 * Copyright 2010 The Apache Software Foundation
 *
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
package org.apache.hadoop.hbase.zookeeper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.Abortable;
import org.apache.hadoop.hbase.HServerAddress;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.zookeeper.KeeperException;

public class RootRegionTracker extends ZooKeeperNodeTracker {
  private static final Log LOG = LogFactory.getLog(RootRegionTracker.class);

  /**
   * Creates a root region location tracker.
   *
   * <p>After construction, use {@link #start} to kick off tracking.
   *
   * @param watcher
   * @param abortable
   */
  public RootRegionTracker(ZooKeeperWatcher watcher, Abortable abortable) {
    super(watcher, watcher.rootServerZNode, abortable);
  }

  /**
   * Checks if the root region location is available.
   * @return true if root region location is available, false if not
   */
  public boolean isLocationAvailable() {
    return super.getData() != null;
  }

  /**
   * Gets the root region location, if available.  Null if not.
   * @return server address for server hosting root region, null if none
   *         available
   */
  public HServerAddress getRootRegionLocation() {
    byte [] data = super.getData();
    return data == null ? null : new HServerAddress(Bytes.toString(data));
  }

  /**
   * Sets the root region location.
   * @param address
   * @throws KeeperException unexpected zk exception
   */
  public void setRootRegionLocation(HServerAddress address)
  throws KeeperException {
    try {
      ZKUtil.createAndWatch(watcher, watcher.rootServerZNode,
        Bytes.toBytes(address.toString()));
    } catch(KeeperException.NodeExistsException nee) {
      ZKUtil.setData(watcher, watcher.rootServerZNode,
          Bytes.toBytes(address.toString()));
    }
  }

  @Override
  protected Log getLog() {
    return LOG;
  }
}