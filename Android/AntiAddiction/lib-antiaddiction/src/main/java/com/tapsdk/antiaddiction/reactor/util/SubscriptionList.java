/**
 * Copyright 2014 Netflix, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tapsdk.antiaddiction.reactor.util;

import com.tapsdk.antiaddiction.reactor.Subscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public final class SubscriptionList implements Subscription {

    private List<Subscription> subscriptions;
    private volatile boolean unsubscribed;

    /**
     * Constructs an empty SubscriptionList.
     */
    public SubscriptionList() {
        // nothing to do
    }

    /**
     * Constructs a SubscriptionList with the given initial child subscriptions.
     * @param subscriptions the array of subscriptions to start with
     */
    public SubscriptionList(final Subscription... subscriptions) {
        this.subscriptions = new LinkedList<Subscription>(Arrays.asList(subscriptions));
    }

    /**
     * Constructs a SubscriptionList with the given initial child subscription.
     * @param s the initial subscription instance
     */
    public SubscriptionList(Subscription s) {
        this.subscriptions = new LinkedList<Subscription>();
        this.subscriptions.add(s);
    }

    @Override
    public boolean isUnsubscribed() {
        return unsubscribed;
    }

    /**
     * Adds a new {@link Subscription} to this {@code SubscriptionList} if the {@code SubscriptionList} is
     * not yet unsubscribed. If the {@code SubscriptionList} <em>is</em> unsubscribed, {@code add} will
     * indicate this by explicitly unsubscribing the new {@code Subscription} as well.
     *
     * @param s
     *          the {@link Subscription} to add
     */
    public void add(final Subscription s) {
        if (s.isUnsubscribed()) {
            return;
        }
        if (!unsubscribed) {
            synchronized (this) {
                if (!unsubscribed) {
                    List<Subscription> subs = subscriptions;
                    if (subs == null) {
                        subs = new LinkedList<Subscription>();
                        subscriptions = subs;
                    }
                    subs.add(s);
                    return;
                }
            }
        }
        s.unsubscribe();
    }

    public void remove(final Subscription s) {
        if (!unsubscribed) {
            boolean unsubscribe;
            synchronized (this) {
                List<Subscription> subs = subscriptions;
                if (unsubscribed || subs == null) {
                    return;
                }
                unsubscribe = subs.remove(s);
            }
            if (unsubscribe) {
                // if we removed successfully we then need to call unsubscribe on it (outside of the lock)
                s.unsubscribe();
            }
        }
    }

    /**
     * Unsubscribe from all of the subscriptions in the list, which stops the receipt of notifications on
     * the associated {@code Subscriber}.
     */
    @Override
    public void unsubscribe() {
        if (!unsubscribed) {
            List<Subscription> list;
            synchronized (this) {
                if (unsubscribed) {
                    return;
                }
                unsubscribed = true;
                list = subscriptions;
                subscriptions = null;
            }
            // we will only get here once
            unsubscribeFromAll(list);
        }
    }

    private static void unsubscribeFromAll(Collection<Subscription> subscriptions) {
        if (subscriptions == null) {
            return;
        }
        List<Throwable> es = null;
        for (Subscription s : subscriptions) {
            try {
                s.unsubscribe();
            } catch (Throwable e) {
                if (es == null) {
                    es = new ArrayList<Throwable>();
                }
                es.add(e);
            }
        }
    }

}
