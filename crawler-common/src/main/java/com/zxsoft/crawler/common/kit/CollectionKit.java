package com.zxsoft.crawler.common.kit;

import java.util.Collection;
import java.util.Map;

/**
 * Created by cox on 2015/11/9.
 */
public class CollectionKit {
    public CollectionKit() {
    }

    public static <E> Boolean isEmpty(Collection<E> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <K, V> Boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <E> Boolean notEmpty(Collection<E> collection) {
        return !CollectionKit.isEmpty(collection);
    }

    public static <K, V> Boolean notEmpty(Map<K, V> map) {
        return !CollectionKit.isEmpty(map);
    }
}
