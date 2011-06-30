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
package com.proofpoint.zookeeper.crossprocess;

import com.proofpoint.zookeeper.log.Logger;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Using Zookeeper, manages a group of servers whereby only one at a time can be the leader
 */
public class LeadershipNominatorImp implements LeadershipNominator
{
    private static final Logger             log = Logger.get(LeadershipNominatorImp.class);

    private final CrossProcessLock          lock;
    private final ExecutorService           executor;
    private final AtomicReference<LeadershipNominatorNotifier> notifier;
    private final String                    groupName;

    /**
     *
     * @param lockFactory zookeeper instance
     * @param groupName name of the leadership group. There will be one leader per group
     * @throws Exception errors
     */
    public LeadershipNominatorImp(CrossProcessLockFactory lockFactory, String groupName) throws Exception
    {
        this.notifier = new AtomicReference<LeadershipNominatorNotifier>(null);
        this.groupName = groupName;
        
        lock = lockFactory.newLock(groupName);

        ThreadFactory factory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("LeadershipNominator")
                // TODO: .setUncaughtExceptionHandler()
                .build();

        executor = Executors.newSingleThreadExecutor(factory);
    }

    @Override
    public void     setNotifier(LeadershipNominatorNotifier n)
    {
        notifier.set(n);
    }

    @Override
    public LeadershipNominatorNotifier getNotifier()
    {
        return notifier.get();
    }

    @Override
    public void        start()
    {
        assert getNotifier() != null;

        executor.submit
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    attemptLeadership();
                }
            }
        );
    }

    @Override
    public void        stop()
    {
        log.debug("Stopping group: %s...", groupName);

        executor.shutdownNow();
        try
        {
            executor.awaitTermination(1, TimeUnit.MINUTES); // TODO how long to wait? Make configurable?
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
        }

        log.debug("...Group: %s - stopped", groupName);
    }

    @Override
    public boolean     hasLeadership()
    {
        try
        {
            return lock.isLocked();
        }
        catch ( Exception e )
        {
            throw new Error(e);
        }
    }

    private void attemptLeadership()
    {
        while ( !Thread.interrupted() )
        {
            try
            {
                log.debug("Group: %s - waiting for leadership", groupName);

                lock.lock();

                log.debug("Group: %s - taking leadership", groupName);

                notifier.get().takeLeadership();
            }
            catch ( InterruptedException e )
            {
                Thread.currentThread().interrupt();
                break;
            }
            catch ( Exception e )
            {
                log.error(e, "Some exception thingie.");
            }
            finally
            {
                try
                {
                    lock.unlock();

                    log.debug("Group: %s - relinquishing leadership", groupName);
                }
                catch ( Exception e )
                {
                    // ignore
                }

                notifier.get().lostLeadership();
            }
        }
    }
}
