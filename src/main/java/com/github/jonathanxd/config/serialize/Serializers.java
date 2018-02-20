/*
 *      Config - Configuration library <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2018 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
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
package com.github.jonathanxd.config.serialize;

import com.github.jonathanxd.config.CommonTypes;
import com.github.jonathanxd.config.Key;
import com.github.jonathanxd.config.KeySpec;
import com.github.jonathanxd.config.Storage;
import com.github.jonathanxd.iutils.matching.When;
import com.github.jonathanxd.iutils.text.ArgsAppliedText;
import com.github.jonathanxd.iutils.text.CapitalizeComponent;
import com.github.jonathanxd.iutils.text.Color;
import com.github.jonathanxd.iutils.text.DecapitalizeComponent;
import com.github.jonathanxd.iutils.text.LocalizableComponent;
import com.github.jonathanxd.iutils.text.StringComponent;
import com.github.jonathanxd.iutils.text.Style;
import com.github.jonathanxd.iutils.text.Styles;
import com.github.jonathanxd.iutils.text.Text;
import com.github.jonathanxd.iutils.text.TextComponent;
import com.github.jonathanxd.iutils.text.TextUtil;
import com.github.jonathanxd.iutils.text.VariableComponent;
import com.github.jonathanxd.iutils.type.TypeInfo;
import com.github.jonathanxd.iutils.type.TypeInfoUtil;
import com.github.jonathanxd.iutils.type.TypeParameterProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Serializer manager. We recommend to not call {@link Serializers} function directly from {@link
 * Serializer}. Use {@link Key} functions, such as {@link Key#setValue(Object)}, {@link
 * Key#getValue()}, {@link Key#getKey(String, Class)}. Read {@link Key} documentation for more
 * info.
 *
 * @see Key
 */
public final class Serializers {

    /**
     * Default serializers
     */
    public static final Serializers GLOBAL = new Serializers();

    static {
        Serializers.GLOBAL.registerAll(CommonTypes.PRIMITIVE, new PrimitiveSerializer<>());
        Serializers.GLOBAL.register(CommonTypes.STRING, new PrimitiveSerializer<>());
        Serializers.GLOBAL.register(TypeInfo.of(List.class), new ListSerializer());
        Serializers.GLOBAL.register(TypeInfo.of(Map.class), new MapSerializer());
        Serializers.GLOBAL.register(CommonTypes.TYPE_INFO, new TypeInfoSerializer());
        Serializers.GLOBAL.register(CommonTypes.CLASS, new ClassSerializer());
        Serializers.GLOBAL.registerEnumSerializer(TypeInfo.of(TextSerializer.ComponentType.class));
        Serializers.GLOBAL.register(CommonTypes.UUID, new UuidSerializer());
        Serializers.GLOBAL.register(CommonTypes.TEXT_COMPONENT, new TextSerializer());
    }

    /**
     * Map to store serializers.
     */
    private final Map<TypeInfo<?>, Serializer> serializerMap = new HashMap<>();

    /**
     * Returns true if a serializer of provided {@link TypeInfo type information} is present, false
     * otherwise.
     *
     * @param typeInfo Type information.
     * @return True if a serializer of provided {@link TypeInfo type information} is present, false
     * otherwise.
     */
    public boolean hasSerializer(TypeInfo<?> typeInfo) {
        return this.findSerializer(typeInfo).isPresent()
                || (this != Serializers.GLOBAL && Serializers.GLOBAL.hasSerializer(typeInfo));
    }

    /**
     * Serialize the {@code value} to {@code key}.
     *
     * @param value Value to serialize.
     * @param key   Key to store serialized value.
     * @param <T>   Type of the value.
     */
    @SuppressWarnings("unchecked")
    public <T> void serialize(T value, Key<T> key) {
        this.findSerializer(key.getTypeInfo())
                .orElseThrow(() -> missingSerializer(key))
                .serialize(value, key, key.getTypeInfo(), key.getStorage(), this);
    }

    /**
     * Serialize the {@code value} to {@code key} and returns the plain value.
     *
     * @param value Value to serialize.
     * @param key   Key to store serialized value.
     * @param <T>   Type of the value.
     * @return Serialized value.
     */
    @SuppressWarnings("unchecked")
    public <T> T serializeAndGet(T value, Key<T> key) {
        this.serialize(value, key);
        return (T) key.getStorage().fetchValue(key);
    }

