/*
 * Copyright (C) 2016 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.sta.mqtt.subscription;

import de.fraunhofer.iosb.ilt.sta.json.serialize.EntityFormatter;
import de.fraunhofer.iosb.ilt.sta.model.core.Entity;
import de.fraunhofer.iosb.ilt.sta.parser.query.QueryParser;
import de.fraunhofer.iosb.ilt.sta.path.EntitySetPathElement;
import de.fraunhofer.iosb.ilt.sta.path.Property;
import de.fraunhofer.iosb.ilt.sta.path.ResourcePath;
import de.fraunhofer.iosb.ilt.sta.query.Query;
import de.fraunhofer.iosb.ilt.sta.settings.CoreSettings;
import de.fraunhofer.iosb.ilt.sta.util.StringHelper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jab
 */
public class EntitySetSubscription extends AbstractSubscription {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntitySetSubscription.class);
    private final Set<Property> selectedProperties = new HashSet<>();

    public EntitySetSubscription(String topic, ResourcePath path, String serviceRootUrl) {
        super(topic, path, serviceRootUrl);
        init();
    }

    private void init() {
        entityType = ((EntitySetPathElement) path.getLastElement()).getEntityType();

        Query query = parseQuery(SubscriptionFactory.getQueryFromTopic(topic));
        if (query != null
                && query.getSelect() != null
                && !query.getSelect().isEmpty()) {
            if (query.getCount().isPresent()
                    || !query.getExpand().isEmpty()
                    || query.getFilter() != null
                    || !query.getOrderBy().isEmpty()
                    || query.getSkip().isPresent()
                    || query.getTop().isPresent()) {
                throw new IllegalArgumentException("Invalid subscription to: '" + topic + "': only $select is allowed in query options.");
            }
            selectedProperties.addAll(query.getSelect());
        }
        generateFilter(1);
    }

    private Query parseQuery(String topic) {
        String queryString = null;
        try {
            queryString = URLDecoder.decode(topic, StringHelper.UTF8.name());
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Unsupported encoding.", ex);
        }
        try {
            return QueryParser.parseQuery(queryString, new CoreSettings());
        } catch (IllegalArgumentException e) {
            LOGGER.error("Invalid query: {} ERROR: {}", queryString, e.getMessage());
            return null;
        }
    }

    @Override
    public String doFormatMessage(Entity entity) throws IOException {
        if (!selectedProperties.isEmpty()) {
            entity.setSelectedProperties(selectedProperties);
        }
        return EntityFormatter.writeEntity(entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), selectedProperties);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        final EntitySetSubscription other = (EntitySetSubscription) obj;
        return Objects.equals(this.selectedProperties, other.selectedProperties);
    }

}
