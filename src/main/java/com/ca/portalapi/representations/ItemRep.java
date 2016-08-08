package com.ca.portalapi.representations;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by leuho02 on 2016-08-04.
 */
public class ItemRep extends ResourceSupport {
    @JsonProperty("id")
    private final String uuid;
    private final String name;
    private final String description;

    public ItemRep(final String uuid, final String name, final String description) {
        this.uuid = uuid;
        this.name = name;
        this.description = description;
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
