package com.digitalbuddha.daodemo.base;

import android.support.annotation.NonNull;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.digitalbuddha.daodemo.util.Id;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import rx.Observable;

public class RxCache<Parsed> {//RxCache cache
    //in memory cache of data
    final Cache<Id<Parsed>, Observable<Parsed>> memory;

    private RxCache() {
        memory = CacheBuilder.newBuilder()
                .maximumSize(getCacheSize())
                .expireAfterAccess(getCacheTTL(), TimeUnit.MILLISECONDS)
                .build();
    }

    public static <Parsed> RxCache<Parsed> create() {
        return new RxCache<>();
    }

    /**
     * @return data from get
     */
    protected Observable<Parsed> get(@NonNull final Id<Parsed> id, Observable<Parsed> network) {
        try {
            return memory.get(id, () -> network);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return Observable.empty();
    }

    protected void update(@NonNull final Id<Parsed> id, final Parsed data) {
        memory.put(id, Observable.just(data));
    }

    protected void clearMemory() {
        memory.invalidateAll();
    }

    /**
     * Clear get by id
     *
     * @param id of data to clear
     */
    protected void clearMemory(@NonNull final Id<Parsed> id) {
        memory.invalidate(id);
    }

    /**
     * Default Cache TTL, can be overridden
     *
     * @return get cache ttl
     */
    protected long getCacheTTL() {
        return TimeUnit.HOURS.toMillis(24);
    }

    /**
     * Default mem cache is 1, can be overridden otherwise
     *
     * @return get cache size
     */
    protected int getCacheSize() {
        return 1;
    }
}