    /**
     * Serialize the {@code value} to {@code key}.
     *
     * @param value Value to serialize.
     * @param key   Key to store serialized value.
     */
    @SuppressWarnings("unchecked")
    public void serializeUnchecked(Object value, Key<?> key) {
        this.serializeUnchecked(value, key, key.getTypeInfo());
    }

    /**
     * Serialize the {@code value} to {@code key} and returns the plain value.
     *
     * @param value Value to serialize.
     * @param key   Key to store serialized value.
     * @return Serialized value.
     */
    @SuppressWarnings("unchecked")
    public Object serializeUncheckedAndGet(Object value, Key<?> key) {
        this.serializeUnchecked(value, key);
        return key.getStorage().fetchValue(key);
    }

    /**
     * Serialize the {@code value} to {@code key}.
     *
     * @param value    Value to serialize.
     * @param key      Key to store serialized value.
     * @param typeInfo Type of expected value.
     */
    @SuppressWarnings("unchecked")
    public void serializeUnchecked(Object value, Key<?> key, TypeInfo<?> typeInfo) {
        ((Serializer<Object>) this.findSerializer(typeInfo)
                .orElseThrow(() -> missingSerializer(key, typeInfo)))
                .serialize(value, (Key<Object>) key, typeInfo, key.getStorage(), this);
    }

    /**
     * Deserialize object from {@code key}.
     *
     * @param key Key to deserialize object.
     * @param <T> Type of value.
     * @return Object de-serialized from {@code key}.
     */
    @SuppressWarnings("unchecked")
    public <T> T deserialize(Key<T> key) {
        return this.findSerializer(key.getTypeInfo())
                .orElseThrow(() -> missingSerializer(key))
                .deserialize(key, key.getTypeInfo(), key.getStorage(), this);
    }

    /**
     * Deserialize object from {@code key}.
     *
     * @param key      Key to deserialize object.
     * @param typeInfo Information of type to deserialize.
     * @return Object de-serialized from {@code key}.
     */
    @SuppressWarnings("unchecked")
    public Object deserializeUnchecked(Key<?> key, TypeInfo<?> typeInfo) {
        return ((Serializer<Object>) this.findSerializer(typeInfo)
                .orElseThrow(() -> missingSerializer(key, typeInfo)))
                .deserialize((Key<Object>) key, typeInfo, key.getStorage(), this);
    }

    /**
     * Register a serializer of values of type {@link T}.
     *
     * @param typeInfo   Type of value that {@code serializer} can serialize and deserialize.
     * @param serializer Serializer.
     * @param <T>        Type of the value.
     */
    public <T> void register(TypeInfo<T> typeInfo, Serializer<T> serializer) {
        this.getSerializerMap().put(typeInfo, serializer);
    }

    /**
     * Register a enum serializer to serialize value of type {@link T}.
     *
     * @param typeInfo Enum concrete type (example: {@code ElementType}).
     * @param <T>      Enum type.
     */
    public <T extends Enum<T>> void registerEnumSerializer(TypeInfo<T> typeInfo) {
        this.getSerializerMap().put(typeInfo, new EnumSerializer<>());
    }

    /**
     * Register a serializer of values of type {@code typeInfo}.
     *
     * @param typeInfo   Type of value that {@code serializer} can serialize and deserialize.
     * @param serializer Serializer.
     */
    public void registerUnchecked(TypeInfo<?> typeInfo, Serializer serializer) {
        this.getSerializerMap().put(typeInfo, serializer);
    }

    /**
     * Register a serializer for all type info in {@code typeInfoIterable}.
     *
     * @param typeInfoIterable Iterable with all type info to register serializer to.
     * @param serializer       Serializer.
     */
    public void registerAll(Iterable<TypeInfo<?>> typeInfoIterable, Serializer serializer) {
        for (TypeInfo<?> info : typeInfoIterable) {
            this.registerUnchecked(info, serializer);
        }

    }

