/*
 * Copyright 2012 Erlend Hamnaberg
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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.hamnaberg.json.extension.Extended;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Property extends Extended<Property> {
    public Property(ObjectNode delegate) {
        super(delegate);
    }

    public String getName() {
        return delegate.get("name").asText();
    }

    public Optional<Value> getValue() {
        return ValueFactory.createValue(delegate.get("value"));
    }

    public Optional<String> getPrompt() {
        return delegate.has("prompt") ? Optional.of(delegate.get("prompt").asText()) : Optional.<String>absent();
    }

    public List<Value> getArray() {
        JsonNode array = delegate.get("array");
        ImmutableList.Builder<Value> builder = ImmutableList.builder();
        if (array != null && array.isArray()) {
            for (JsonNode n : array) {
                Optional<Value> opt = ValueFactory.createValue(n);
                if (opt.isPresent()) {
                    builder.add(opt.get());
                }
            }
        }
        return builder.build();
    }

    public Map<String, Value> getObject() {
        ImmutableMap.Builder<String, Value> builder = ImmutableMap.builder();
        JsonNode object = delegate.get("object");
        if (object != null && object.isObject()) {
            Iterator<Map.Entry<String,JsonNode>> fields = object.getFields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> next = fields.next();
                Optional<Value> opt = ValueFactory.createValue(next.getValue());
                if (opt.isPresent()) {
                    builder.put(next.getKey(), opt.get());
                }
            }
        }
        return builder.build();
    }

    @Override
    public String toString() {
        return String.format("Property with name %s, value %s, array %s, object %s, prompt %s", getName(), getValue().orNull(), getArray(), getObject(), getPrompt());
    }

    public static Property value(String name, Optional<String> prompt, Optional<Value> value) {
        ObjectNode node = makeObject(name, prompt);
        if (value.isPresent()) {
            node.put("value", getJsonValue(value.get()));
        }
        return new Property(node);
    }

    public static Property array(String name, Optional<String> prompt, List<Value> list) {
        ObjectNode node = makeObject(name, prompt);
        ArrayNode array = JsonNodeFactory.instance.arrayNode();
        for (Value value : list) {
            array.add(getJsonValue(value));
        }
        node.put("array", array);
        return new Property(node);
    }

    public static Property object(String name, Optional<String> prompt, Map<String, Value> object) {
        ObjectNode node = makeObject(name, prompt);
        ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
        for (Map.Entry<String, Value> entry : object.entrySet()) {
            objectNode.put(entry.getKey(), getJsonValue(entry.getValue()));
        }
        node.put("object", objectNode);
        return new Property(node);
    }

    @Override
    protected Property copy(ObjectNode value) {
        return new Property(value);
    }

    private static ObjectNode makeObject(String name, Optional<String> prompt) {
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("name", name);
        if (prompt.isPresent()) {
            node.put("prompt", prompt.get());
        }
        return node;
    }

    private static JsonNode getJsonValue(Value value) {
        if (value.isNumeric()) {
            return new DoubleNode(value.asNumber().doubleValue());
        }
        else if (value.isString()) {
            return new TextNode(value.asString());
        }
        else if (value.isBoolean()) {
            return BooleanNode.valueOf(value.asBoolean());
        }
        return NullNode.getInstance();
    }

}
