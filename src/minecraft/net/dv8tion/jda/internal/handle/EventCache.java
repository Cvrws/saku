/*
 * Copyright 2015 Austin Keener, Michael Ritter, Florian Spieß, and the JDA contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dv8tion.jda.internal.handle;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.utils.CacheConsumer;
import net.dv8tion.jda.internal.utils.JDALogger;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EventCache
{
    /** Sequence difference after which events will be removed from cache */
    public static final long TIMEOUT_AMOUNT = 100;
    private final EnumMap<Type, TLongObjectMap<List<CacheNode>>> eventCache = new EnumMap<>(Type.class);

    public EventCache() {}

    public synchronized void timeout(final long responseTotal)
    {
        if (eventCache.isEmpty())
            return;
        AtomicInteger count = new AtomicInteger();
        eventCache.forEach((type, map) ->
        {
            if (map.isEmpty())
                return;
            TLongObjectIterator<List<CacheNode>> iterator = map.iterator();
            while (iterator.hasNext())
            {
                iterator.advance();
                long triggerId = iterator.key();
                List<CacheNode> cache = iterator.value();
                //Remove when this node is more than 100 events ago
                cache.removeIf(node ->
                {
                    boolean remove = responseTotal - node.responseTotal > TIMEOUT_AMOUNT;
                    if (remove)
                    {
                        count.incrementAndGet();
                    }
                    return remove;
                });
                if (cache.isEmpty())
                    iterator.remove();
            }
        });
        int amount = count.get();
    }

    public synchronized void cache(Type type, long triggerId, long responseTotal, DataObject event, CacheConsumer handler)
    {
        TLongObjectMap<List<CacheNode>> triggerCache =
                eventCache.computeIfAbsent(type, k -> new TLongObjectHashMap<>());

        List<CacheNode> items = triggerCache.get(triggerId);
        if (items == null)
        {
            items = new LinkedList<>();
            triggerCache.put(triggerId, items);
        }

        items.add(new CacheNode(responseTotal, event, handler));
    }

    public synchronized void playbackCache(Type type, long triggerId)
    {
        TLongObjectMap<List<CacheNode>> typeCache = this.eventCache.get(type);
        if (typeCache == null)
            return;

        List<CacheNode> items = typeCache.remove(triggerId);
        if (items != null && !items.isEmpty())
        {

            for (CacheNode item : items)
                item.execute();
        }
    }

    public synchronized int size()
    {
        return (int) eventCache.values().stream()
                .mapToLong(typeMap ->
                    typeMap.valueCollection().stream().mapToLong(List::size).sum())
                .sum();
    }

    public synchronized void clear()
    {
        eventCache.clear();
    }

    public synchronized void clear(Type type, long id)
    {
        TLongObjectMap<List<CacheNode>> typeCache = this.eventCache.get(type);
        if (typeCache == null)
            return;

    }

    public enum Type
    {
        USER, MEMBER, GUILD, CHANNEL, ROLE, RELATIONSHIP, CALL, SCHEDULED_EVENT
    }

    private class CacheNode
    {
        private final long responseTotal;
        private final DataObject event;
        private final CacheConsumer callback;

        public CacheNode(long responseTotal, DataObject event, CacheConsumer callback)
        {
            this.responseTotal = responseTotal;
            this.event = event;
            this.callback = callback;
        }

        void execute()
        {
            callback.execute(responseTotal, event);
        }
    }
}
