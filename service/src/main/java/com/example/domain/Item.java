package com.example.domain;

/**
 * Created by hyleung on 2016-08-04.
 */
public class Item {
    private final int id;
    private final String uuid;
    private final String name;
    private final String description;

    public Item(final int id, final String uuid, final String name, final String description) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
