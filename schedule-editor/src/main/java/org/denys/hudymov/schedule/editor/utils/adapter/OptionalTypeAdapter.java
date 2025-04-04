package org.denys.hudymov.schedule.editor.utils.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public class OptionalTypeAdapter<E> extends TypeAdapter<Optional<E>> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != Optional.class) {
                return null;
            }

            final ParameterizedType parameterizedType = (ParameterizedType) type.getType();
            final Type actualType = parameterizedType.getActualTypeArguments()[0];
            final TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(actualType));
            return new OptionalTypeAdapter(adapter);
        }
    };
    private final TypeAdapter<E> elementTypeAdapter;

    public OptionalTypeAdapter(TypeAdapter<E> elementTypeAdapter) {
        this.elementTypeAdapter = elementTypeAdapter;
    }

    @Override
    public void write(JsonWriter out, Optional<E> value) throws IOException {
        if (value.isPresent()) {
            elementTypeAdapter.write(out, value.get());

        } else {
            out.nullValue();
        }
    }

    @Override
    public Optional<E> read(JsonReader in) throws IOException {
        if (in.peek() != JsonToken.NULL) {
            return Optional.ofNullable(elementTypeAdapter.read(in));
        }

        in.nextNull();
        return Optional.empty();
    }
}