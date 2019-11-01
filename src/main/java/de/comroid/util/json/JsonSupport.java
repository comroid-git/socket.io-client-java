package de.comroid.util.json;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public final class JsonSupport {
    private JsonSupport() {
        throw new UnsupportedOperationException("Cannot instantiate JsonSupport");
    }
    
    public static JsonNode nodeOf(Object of) {
        if (of == null) {
            return JsonNodeFactory.instance.nullNode();
        } else if (of instanceof JsonNode) {
            return (JsonNode) of;
        } else if (of instanceof Collection) {
            return arrayNode(of);
        } else if (of instanceof Stream) {
            return arrayNode(((Stream) of).toArray());
        } else if (of instanceof Integer) {
            return JsonNodeFactory.instance.numberNode((Integer) of);
        } else if (of instanceof Long) {
            return JsonNodeFactory.instance.numberNode((Long) of);
        } else if (of instanceof Double) {
            return JsonNodeFactory.instance.numberNode((Double) of);
        } else if (of instanceof String) {
            return JsonNodeFactory.instance.textNode((String) of);
        } else if (of instanceof Boolean) {
            return JsonNodeFactory.instance.booleanNode((Boolean) of);
        } else {
            return JsonNodeFactory.instance.textNode(of.toString());
        }
    }

    public static <T, N> ArrayNode arrayNode(List<T> items, Function<T, N> mapper) {
        ArrayNode node = JsonNodeFactory.instance.arrayNode(items.size());
        for (T item : items) node.add(nodeOf(mapper.apply(item)));
        return node;
    }

    public static <T> ArrayNode arrayNode(List<T> items) {
        ArrayNode node = JsonNodeFactory.instance.arrayNode(items.size());
        for (T item : items) node.add(nodeOf(item));
        return node;
    }

    public static ArrayNode arrayNode(Object... items) {
        ArrayNode node = JsonNodeFactory.instance.arrayNode(items.length);
        for (Object item : items) node.add(nodeOf(item));
        return node;
    }
}
