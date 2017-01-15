/*
 *      Config - Configuration API. <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2016 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/ & https://github.com/TheRealBuggy/) <jonathan.scripter@programmer.net>
 *      Copyright (c) contributors
 *
 *
 *      Permission is hereby granted, free of charge, to any person obtaining a copy
 *      of this software and associated documentation files (the "Software"), to deal
 *      in the Software without restriction, including without limitation the rights
 *      to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *      copies of the Software, and to permit persons to whom the Software is
 *      furnished to do so, subject to the following conditions:
 *
 *      The above copyright notice and this permission notice shall be included in
 *      all copies or substantial portions of the Software.
 *
 *      THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *      IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *      FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *      AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *      LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *      OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *      THE SOFTWARE.
 */
package com.github.jonathanxd.config.serializer;

import com.github.jonathanxd.config.key.Node;
import com.github.jonathanxd.iutils.conditions.Conditions;
import com.github.jonathanxd.iutils.containers.primitivecontainers.IntContainer;
import com.github.jonathanxd.iutils.exceptions.RethrowException;
import com.github.jonathanxd.iutils.function.collector.BiCollectors;
import com.github.jonathanxd.iutils.function.stream.MapStream;
import com.github.jonathanxd.iutils.map.MapUtils;
import com.github.jonathanxd.iutils.object.GenericRepresentation;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jonathan on 25/06/16.
 */
public class Serializers {

    private static final Serializers DEFAULT = new Serializers(
            MapUtils.mapOf(GenericRepresentation.aEnd(Collection.class), new CollectionSerializer(),
                    GenericRepresentation.aEnd(Map.class), new MapSerializer(),
                    GenericRepresentation.aEnd(Class.class), new ClassSerializer(),
                    GenericRepresentation.aEnd(Enum.class), new EnumSerializer(),
                    GenericRepresentation.aEnd(GenericRepresentation.class), new GenericRepresentationSerializer())
    );

    private final Map<GenericRepresentation<?>, Serializer<?>> serializerMap = new HashMap<>();

    Serializers(Map<GenericRepresentation<?>, Serializer<?>> serializers) {
        this.serializerMap.putAll(serializers);
    }

    public Serializers() {

    }

    public static Serializers getDefaultSerializers() {
        return DEFAULT;
    }

