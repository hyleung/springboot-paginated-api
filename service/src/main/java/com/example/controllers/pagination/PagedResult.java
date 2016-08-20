package com.example.controllers.pagination;

import org.springframework.hateoas.Link;

import java.util.List;

/**
 * Created by hyleung on 2016-08-05.
 */
public class PagedResult<T> {
    private List<T> result;
    private List<PagedResultLink> links;

    public PagedResult(final List<T> result, final List<PagedResultLink> links) {
        this.result = result;
        this.links = links;
    }

    public List<T> getResult() {
        return result;
    }

    public List<PagedResultLink> getLinks() {
        return links;
    }

    public static class PagedResultLink {
        public final Integer pageSize;
        public final Integer lastSeen;
        public final String action;
        public final String rel;

        public PagedResultLink(final Integer pageSize, final Integer lastSeen, final String action, final String rel) {
            this.pageSize = pageSize;
            this.lastSeen = lastSeen;
            this.action = action;
            this.rel = rel;
        }
    }
}