    /**
     * Find the serializer of type {@link T}.
     *
     * @param typeInfo Information about type {@link T}.
     * @param <T>      Type.
     * @return an Optional of the Serializer if found, or {@link Optional#empty()} otherwise.
     */
    @SuppressWarnings("unchecked")
    private <T> Optional<Serializer<T>> findSerializer(TypeInfo<T> typeInfo) {

        if (typeInfo == null)
            return Optional.empty();

        Class<? extends T> aClass = typeInfo.getTypeClass();

        if (aClass.getTypeParameters().length > 0) {
            TypeInfo<? extends T> info = TypeInfo.of(aClass);

            if (this.getSerializerMap().containsKey(info))
                return Optional.of((Serializer<T>) this.serializerMap.get(info));
        }

        if (this.getSerializerMap().containsKey(typeInfo))
            return Optional.of((Serializer<T>) this.serializerMap.get(typeInfo));

        for (Map.Entry<TypeInfo<?>, Serializer> entry : this.getSerializerMap().entrySet()) {
            if (entry.getKey().isAssignableFrom(typeInfo))
                return Optional.of((Serializer<T>) entry.getValue());
        }

        if (this != Serializers.GLOBAL)
            return Serializers.GLOBAL.findSerializer(typeInfo);

        return Optional.empty();
    }

    /**
     * Create exception of missing serializer of {@code key}.
     *
     * @param key Key which serializer is not present.
     * @return Serializer Exception.
     */
    private RuntimeException missingSerializer(Key<?> key) {
        throw new IllegalStateException("Missing serializer of key '" + key + "' of type '" + key.getTypeInfo() + "'!");
    }

    /**
     * Create exception of missing serializer of {@code key} of {@code type}.
     *
     * @param key  Key which serializer is not present.
     * @param type Type of missing serializer.
     * @return Serializer Exception.
     */
    private RuntimeException missingSerializer(Key<?> key, TypeInfo<?> type) {
        throw new IllegalStateException("Missing serializer of key '" + key + "' of type '" + type + "'!");
    }

    /**
     * Gets the serializer map.
     *
     * @return Serializer map.
     */
    private Map<TypeInfo<?>, Serializer> getSerializerMap() {
        return this.serializerMap;
    }

    static class BasicSerializer<T> implements Serializer<T> {

