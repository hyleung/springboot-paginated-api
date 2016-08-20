package com.example.representations;

import org.springframework.hateoas.ResourceSupport;

/**
 * Created by hyleung on 2016-08-04.
 */
public class CreateItemForm extends ResourceSupport {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
