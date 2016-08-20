package com.example.pagination

import static org.assertj.core.api.Assertions.*
import com.example.controllers.pagination.PaginationStrategy
import com.example.controllers.pagination.PreviousPageStrategy
import com.example.dao.ItemDao
import com.example.domain.Item
import com.google.common.collect.Lists
import spock.lang.Specification

/**
 * Test suite for paginating into "previous" result set.
 *
 * Assumes that sorting is via ID, descending:
 *
 * i.e. [10, 9, 8, 7, 6, 5, 4, 3, 2, 1]
 *
 * Whereas the pagination logic for "next" gets the page of results where
 * ID < lastSeen, for "previous", we go in the opposite direction.
 *
 * e.g. lastSeen=7&action=previous
 *
 * Result set should be [8, 7] with a "lastSeen" of 9.
 *
 */
class PreviousPageStrategySpec extends Specification {
    def ItemDao dao = Stub(ItemDao)
    def PaginationStrategy strategy

    def setup() {
        strategy = new PreviousPageStrategy()
    }
    def 'Previous page should produce correct data set'() {
        given:
            def pageSize = 2
            def lastSeen = 7
            dao.listPrevious(pageSize, lastSeen) >> Lists.newArrayList(
                    new Item(9, 'some-uuid', 'some-name', 'some-description'),
                    new Item(8, 'some-uuid', 'some-name', 'some-description'),
                    new Item(7, 'some-uuid', 'some-name', 'some-description')
            )
            dao.getMinId() >> 1
        when:
            def result = strategy.getPaginatedResult(dao, pageSize, lastSeen)
        then:
            assertThat(result.result)
                .extracting({item -> item.id})
                .contains(8, 7)
    }

    def 'Previous page should produce correct "self" link'() {
        given:
            def pageSize = 2
            def lastSeen = 7
            dao.listPrevious(pageSize, lastSeen) >> Lists.newArrayList(
                    new Item(9, 'some-uuid', 'some-name', 'some-description'),
                    new Item(8, 'some-uuid', 'some-name', 'some-description'),
                    new Item(7, 'some-uuid', 'some-name', 'some-description')
            )
            dao.getMinId() >> 1
        when:
            def result = strategy.getPaginatedResult(dao, pageSize, lastSeen)
        then:
            def self = result.links.find { l -> l.rel == "self" }
            self.lastSeen == 9
    }
    def 'Previous page should produce correct "next" link'() {
        given:
            def pageSize = 2
            def lastSeen = 7
            dao.listPrevious(pageSize, lastSeen) >> Lists.newArrayList(
                    new Item(9, 'some-uuid', 'some-name', 'some-description'),
                    new Item(8, 'some-uuid', 'some-name', 'some-description'),
                    new Item(7, 'some-uuid', 'some-name', 'some-description')
            )
            dao.getMinId() >> 1
        when:
            def result = strategy.getPaginatedResult(dao, pageSize, lastSeen)
        then:
            def next = result.links.find { l -> l.rel == "next" }
            next.lastSeen == 7
    }

    def 'Previous page should produce correct "prev" link'() {
        given:
            def pageSize = 2
            def lastSeen = 7
            dao.listPrevious(pageSize, lastSeen) >> Lists.newArrayList(
                    new Item(9, 'some-uuid', 'some-name', 'some-description'),
                    new Item(8, 'some-uuid', 'some-name', 'some-description'),
                    new Item(7, 'some-uuid', 'some-name', 'some-description')
            )
            dao.getMinId() >> 1
        when:
            def result = strategy.getPaginatedResult(dao, pageSize, lastSeen)
        then:
            def prev = result.links.find { l -> l.rel == "prev" }
            prev.lastSeen == 9
            prev.action == "previous"
    }
    def 'Previous page should not produce "prev" link on first page'() {
        given:
            def pageSize = 2
            def lastSeen = 9
            dao.listPrevious(pageSize, lastSeen) >> Lists.newArrayList(
                    new Item(10, 'some-uuid', 'some-name', 'some-description'),
                    new Item(9, 'some-uuid', 'some-name', 'some-description')
            )
            dao.getMinId() >> 1
        when:
            def result = strategy.getPaginatedResult(dao, pageSize, lastSeen)
        then:
            assertThat(result.getLinks())
                    .extracting{l -> l.rel}
                    .doesNotContain("prev")
    }
}
