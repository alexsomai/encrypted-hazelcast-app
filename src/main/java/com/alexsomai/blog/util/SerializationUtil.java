package com.alexsomai.blog.util;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.internal.serialization.impl.ObjectDataOutputStream;
import com.hazelcast.nio.ObjectDataInput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class SerializationUtil {

    private static final InternalSerializationService SERIALIZATION_SERVICE = new DefaultSerializationServiceBuilder().build();

    private SerializationUtil() {}

    public static byte[] serialize(Object object) {
        if (object == null) {
            return null;
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            ObjectDataOutputStream
                odos = com.hazelcast.internal.serialization.impl.SerializationUtil.createObjectDataOutputStream(oos, SERIALIZATION_SERVICE);
            odos.writeObject(object);
            odos.flush();

            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), e);
        }
    }

    public static Object deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            ObjectDataInput
                odi = com.hazelcast.internal.serialization.impl.SerializationUtil.createObjectDataInputStream(ois, SERIALIZATION_SERVICE);

            return odi.readObject();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to deserialize object", e);
        }
    }
}
