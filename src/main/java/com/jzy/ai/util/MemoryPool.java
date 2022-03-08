package com.jzy.ai.util;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 缓存池
 */
public class MemoryPool<T extends IMemoryObject> implements Serializable {

    private static final long serialVersionUID = 943760723073862247L;

    private final LinkedBlockingQueue<T> cache;


    public MemoryPool(int max) {
        cache = new LinkedBlockingQueue<>(max);
    }

    public void put(T value) {
        value.release();
        this.cache.add(value);
    }

    public T get(Class<? extends T> c) {
        try {
            T t = this.cache.poll();
            if (t == null) {
                t = c.newInstance();
            }
            return t;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
