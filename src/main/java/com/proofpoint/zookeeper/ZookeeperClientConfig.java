/*
 * Copyright 2010 Proofpoint, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.proofpoint.zookeeper;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

public class ZookeeperClientConfig
{
  private String connectionString;
  private int sessionTimeoutInMs = 60000;
  private int connectionTimeoutInMs = 10000;
  private int maxConnectionLossRetries = 2;
  private int connectionLossSleepInMs = 1000;
  private String sessionStorePath;

  @Null
  public String getSessionStorePath()
  {
    return sessionStorePath;
  }

  public ZookeeperClientConfig setSessionStorePath(String sessionStorePath)
  {
    this.sessionStorePath = sessionStorePath;
    return this;
  }

  @NotNull
  public String getConnectionString()
  {
    return connectionString;
  }

  public ZookeeperClientConfig setConnectionString(String connectionString)
  {
    this.connectionString = connectionString;
    return this;
  }

  public int getSessionTimeoutInMs()
  {
    return sessionTimeoutInMs;
  }

  public ZookeeperClientConfig setSessionTimeoutInMs(int sessionTimeoutInMs)
  {
    this.sessionTimeoutInMs = sessionTimeoutInMs;
    return this;
  }

  public int getConnectionTimeoutInMs()
  {
    return connectionTimeoutInMs;
  }

  public ZookeeperClientConfig setConnectionTimeoutInMs(int connectionTimeoutInMs)
  {
    this.connectionTimeoutInMs = connectionTimeoutInMs;
    return this;
  }

  public int getMaxConnectionLossRetries()
  {
    return maxConnectionLossRetries;
  }

  public ZookeeperClientConfig setMaxConnectionLossRetries(int maxConnectionLossRetries)
  {
    this.maxConnectionLossRetries = maxConnectionLossRetries;
    return this;
  }

  public int getConnectionLossSleepInMs()
  {
    return connectionLossSleepInMs;
  }

  public ZookeeperClientConfig setConnectionLossSleepInMs(int connectionLossSleepInMs)
  {
    this.connectionLossSleepInMs = connectionLossSleepInMs;
    return this;
  }
}