    private static boolean hasDefaultPublicConstructor(Class<?> aClass) {
        try {
            return Modifier.isPublic(aClass.getConstructor().getModifiers());
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public void registerSerializer(Serializer<?> serializer) {
        validade(serializer);

        serializerMap.put(getType(serializer), serializer);
    }

    public void unregisterSerializer(Serializer<?> serializer) {
        validade(serializer);

        serializerMap.remove(getType(serializer));
    }

    @SuppressWarnings("unchecked")
    public <T> Serializer<T> getSerializer(GenericRepresentation<T> representation) {

        if (this != DEFAULT) {
            Serializer<?> defaultSerializer = DEFAULT.getSerializer(representation);

            if (defaultSerializer != null)
                return (Serializer<T>) defaultSerializer;
        }

        Class<? extends T> firstType = representation.getAClass();

        if (firstType.getTypeParameters().length != 0) {
            // Is Generic
            GenericRepresentation<? extends T> plain = GenericRepresentation.aEnd(firstType);

            Serializer<?> assignable = findAssignable(plain);

            if (assignable != null) {
                return (Serializer<T>) assignable;
            }

            /**
             if (this.serializerMap.containsKey(plain)) {
             return (Serializer<T>) this.serializerMap.get(plain);
             }*/
        }

        return (Serializer<T>) findAssignable(representation);
    }

    public <T> Serializer<T> getRequiredSerializer(GenericRepresentation<T> representation) {
        return Conditions.checkNotNull(getSerializer(representation), "Cannot get Serializer for type: '" + representation + "'!");
    }

    private Serializer<?> findAssignable(GenericRepresentation<?> genericRepresentation) {

        if (this.serializerMap.containsKey(genericRepresentation))
            return this.serializerMap.get(genericRepresentation);

        Map<GenericRepresentation<?>, Serializer<?>> collected = MapStream.of(this.serializerMap)
                .filter((genericRepresentation1, serializer) -> genericRepresentation1.compareToAssignable(genericRepresentation) == 0)
                .collect(BiCollectors.toMap());

        Iterator<Map.Entry<GenericRepresentation<?>, Serializer<?>>> iterator = collected.entrySet().iterator();

        Serializer<?> last = null;

        while (iterator.hasNext()) {

            Map.Entry<GenericRepresentation<?>, Serializer<?>> current = iterator.next();

            if (!iterator.hasNext())
                last = current.getValue();
        }

        return last;
    }

    private void validade(Serializer<?> serializer) {
        try {
            GenericRepresentation[] references = serializer.getReferences();

            if (references.length == 0) {
                throw new IllegalArgumentException("Empty representation!");
            } else if (references[0].getAClass().equals(Object.class)) {
                throw new IllegalArgumentException("Cannot create Serializer for Object class!");
            }
        } catch (Throwable t) {
            throw new IllegalArgumentException("Cannot infer generic type of Serializer: '" + serializer.getClass().getCanonicalName() + "'", t);
        }
    }

    private GenericRepresentation<?> getType(Serializer<?> serializer) {
        return serializer.getReferences()[0];
    }

    private static final class CollectionSerializer implements Serializer<Collection> {

        @Override
        public void serialize(Collection value, com.github.jonathanxd.config.key.Node node, GenericRepresentation<?> representation) {

            if (representation.getRelated().length == 0)
                throw new IllegalArgumentException("Missing element type of collection. Representation: '" + representation + "'!");

            GenericRepresentation at0 = representation.getRelated()[0];// Element

            int x = 0;

            for (Object val : value) {
                node.getNode(String.valueOf(x)).setValue(val, at0);
                ++x;
            }

        }

        @Override
        public Collection deserialize(com.github.jonathanxd.config.key.Node node, GenericRepresentation<?> representation) {

            if (representation.getRelated().length == 0)
                throw new IllegalArgumentException("Missing element type of collection. Representation: '" + representation + "'!");

            Collection collection = new ArrayList();
            try {
                GenericRepresentation at0 = representation.getRelated()[0];// Element

                for (com.github.jonathanxd.config.key.Node node1 : node.getChildrenNodes()) {
                    collection.add(node1.getValue(at0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return collection;
        }
    }

    @SuppressWarnings("unchecked")
    private static final class MapSerializer implements Serializer<Map> {

        @Override
        public void serialize(Map value, com.github.jonathanxd.config.key.Node node, GenericRepresentation<?> representation) {

            if (representation.getRelated().length < 2)
                throw new IllegalArgumentException("Missing Key and/or Value type of Map. Representation: '" + representation + "'!");

            GenericRepresentation keyType = representation.getRelated()[0];// Element
            GenericRepresentation valueType = representation.getRelated()[1];// Element

            boolean defaultConstructor = hasDefaultPublicConstructor(keyType.getAClass());

            if (keyType.getAClass().equals(String.class)) {
                value.forEach((key, v) -> {
                    node.getNode(key.toString()).setValue(v, valueType);
                });
            } else {
                IntContainer intContainer = new IntContainer();

                value.forEach((key, v) -> {

                    if (!defaultConstructor) {
                        node.getNode(String.valueOf(intContainer.get()), "key").setValue(key, keyType);
                        node.getNode(String.valueOf(intContainer.get()), "value").setValue(v, valueType);
                        intContainer.add();
                    } else {
                        node.getNode(key).setValue(key, keyType);
                        node.getNode(key, "value").setValue(v, valueType);

                    }
                });
            }

        }

        @Override
        public Map deserialize(com.github.jonathanxd.config.key.Node node, GenericRepresentation<?> representation) {

            Map map = new HashMap();

            if (representation.getRelated().length < 2)
                throw new IllegalArgumentException("Missing Key and/or Value type of Map. Representation: '" + representation + "'!");

            GenericRepresentation keyType = representation.getRelated()[0];// Element
            GenericRepresentation valueType = representation.getRelated()[1];// Element

            boolean defaultConstructor = hasDefaultPublicConstructor(keyType.getAClass());

            try {

                Node[] childrenNodes = node.getChildrenNodes();

                if (keyType.getAClass().equals(String.class)) {
                    for (Node childrenNode : childrenNodes) {
                        String name = (String) childrenNode.getPath().getPathName();
                        Object value = childrenNode.getValue(valueType);
                        map.put(name, value);
                    }

                } else {
                    for (Node childrenNode : childrenNodes) {
                        Object key;
                        Object value;

                        if (!defaultConstructor) {
                            key = childrenNode.getNode("key").getValue(keyType);
                            value = childrenNode.getNode("value").getValue(valueType);
                        } else {
                            key = childrenNode.getValue(keyType);
                            value = childrenNode.getNode("value").getValue(valueType);
                        }
                        map.put(key, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return map;
        }
    }

    public static class ClassSerializer implements Serializer<Class> {

        @Override
        public void serialize(Class value, Node node, GenericRepresentation<?> representation) {
            node.setValue(value.getName());
        }

        @Override
        public Class deserialize(Node node, GenericRepresentation<?> representation) {
            try {
                return Class.forName(String.valueOf(node.getValue()));
            } catch (ClassNotFoundException e) {
                throw new RethrowException(e, e.getCause());
            }
        }
    }

    public static class EnumSerializer implements Serializer<Enum> {

        @Override
        public void serialize(Enum value, Node node, GenericRepresentation<?> representation) {
            node.getNode("type").setValue(value.getDeclaringClass(), Class.class);
            node.getNode("name").setValue(value.name());
        }

        @Override
        public Enum deserialize(Node node, GenericRepresentation<?> representation) {
            Class type = node.getNode("type").getValue(Class.class);
            String name = node.getNode("name").getValueAsString();

            return Enum.valueOf(type, name);
        }
    }

    public static class GenericRepresentationSerializer implements Serializer<GenericRepresentation> {

        @Override
        public void serialize(GenericRepresentation value, Node node, GenericRepresentation<?> representation) {
            node.setValue(value.toFullString());
        }

        @Override
        public GenericRepresentation deserialize(Node node, GenericRepresentation<?> representation) {
            try {
                return GenericRepresentation.fromFullString(node.getValueAsString()).get(0);
            } catch (ClassNotFoundException e) {
                throw new RethrowException(e, e.getCause());
            }
        }
    }
}
