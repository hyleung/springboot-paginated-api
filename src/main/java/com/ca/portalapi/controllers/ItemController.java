package com.ca.portalapi.controllers;

import com.ca.portalapi.dao.ItemDao;
import com.ca.portalapi.domain.Item;
import com.ca.portalapi.exceptions.ResourceNotFound;
import com.ca.portalapi.representations.CreateItemForm;
import com.ca.portalapi.representations.ItemRep;
import io.codearte.jfairy.Fairy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
        final Resources<ItemRep> result = new Resources<>(listJson());
        result.add(linkTo(methodOn(ItemController.class).readFormHal()).withRel("create-form"));
        return result;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemRep getJson(@PathVariable("id") final String id) {
        return dao
                .get(id)
                .map(item -> new ItemRep(item.getUuid(), item.getName(),item.getDescription()))
                .orElseThrow(ResourceNotFound::new);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    public ItemRep getHalJson(@PathVariable("id") final String id) {
        return getJson(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathParam("id") final String id) {
        int count = dao.delete(id);
        if (count == 0) {
            throw new ResourceNotFound();
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE})
    public ResponseEntity create(@RequestBody final CreateItemForm form) {
        String uuid = dao.create(form.getName(), form.getDescription());
        return ResponseEntity
                .created(URI.create("/items/" + uuid))
                .build();
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public CreateItemForm readForm() {
        Fairy fairy = Fairy.create();
        String name = String.format("%s-%s", fairy.person().lastName().toLowerCase(), fairy.person().firstName().toLowerCase());
        final CreateItemForm form = new CreateItemForm();
        form.setName(name);
        form.setDescription(String.format("Description of %s", name));
        return form;
    }
    @RequestMapping(value = "/form", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    public CreateItemForm readFormHal() {
        final CreateItemForm result = readForm();
        result.add(linkTo(methodOn(ItemController.class).create(result)).withRel("create"));
        return result;
    }
}
