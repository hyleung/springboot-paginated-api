package com.ca.portalapi.controllers;

import com.ca.portalapi.controllers.pagination.NextPageStrategy;
import com.ca.portalapi.controllers.pagination.PagedResult;
import com.ca.portalapi.controllers.pagination.PaginationStrategy;
import com.ca.portalapi.controllers.pagination.PreviousPageStrategy;
import com.ca.portalapi.dao.ItemDao;
import com.ca.portalapi.domain.Item;
import com.ca.portalapi.representations.CreateItemForm;
import com.ca.portalapi.representations.ItemRep;
import io.codearte.jfairy.Fairy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.hateoas.*;
import org.springframework.hateoas.core.ControllerEntityLinks;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by leuho02 on 2016-08-08.
 */
@RestController
@EnableAutoConfiguration
@RequestMapping("/items")
@CrossOrigin(origins = "*")
public class ItemListController {
    private static final Logger log = LoggerFactory.getLogger(ItemListController.class);
    public static final String PREVIOUS_ACTION = "previous";

    @Autowired
    EntityLinks entityLinks;
    @Autowired
    private ItemDao dao;
    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemRep> listJson() {
        return dao
                .list()
                .stream()
                .map(item -> new ItemRep(item.getId(), item.getUuid(), item.getName(), item.getDescription()))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    public Resources<ItemRep> listHalJson(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                          @RequestParam(value = "lastSeen", required = false) Integer lastSeen,
                                          @RequestParam(value = "action", required = false) String action) throws MalformedURLException, UnsupportedEncodingException {
        Resources<ItemRep> result;
        List<ItemRep> items;
        if (pageSize == null) {
            items = dao.list()
                    .stream()
                    .map(item -> new ItemRep(item.getId(), item.getUuid(), item.getName(), item.getDescription()))
                    .collect(Collectors.toList());
            result = new Resources<>(items);
            result.add(linkTo(methodOn(ItemListController.class).listHalJson(null, null, null)).withSelfRel());
        } else {
            PaginationStrategy paginationStrategy =
                    PREVIOUS_ACTION.equals(action) ? new PreviousPageStrategy() : new NextPageStrategy();
            final PagedResult<Item> paginatedResult = paginationStrategy.getPaginatedResult(dao, pageSize, lastSeen);
            items = paginatedResult.getResult()
                    .stream()
                    .map(item -> new ItemRep(item.getId(), item.getUuid(), item.getName(), item.getDescription()))
                    .collect(Collectors.toList());
            items.forEach(rep -> rep.add(
                    entityLinks.linkFor(ItemRep.class, rep.getUUID()).withSelfRel(),
                    entityLinks.linkFor(ItemRep.class, rep.getUUID()).withRel("delete")));
            result = new Resources<>(items);

            paginatedResult
                    .getLinks().forEach(pagedResultLink -> {
                try {
                    result.add(linkTo(methodOn(ItemListController.class).listHalJson(pagedResultLink.pageSize,
                            pagedResultLink.lastSeen,
                            pagedResultLink.action)).withRel(pagedResultLink.rel));
                } catch (MalformedURLException | UnsupportedEncodingException e) {
                    log.error("Error while creating links", e);
                }
            });
        }

        result.add(linkTo(methodOn(ItemListController.class).readFormHal()).withRel("create-form"));

        return result;
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
        result.add(linkTo(methodOn(ItemListController.class).create(result)).withRel("create"));
        return result;
    }
}
