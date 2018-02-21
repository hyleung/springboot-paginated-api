package com.example.controllers;

import com.example.representations.Root;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@EnableAutoConfiguration
@CrossOrigin(origins = "*")
public class RootController {

  @RequestMapping(value = "/", method = RequestMethod.GET, produces = {MediaTypes.HAL_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE} )
  public Root get() throws MalformedURLException, UnsupportedEncodingException {
    Root rep = new Root();
    rep.add(
        linkTo(methodOn(ItemListController.class).get(5, null, null)).withRel("items"));
    return rep;
  }
}
