package com.example.controllers;

import com.example.dao.ItemDao;
import com.example.exceptions.ResourceNotFound;
import com.example.representations.ItemRep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by hyleung on 2016-08-04.
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/items")
@ExposesResourceFor(ItemRep.class)
@CrossOrigin("*")
public class ItemController {
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    @Autowired
    private ItemDao dao;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemRep getJson(@PathVariable("id") final String id) {
        return dao
                .get(id)
                .map(item -> new ItemRep(item.getId(), item.getUuid(), item.getName(),item.getDescription()))
                .orElseThrow(ResourceNotFound::new);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    public ItemRep getHalJson(@PathVariable("id") final String id) {
        final ItemRep result = getJson(id);
        result.add(
                linkTo(methodOn(ItemController.class).getHalJson(id)).withSelfRel(),
                linkTo(methodOn(ItemController.class).delete(id)).withRel("delete"));
        return result;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable("id") final String id) {
        int count = dao.delete(id);
        if (count == 0) {
            throw new ResourceNotFound();
        }
        return ResponseEntity.noContent().build();
    }
}
