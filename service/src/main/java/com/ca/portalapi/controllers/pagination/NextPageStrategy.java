package com.ca.portalapi.controllers.pagination;

import com.ca.portalapi.dao.ItemDao;
import com.ca.portalapi.domain.Item;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by leuho02 on 2016-08-08.
 */
public class NextPageStrategy implements PaginationStrategy {
    @Override
    public PagedResult<Item> getPaginatedResult(final ItemDao dao, final Integer pageSize, final Integer lastSeen) throws MalformedURLException, UnsupportedEncodingException {
        List<PagedResult.PagedResultLink> links = new ArrayList<>();
        List<Item> items = dao.list(pageSize, Optional.ofNullable(lastSeen));
        final Integer minId = dao.getMinId();
        final Integer newLastSeenId = items.get(items.size() - 1).getId();
        //if we're not on the last page (i.e. our result set contains the item with min id)
        // add a link to "next"
        if (minId != newLastSeenId) {
            links.add(new PagedResult.PagedResultLink(pageSize, newLastSeenId, null, "next"));
        }
        //if we're on the first page, there will be no "lastSeen" parameter,
        //don't show a previous link
        if (lastSeen != null) {
            links.add(new PagedResult.PagedResultLink(pageSize, lastSeen, "previous", "prev"));
        }
        links.add(new PagedResult.PagedResultLink(pageSize, lastSeen, null, "self"));
        return new PagedResult<>(items, links);
    }
}
