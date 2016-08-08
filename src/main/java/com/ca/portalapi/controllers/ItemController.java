package com.ca.portalapi.controllers;

import com.ca.portalapi.dao.ItemDao;
import com.ca.portalapi.domain.Item;
import com.ca.portalapi.exceptions.ResourceNotFound;
import com.ca.portalapi.representations.CreateItemForm;
import com.ca.portalapi.representations.ItemRep;
import io.codearte.jfairy.Fairy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.BasicLinkBuilder;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import javax.websocket.server.PathParam;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    @Autowired
    private ItemDao dao;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemRep> listJson() {
        return dao
                .list(Optional.empty(), Optional.empty())
                .stream()
                .map(item -> new ItemRep(item.getId(), item.getUuid(), item.getName(), item.getDescription()))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaTypes.HAL_JSON_VALUE)
    public Resources<ItemRep> listHalJson(@RequestParam(value = "pageSize", required = false) Integer pageSize,
                                          @RequestParam(value = "lastSeen", required = false) Integer lastSeen,
                                          @RequestParam(value = "prev", required = false) String prev) throws MalformedURLException, UnsupportedEncodingException {
        final List<ItemRep> items = list(pageSize, lastSeen);
        final Resources<ItemRep> result = new Resources<>(items);
        result.add(linkTo(methodOn(ItemController.class).readFormHal()).withRel("create-form"));
        final ControllerLinkBuilder selfRelBuilder = linkTo(methodOn(ItemController.class).listHalJson(pageSize, lastSeen, prev));
        result.add(selfRelBuilder.withSelfRel());
        if (pageSize != null) {
            log.debug("Fetching page {}, last_seen_id = {}", pageSize, lastSeen);
            //compute min last seen id
            final Integer min = items.stream()
                    .min(Comparator.comparingInt(ItemRep::getRecordId))
                    .map(ItemRep::getRecordId)
                    .orElseThrow(RuntimeException::new);
            //encode the prev URI
            String uri = "?pageSize=" + pageSize;
            if (lastSeen != null) {
                uri += "&lastSeen=" + lastSeen;
            }
            if (prev != null) {
                uri += "&prev=" + prev;
            }
            final String encodedPrev = URLEncoder.encode(uri, "UTF-8");
            result.add(linkTo(methodOn(ItemController.class)
                    .listHalJson(pageSize, min, encodedPrev))
                    .withRel("next"));
            if (prev != null) {
                final String decoded = URLDecoder.decode(prev, "UTF-8");
                result.add(BasicLinkBuilder.linkToCurrentMapping().slash("items" + decoded).withRel("prev"));
            }
        }
        return result;
    }

    private List<ItemRep> list(final Integer pageSize, final Integer lastSeen) {
        return dao
                .list(Optional.ofNullable(pageSize), Optional.ofNullable(lastSeen))
                .stream()
                .map(item -> new ItemRep(item.getId(), item.getUuid(), item.getName(), item.getDescription()))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ItemRep getJson(@PathVariable("id") final String id) {
        return dao
                .get(id)
                .map(item -> new ItemRep(item.getId(), item.getUuid(), item.getName(),item.getDescription()))
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
