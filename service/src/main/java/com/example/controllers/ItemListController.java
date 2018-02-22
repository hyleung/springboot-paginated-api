package com.example.controllers;

import com.example.controllers.pagination.NextPageStrategy;
import com.example.controllers.pagination.PagedResult;
import com.example.controllers.pagination.PaginationStrategy;
import com.example.controllers.pagination.PreviousPageStrategy;
import com.example.dao.ItemDao;
import com.example.domain.Item;
import com.example.representations.CreateItemForm;
import com.example.representations.ItemRep;
import io.codearte.jfairy.Fairy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.hateoas.*;
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
 * Created by hyleung on 2016-08-08.
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

    @RequestMapping(value = "", method = RequestMethod.GET, produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Resources<ItemRep> get(@RequestParam(value = "pageSize", required = false, defaultValue = "5") Integer pageSize,
                                          @RequestParam(value = "lastSeen", required = false) Integer lastSeen,
                                          @RequestParam(value = "action", required = false) String action) throws MalformedURLException, UnsupportedEncodingException {
        Resources<ItemRep> result;
        List<ItemRep> items;
        PaginationStrategy paginationStrategy =
            PREVIOUS_ACTION.equals(action) ? new PreviousPageStrategy() : new NextPageStrategy();
        final PagedResult<Item> paginatedResult = paginationStrategy.getPaginatedResult(dao, pageSize, lastSeen);
        items = paginatedResult.getResult()
                               .stream()
                               .map(item -> new ItemRep(item.getId(), item.getUuid(), item.getName(), item.getDescription()))
                               .collect(Collectors.toList());
        result = new Resources<>(items);

        items.forEach(rep -> {
                    rep.add(
                            entityLinks.linkForSingleResource(ItemRep.class, rep.getUUID()).withSelfRel(),
                            entityLinks.linkForSingleResource(ItemRep.class, rep.getUUID()).withRel("delete"));
                    result.add(linkTo(methodOn(ItemController.class).get(rep.getUUID())).withRel("item"));
                }
        );
        paginatedResult
            .getLinks().forEach(pagedResultLink -> {
            try {
                result.add(linkTo(methodOn(ItemListController.class).get(pagedResultLink.pageSize,
                    pagedResultLink.lastSeen,
                    pagedResultLink.action)).withRel(pagedResultLink.rel));
            } catch (MalformedURLException | UnsupportedEncodingException e) {
                log.error("Error while creating links", e);
            }
        });

        // Note that "create-form" is an IANA registered link relation
        // ref. https://tools.ietf.org/html/rfc6861
        result.add(linkTo(methodOn(ItemListController.class).getForm()).withRel("create-form"));

        return result;
    }
    @RequestMapping(value = "", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE, MediaTypes.HAL_JSON_VALUE})
    public ResponseEntity create(@RequestBody final CreateItemForm form) {
        String uuid = dao.create(form.getName(), form.getDescription());
        return ResponseEntity
                .created(URI.create("/items/" + uuid))
                .build();
    }
    @RequestMapping(value = "/form", method = RequestMethod.GET, produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public CreateItemForm getForm() {
        final CreateItemForm result = readForm();
        result.add(linkTo(methodOn(ItemListController.class).create(result)).withRel("create"));
        return result;
    }

    private CreateItemForm readForm() {
        Fairy fairy = Fairy.create();
        String name = String.format("%s-%s", fairy.person().lastName().toLowerCase(), fairy.person().firstName().toLowerCase());
        final CreateItemForm form = new CreateItemForm();
        form.setName(name);
        form.setDescription(String.format("Description of %s", name));
        return form;
    }
}
