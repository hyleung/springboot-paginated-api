package com.ca.portalapi.representations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

/**
 * Created by leuho02 on 2016-08-04.
 */
@Relation(collectionRelation = "items")
public class ItemRep extends ResourceSupport {
    @JsonProperty("id")
    private final String uuid;
    private final int id;
    private final String name;
    private final String description;

    public ItemRep(final int id, final String uuid, final String name, final String description) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
    }

    @JsonIgnore
    public int getRecordId() {
        return id;
    }

    public String getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
