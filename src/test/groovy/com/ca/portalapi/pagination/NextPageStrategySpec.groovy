package com.ca.portalapi.pagination

import com.ca.portalapi.controllers.pagination.PagedResult

import static org.assertj.core.api.Assertions.*
import com.ca.portalapi.controllers.pagination.NextPageStrategy
import com.ca.portalapi.controllers.pagination.PaginationStrategy
import com.ca.portalapi.dao.ItemDao
import com.ca.portalapi.domain.Item
import com.google.common.collect.Lists
import spock.lang.Specification

/**
 * Test suite for paginating into "next" result set.
 *
 * Assumes that sorting is via ID, descending:
 *
 * i.e. [10, 9, 8, 7, 6, 5, 4, 3, 2, 1]
 *
 * Page 1, should display [10, 9]. Page 2, [8,7] - with a "lastSeen" of 9.
 */
class NextPageStrategySpec extends Specification {
    def ItemDao dao = Stub(ItemDao)
    def PaginationStrategy strategy

    def setup() {
        strategy = new NextPageStrategy()
    }

    def 'First page of results should only produce "next" and "self" links (no "previous")'() {
        given:
            def pageSize = 2
            dao.list(pageSize, Optional.empty()) >> Lists.newArrayList(
                    new Item(10, 'some-uuid', 'some-name', 'some-description'),
                    new Item(9, 'some-uuid', 'some-name', 'some-description')
            )
            dao.getMinId() >> 1
        when:
            def result = strategy.getPaginatedResult(dao, pageSize, null)
        then:
            assertThat(result.getLinks())
                    .extracting{l -> l.rel}
                    .contains("next")
        and:
            assertThat(result.getLinks())
                    .extracting{l -> l.rel}
                    .doesNotContain("prev")
        and:
            assertThat(result.getLinks())
                    .extracting{l -> l.rel}
                    .contains("self")
    }

    def 'Last page of results should only produce "previous" and "self" links (no "next")'() {
        given:
            def pageSize = 2
            def lastSeen = 3
            dao.list(pageSize, Optional.of(lastSeen)) >> Lists.newArrayList(
                    new Item(2, 'some-uuid', 'some-name', 'some-description'),
                    new Item(1, 'some-uuid', 'some-name', 'some-description')
            )
            dao.getMinId() >> 1
        when:
            def result = strategy.getPaginatedResult(dao, pageSize, lastSeen)
        then:
            assertThat(result.getLinks())
                    .extracting{l -> l.rel}
                    .contains("prev")
            and:
            assertThat(result.getLinks())
                    .extracting{l -> l.rel}
                    .doesNotContain("next")
            and:
            assertThat(result.getLinks())
                    .extracting{l -> l.rel}
                    .contains("self")
    }

    def 'Middle page of results should produce "previous", "next", and "self" links'() {
        given:
        def pageSize = 2
        def lastSeen = 5
        dao.list(pageSize, Optional.of(lastSeen)) >> Lists.newArrayList(
                new Item(4, 'some-uuid', 'some-name', 'some-description'),
                new Item(3, 'some-uuid', 'some-name', 'some-description')
        )
        dao.getMinId() >> 1
        when:
        def result = strategy.getPaginatedResult(dao, pageSize, lastSeen)
        then:
        assertThat(result.getLinks())
                .extracting{l -> l.rel}
                .contains("prev")
        and:
        assertThat(result.getLinks())
                .extracting{l -> l.rel}
                .contains("next")
        and:
        assertThat(result.getLinks())
                .extracting{l -> l.rel}
                .contains("self")
    }

    def 'Prev links should have lastSeen same as self, plus action of "previous"'() {
        given:
            def pageSize = 2
            def lastSeen = 5
            dao.list(pageSize, Optional.of(lastSeen)) >> Lists.newArrayList(
                    new Item(4, 'some-uuid', 'some-name', 'some-description'),
                    new Item(3, 'some-uuid', 'some-name', 'some-description')
            )
            dao.getMinId() >> 1
        when:
            def result = strategy.getPaginatedResult(dao, pageSize, lastSeen)
        then:
            assertThat(result.getLinks())
                    .extracting{l -> l.rel}
                    .contains("prev")
        and:
            def prev = result.links.find { l -> l.rel == "prev" }
            prev.lastSeen == lastSeen
            prev.action == "previous"
    }
}
