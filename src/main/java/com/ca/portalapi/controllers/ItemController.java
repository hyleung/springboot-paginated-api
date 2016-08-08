package com.ca.portalapi.controllers;

import com.ca.portalapi.dao.ItemDao;
import com.ca.portalapi.exceptions.ResourceNotFound;
import com.ca.portalapi.representations.ItemRep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by leuho02 on 2016-08-04.
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/items")
public class ItemController {
    @Autowired
    private ItemDao dao;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemRep> listJson() {
        return dao
                .list()
                .stream()
                .map(item -> new ItemRep(item.getUuid(), item.getName(), item.getDescription()))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    public Resources<ItemRep> listHalJson() {
        return new Resources<>(listJson());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemRep getJson(@PathVariable("id") final String id) {
        return dao
                .get(id)
                .map(item -> new ItemRep(item.getUuid(), item.getName(),item.getDescription()))
                .orElseThrow(ResourceNotFound::new);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    public Resource<ItemRep> getHalJson(@PathVariable("id") final String id) {
        return new Resource<>(getJson(id));
    }
}