        @Override
        public void serialize(T value, Key<T> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            storage.pushValue(key, value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T deserialize(Key<T> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            return (T) storage.fetchValue(key);
        }
    }

    static class PrimitiveSerializer<T> implements Serializer<T> {

        @Override
        public void serialize(T value, Key<T> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            storage.pushValue(key, value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public T deserialize(Key<T> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            Object value = storage.fetchValue(key);

            if (value == null)
                return null;

            String s = value.toString();

            return (T) When.When(typeInfo,
                    When.Matches(CommonTypes::isBoolean, t -> Boolean.valueOf(s)),
                    When.Matches(CommonTypes::isByte, t -> Byte.valueOf(s)),
                    When.Matches(CommonTypes::isShort, t -> Short.valueOf(s)),
                    When.Matches(CommonTypes::isChar, t -> s.charAt(0)),
                    When.Matches(CommonTypes::isInteger, t -> Integer.valueOf(s)),
                    When.Matches(CommonTypes::isFloat, t -> Float.valueOf(s)),
                    When.Matches(CommonTypes::isLong, t -> Long.valueOf(s)),
                    When.Matches(CommonTypes::isDouble, t -> Double.valueOf(s)),
                    When.Matches(CommonTypes::isString, t -> s)
            ).evaluate().getValue();
        }
    }

    static class MapSerializer implements Serializer<Map> {

        @SuppressWarnings("unchecked")
        @Override
        public void serialize(Map value, Key<Map> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {

            Map<Object, Object> newMap = new LinkedHashMap<>();
            TypeInfo<?> keyType = typeInfo.getTypeParameter(0); // K
            TypeInfo<?> valueType = typeInfo.getTypeParameter(1); // V

            Map<String, Object> temp = new LinkedHashMap<>();
            Storage newStorage = Storage.createMapStorage(key, temp);

            for (Map.Entry<?, ?> o : ((Map<?, ?>) value).entrySet()) {
                Key<?> vKey = key.getAs(keyType, newStorage);
                Key<?> vValue = key.getAs(valueType, newStorage);

                Object serializedKey = serializers.serializeUncheckedAndGet(o.getKey(), vKey);
                temp.clear();

                Object serializedValue = serializers.serializeUncheckedAndGet(o.getValue(), vValue);
                temp.clear();

                newMap.put(serializedKey, serializedValue);
            }

            storage.pushValue(key, newMap);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map deserialize(Key<Map> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {

            Map<Object, Object> newMap = new LinkedHashMap<>();

            Object value = storage.fetchValue(key);

            if (value instanceof Map) {
                TypeInfo<?> keyType = typeInfo.getTypeParameter(0); // K
                TypeInfo<?> valueType = typeInfo.getTypeParameter(1); // V

                Storage newStorage = Storage.createMapStorage(key);

                for (Map.Entry<?, ?> o : ((Map<?, ?>) value).entrySet()) {
                    Key<?> vKey = key.getAs(keyType, newStorage);
                    Key<?> vValue = key.getAs(valueType, newStorage);

                    newStorage.pushValue(vKey, o.getKey());

                    Object deserializedKey = serializers.deserialize(vKey);

                    newStorage.pushValue(vKey, o.getValue()); // Emulated
                    Object desserializedValue = serializers.deserialize(vValue);

                    newMap.put(deserializedKey, desserializedValue);
                }

            }


            return newMap;
        }
    }

    /**
     * Serialize a list
     */
    static class ListSerializer implements Serializer<List> {

        @Override
        public void serialize(List value, Key<List> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            TypeInfo<?> elementType = typeInfo.getTypeParameter(0);

            Storage newStorage = Storage.createListStorage(key);

            for (int i = 0; i < value.size(); i++) {
                Object o = value.get(i);

                Key<?> newKey = key.getAs(key.getName() + ":" + i, elementType, newStorage);

                serializers.serializeUncheckedAndGet(o, newKey);
            }

            value.size();
        }

        @Override
        public List deserialize(Key<List> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {

            Object value = storage.fetchValue(key);

            List<Object> result = new ArrayList<>();

            if (value instanceof List) {
                TypeInfo<?> elementType = typeInfo.getTypeParameter(0);

                List list = (List) value;

                Storage listStorage = Storage.createListStorage(key);

                for (int i = 0; i < list.size(); i++) {
                    Object o = list.get(i);

                    Key<?> newKey = key.getAs(key.getName() + ":" + i, elementType, listStorage);

                    listStorage.pushValue(newKey, o);

                    result.add(serializers.deserialize(newKey));
                }
            }

            return result;
        }
    }

    static class ClassSerializer implements Serializer<Class<?>> {

        @Override
        public void serialize(Class<?> value, Key<Class<?>> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            storage.store(key, TypeInfo.of(String.class), value.getCanonicalName());
        }

        @Override
        public Class<?> deserialize(Key<Class<?>> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            try {
                return Class.forName(storage.getAs(key, String.class));
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Cannot resolve class. key: '" + key + "'!", e);
            }
        }

    }

    static class TypeInfoSerializer implements Serializer<TypeInfo<?>> {

        @Override
        public void serialize(TypeInfo<?> value, Key<TypeInfo<?>> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            key.getAs(String.class).setValue(TypeInfoUtil.toFullString(value));
        }

        @Override
        public TypeInfo<?> deserialize(Key<TypeInfo<?>> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            return TypeInfoUtil.fromFullString(key.getAs(String.class).getValue()).get(0);
        }
    }

    static class EnumSerializer<T extends Enum<T>> implements Serializer<T> {

        @Override
        public void serialize(T value, Key<T> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            key.getAs(String.class).setValue(value.name());
        }

        @SuppressWarnings("unchecked")
        @Override
        public T deserialize(Key<T> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            Class<?> typeClass = typeInfo.getTypeClass();
            return Enum.valueOf((Class<T>) typeClass, key.getAs(String.class).getValue());
        }
    }

    /**
     * Serialize to a string using {@link TextUtil#toString(TextComponent)} and deserialize using
     * {@link TextUtil#parse(String)}.
     */
    public static class TextAsStringSerializer implements Serializer<TextComponent> {

        @Override
        public void serialize(TextComponent value, Key<TextComponent> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            key.getAs(String.class).setValue(TextUtil.toString(value));
        }

        @Override
        public TextComponent deserialize(Key<TextComponent> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            return TextUtil.parse(key.getAs(String.class).getValue());
        }
    }

    /**
     * Serializers {@link UUID}.
     */
    public static class UuidSerializer implements Serializer<UUID> {

        @Override
        public void serialize(UUID value, Key<UUID> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            key.getAs(String.class).setValue(value.toString());
        }

        @Override
        public UUID deserialize(Key<UUID> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            return UUID.fromString(key.getAs(String.class).getValue());
        }

    }

    /**
     * Serialize to a Config-style text component.
     */
    public static class TextSerializer implements Serializer<TextComponent> {

        private static final KeySpec<ComponentType> TYPE = KeySpec.create("componentType", ComponentType.class);
        private static final KeySpec<Map<String, TextComponent>> ARGS = KeySpec.create("args",
                new TypeParameterProvider<Map<String, TextComponent>>() {
                }.createTypeInfo());
        private static final KeySpec<TextComponent> TEXT = KeySpec.create("text", TextComponent.class);
        private static final KeySpec<String> STRING_TEXT = KeySpec.create("text", String.class);

        private static final KeySpec<String> LOCALE = KeySpec.create("locale", String.class);
        private static final KeySpec<List<TextComponent>> CHILDREN = KeySpec.create("children",
                new TypeParameterProvider<List<TextComponent>>() {
                }.createTypeInfo());

        private static final KeySpec<String> VARIABLE = KeySpec.create("variable", String.class);
        private static final KeySpec<String> LOCALIZABLE = KeySpec.create("localizable", String.class);

        @Override
        public void serialize(TextComponent value, Key<TextComponent> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            ComponentType componentType = When.When(value,
                    When.InstanceOf(CapitalizeComponent.class, component -> ComponentType.Capitalize),
                    When.InstanceOf(DecapitalizeComponent.class, component -> ComponentType.Decapitalize),
                    When.InstanceOf(ArgsAppliedText.class, component -> ComponentType.ArgsApplied),
                    When.InstanceOf(StringComponent.class, component -> ComponentType.String),
                    When.InstanceOf(VariableComponent.class, component -> ComponentType.Variable),
                    When.InstanceOf(LocalizableComponent.class, component -> ComponentType.Localizable),
                    When.InstanceOf(Color.class, component -> ComponentType.Color),
                    When.InstanceOf(Style.class, component -> ComponentType.Style),
                    When.InstanceOf(Text.class, component -> ComponentType.Text),
                    When.Else(component -> {
                        throw new UnsupportedOperationException("Cannot serialize component: '" + value + "'!");
                    })
            ).evaluate().getValue();

            key.get(TYPE).setValue(componentType);

            switch (componentType) {
                case Capitalize: {
                    key.get(TEXT).setValue(((CapitalizeComponent) value).getTextComponent());
                    break;
                }
                case Decapitalize: {
                    key.get(TEXT).setValue(((DecapitalizeComponent) value).getTextComponent());
                    break;
                }
                case ArgsApplied: {
                    key.get(ARGS).setValue(((ArgsAppliedText) value).getArgs());
                    key.get(TEXT).setValue(((ArgsAppliedText) value).getComponent());
                    break;
                }
                case String: {
                    key.get(STRING_TEXT).setValue(((StringComponent) value).getText());
                    break;
                }
                case Variable: {
                    key.get(VARIABLE).setValue(((VariableComponent) value).getVariable());
                    break;
                }
                case Localizable: {
                    key.get(LOCALIZABLE).setValue(((LocalizableComponent) value).getLocalization());
                    String locale = ((LocalizableComponent) value).getLocale();
                    if (locale != null) {
                        key.get(LOCALE).setValue(locale);
                    }
                    break;
                }
                case Color: {
                    Color color = (Color) value;
                    key.getKey("name", String.class).setValue((color).getName());
                    key.getKey("red", Integer.TYPE).setValue((color).getR());
                    key.getKey("green", Integer.TYPE).setValue((color).getG());
                    key.getKey("blue", Integer.TYPE).setValue((color).getB());
                    key.getKey("alpha", Float.TYPE).setValue((color).getA());

                    break;
                }
                case Style: {
                    Style style = (Style) value;
                    boolean isReset =
                            !(style.isUnderline() || style.isBold() || style.isStrikeThrough() || style.isObfuscated() || style.isItalic());
                    key.getKey("reset", Boolean.TYPE).setValue(isReset);

                    if (!isReset) {
                        if (style.isUnderline())
                            key.getKey("underline", Boolean.TYPE).setValue(true);
                        if (style.isBold())
                            key.getKey("bold", Boolean.TYPE).setValue(true);
                        if (style.isStrikeThrough())
                            key.getKey("strikeThrough", Boolean.TYPE).setValue(true);
                        if (style.isObfuscated())
                            key.getKey("obfuscated", Boolean.TYPE).setValue(true);
                        if (style.isItalic())
                            key.getKey("italic", Boolean.TYPE).setValue(true);
                    }
                    break;
                }
                case Text: {
                    key.get(CHILDREN).setValue(((Text) value).getComponents());
                    break;
                }
                default: {
                    throw new UnsupportedOperationException("Cannot serialize component of type: '" + componentType.name() + "'!");
                }
            }

        }

        @Override
        public TextComponent deserialize(Key<TextComponent> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
            ComponentType type = key.get(TYPE).getValue();

            switch (type) {
                case Capitalize: {
                    return CapitalizeComponent.of(key.get(TEXT).getValue());
                }
                case Decapitalize: {
                    return DecapitalizeComponent.of(key.get(TEXT).getValue());
                }
                case ArgsApplied: {
                    Map<String, TextComponent> args = key.get(ARGS).getValue();
                    return key.get(TEXT).getValue().apply(args);
                }
                case String: {
                    return Text.single(key.get(STRING_TEXT).getValue());
                }
                case Variable: {
                    return Text.variable(key.get(VARIABLE).getValue());
                }
                case Localizable: {
                    String localizable = key.get(LOCALIZABLE).getValue();
                    String locale = key.get(LOCALE).getValueOr(null);
                    return LocalizableComponent.of(locale, localizable);
                }
                case Color: {
                    String name = key.getKey("name", String.class).getValue();
                    Color byName = Color.getByName(name);
                    Key<Integer> redKey = key.getKey("red", Integer.TYPE);
                    Key<Integer> greenKey = key.getKey("green", Integer.TYPE);
                    Key<Integer> blueKey = key.getKey("blue", Integer.TYPE);
                    Key<Float> alphaKey = key.getKey("alpha", Float.TYPE);

                    if (byName != null
                            && (!redKey.exists() || !greenKey.exists() || !blueKey.exists()))
                        return byName;

                    int red = redKey.getValue();
                    int green = greenKey.getValue();
                    int blue = blueKey.getValue();
                    float alpha = alphaKey.getValueOr(1.0F);

                    return Color.createColor(name, red, green, blue, alpha);
                }
                case Style: {
                    boolean reset = key.getKey("reset", Boolean.TYPE).getValueOr(Boolean.FALSE);

                    if (reset)
                        return Styles.RESET;

                    boolean underline = key.getKey("underline", Boolean.TYPE).getValueOr(Boolean.FALSE);
                    boolean bold = key.getKey("bold", Boolean.TYPE).getValueOr(Boolean.FALSE);
                    boolean strikeThrough = key.getKey("strikeThrough", Boolean.TYPE).getValueOr(Boolean.FALSE);
                    boolean obfuscated = key.getKey("obfuscated", Boolean.TYPE).getValueOr(Boolean.FALSE);
                    boolean italic = key.getKey("italic", Boolean.TYPE).getValueOr(Boolean.FALSE);

                    return Style.createStyle(obfuscated, bold, strikeThrough, underline, italic);
                }
                case Text: {
                    List<TextComponent> value = key.get(CHILDREN).getValue();
                    return Text.of(value);
                }
                default: {
                    throw new UnsupportedOperationException("Cannot deserialize component of type: '" + type.name() + "'!");
                }
            }
        }

        public enum ComponentType {
            Capitalize,
            Decapitalize,
            ArgsApplied,
            String,
            Variable,
            Localizable,
            Color,
            Style,
            Text
        }
    }
}
