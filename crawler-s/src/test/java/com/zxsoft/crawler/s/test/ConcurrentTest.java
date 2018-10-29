package com.zxsoft.crawler.s.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentTest {

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        for (int i = 0; i < 20; i++) {
            Map<String, Object> _map = new HashMap<>(map);
            _map.put("key2", new MyBean("id" + i, "name" + i, i));
            threadPool.execute(new MyThread("str" + i, _map));
        }
    }
}

class MyThread implements Runnable {

    private String str;
    private Map<String, Object> map;

    public MyThread(String str, Map<String, Object> map) {
        this.str = str;
        this.map = map;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("str: " + str);
        map.forEach((k, v) -> System.out.println(k + ": " + v));
        System.out.println("--------------------------------------------");

    }
}

class MyBean {
    private String id;
    private String name;
    private int age;

    public MyBean(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    @Override
    public String toString() {
        return "id:" + id + " ,name:" + name + " ,age:" + age;
    }
}