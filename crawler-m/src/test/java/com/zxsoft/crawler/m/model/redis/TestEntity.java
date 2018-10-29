package com.zxsoft.crawler.m.model.redis;

import com.jfinal.plugin.activerecord.Record;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by cox on 2016/1/17.
 */
public class TestEntity implements Map {

    private Record te = new Record();

    @Override
    public int size() {
        return this.te.getColumnNames().length;
    }

    @Override
    public boolean isEmpty() {
        return this.te.getColumns().isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        for (String k : this.te.getColumnNames())
            if (k.equals(key))
                return true;
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for (String k : this.te.getColumnNames())
            if (this.te.get(k).equals(value))
                return true;
        return false;
    }

    @Override
    public Object get(Object key) {
        return this.te.get(key.toString());
    }

    @Override
    public Object put(Object key, Object value) {
        return this.te.set(key.toString(), value);
    }

    @Override
    public Object remove(Object key) {
        return this.te.remove(key.toString());
    }

    @Override
    public void putAll(Map m) {
        for (Object s : m.keySet()) {
            this.te.set(s.toString(), m.get(s));
        }
    }

    @Override
    public void clear() {
        this.te.clear();
    }

    @Override
    public Set keySet() {
        Set set = new HashSet();
        for (String key : this.te.getColumnNames())
            set.add(key);
        return set;
    }

    @Override
    public Collection values() {
        return null;
    }

    @Override
    public Set<Entry> entrySet() {
        return null;
    }

    public String toString() {
        return this.te.toString();
    }
}
