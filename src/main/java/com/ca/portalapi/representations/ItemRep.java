package com.ca.portalapi.representations;

/**
 * Created by leuho02 on 2016-08-04.
 */
public class ItemRep {
    private final String id;
    private final String name;
    private final String description;

    public ItemRep(final String id, final String name, final String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
