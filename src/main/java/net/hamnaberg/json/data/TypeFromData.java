package net.hamnaberg.json.data;

import net.hamnaberg.json.Data;
import net.hamnaberg.json.util.Optional;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import java.io.IOException;

public class TypeFromData<A> implements FromData<Optional<A>> {
    private final Class<A> type;
    private final ObjectMapper mapper;

    public TypeFromData(Class<A> type) {
        this(type, new ObjectMapper());
    }

    public TypeFromData(Class<A> type, ObjectMapper mapper) {
        this.type = type;
        this.mapper = mapper;
    }

    @Override
    public Optional<A> apply(Data data) {
        ObjectNode node = new JsonObjectFromData().apply(data);
        try {
            return Optional.some(mapper.treeToValue(node, type));
        } catch (IOException e) {
            return Optional.none();
        }
    }
}
