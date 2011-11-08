/*
 * Copyright 2011 Erlend Hamnaberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.hamnaberg.json;

import net.hamnaberg.json.util.Lists;
import net.hamnaberg.json.util.Predicate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultJsonCollection extends AbstractJsonCollection {
    private final List<Link> links = new ArrayList<Link>();
    private final List<Item> items = new ArrayList<Item>();
    private final List<Query> queries = new ArrayList<Query>();
    private final Template template;

    public DefaultJsonCollection(URI href) {
        this(href, Version.ONE);
    }

    public DefaultJsonCollection(URI href, Version version) {
        this(href, version, Collections.<Link>emptyList(), Collections.<Item>emptyList(), Collections.<Query>emptyList(), null);
    }

    public DefaultJsonCollection(URI href, Version version, List<Item> items) {
        this(href, version, Collections.<Link>emptyList(), items, Collections.<Query>emptyList(), null);
    }

    public DefaultJsonCollection(URI href, Version version, List<Link> links, List<Item> items, List<Query> queries, Template template) {
        super(href, version);
        if (links != null) {
            this.links.addAll(links);
        }
        if (items != null) {
            this.items.addAll(items);
        }
        if (queries != null) {
            this.queries.addAll(queries);
        }
        this.template = template;
    }

    @Override
    public List<Link> getLinks() {
        return Collections.unmodifiableList(links);
    }

    @Override
    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public List<Query> getQueries() {
        return Collections.unmodifiableList(queries);
    }

    @Override
    public boolean hasTemplate() {
        return template != null;
    }

    public Link findLink(Predicate<Link> predicate) {
        return findFirst(links, predicate);
    }

    public List<Link> findLinks(Predicate<Link> predicate) {
        return Lists.filter(links, predicate);
    }

    public Item findItem(Predicate<Item> predicate) {
        return findFirst(items, predicate);
    }

    public List<Item> findItems(Predicate<Item> predicate) {
        return Lists.filter(items, predicate);
    }

    public Query findQuery(Predicate<Query> predicate) {
        return findFirst(queries, predicate);
    }

    public List<Query> findQueries(Predicate<Query> predicate) {
        return Lists.filter(queries, predicate);
    }

    private <T> T findFirst(List<T> collection, Predicate<T> predicate) {
        List<T> filter = Lists.filter(collection, predicate);
        if (filter.isEmpty()) {
            return null;
        }
        return filter.get(0);
    }

    public Item getFirst() {
        if (items.isEmpty()) {
            return null;
        }
        return items.get(0);
    }

    @Override
    public Template getTemplate() {
        return template;
    }

    @Override
    public ErrorMessage getError() {
        throw new UnsupportedOperationException("Incorrect Collection type.");
    }

    public static class Builder {
        private final URI href;
        private Version version = Version.ONE;
        private final List<Item> itemBuilder = new ArrayList<Item>();
        private final List<Link> linkBuilder = new ArrayList<Link>();
        private final List<Query> queryBuilder = new ArrayList<Query>();
        private Template template;

        public Builder(URI href) {
            this.href = href;
        }

        public Builder withVersion(Version version) {
            this.version = version;
            return this;
        }

        public Builder withTemplate(Template template) {
            this.template = template;
            return this;
        }

        public Builder addItem(Item item) {
            itemBuilder.add(item);
            return this;
        }

        public Builder addItems(Iterable<Item> items) {
            addToList(items, itemBuilder);
            return this;
        }

        public Builder addQuery(Query query) {
            queryBuilder.add(query);
            return this;
        }

        public Builder addQueries(Iterable<Query> queries) {
            addToList(queries, queryBuilder);
            return this;
        }

        public Builder addLink(Link link) {
            linkBuilder.add(link);
            return this;
        }

        public Builder addLinks(Iterable<Link> links) {
            addToList(links, linkBuilder);
            return this;
        }

        private <A> void addToList(Iterable<A> iterable, List<A> list) {
            for (A a : iterable) {
                list.add(a);
            }
        }

        public JsonCollection build() {
            return new DefaultJsonCollection(href, version, linkBuilder, itemBuilder, queryBuilder, template);
        }
    }
}
