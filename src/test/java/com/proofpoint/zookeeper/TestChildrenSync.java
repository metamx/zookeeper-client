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

import com.google.common.util.concurrent.MoreExecutors;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class TestChildrenSync
{
    private ZookeeperClient client;
    private ZookeeperTestServerInstance server;

    @BeforeMethod
    public void setup()
            throws Exception
    {
        server = new ZookeeperTestServerInstance();
        ZookeeperClientConfig config = new ZookeeperClientConfig().setConnectionString(server.getConnectString());
        client = new ZookeeperClient(config);
    }

    @AfterMethod
    public void teardown()
            throws InterruptedException
    {
        client.closeForShutdown();
        server.close();
    }

    @Test
    public void test()
            throws Exception
    {
        client.mkdirs("/test");

        final BlockingQueue<String> event = new ArrayBlockingQueue<String>(1);
        ChildDataWatcher sync = new ChildDataWatcher(client, "/test", new ChildDataListener()
        {
            @Override
            public void added(String child, byte[] data)
            {
                event.offer("add/" + child + "/" + new String(data));
            }

            @Override
            public void updated(String child, byte[] data, int version)
            {
                event.offer("update/" + child + "/" + new String(data));
            }

            @Override
            public void removed(String child)
            {
                event.offer("remove/" + child);
            }
        }, MoreExecutors.sameThreadExecutor());
        sync.start();

        {
            client.create("/test/child", "hey".getBytes());
            String value = event.poll(1, TimeUnit.MINUTES);
            Assert.assertEquals("add/child/hey", value);
        }

        {
            client.setData("/test/child", "yo".getBytes());
            String value = event.poll(1, TimeUnit.MINUTES);
            Assert.assertEquals("update/child/yo", value);
        }

        {
            client.delete("/test/child");
            String value = event.poll(1, TimeUnit.MINUTES);
            Assert.assertEquals("remove/child", value);
        }

        sync.stop();
    }
}
