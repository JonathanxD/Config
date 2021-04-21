/*
 *      Config - Configuration library <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2021 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
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
package com.github.jonathanxd.config;

import com.github.jonathanxd.iutils.collection.Collections3;
import com.github.jonathanxd.iutils.text.TextComponent;
import com.github.jonathanxd.iutils.type.TypeInfo;
import com.github.jonathanxd.iutils.type.TypeParameterProvider;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Common basic types.
 *
 * All common types that should be supported by backend is:
 *
 * {@link String}, {@link Boolean}, {@link Byte}, {@link Short}, {@link Character}, {@link Integer},
 * {@link Float}, {@link Long}, {@link Double}, {@link List} (of these types) and {@link Map} (of
 * these types).
 */
public class CommonTypes {

    public static final TypeInfo<Boolean> BOXED_BOOLEAN = TypeInfo.of(Boolean.class);
    public static final TypeInfo<Byte> BOXED_BYTE = TypeInfo.of(Byte.class);
    public static final TypeInfo<Short> BOXED_SHORT = TypeInfo.of(Short.class);
    public static final TypeInfo<Character> BOXED_CHAR = TypeInfo.of(Character.class);
    public static final TypeInfo<Integer> BOXED_INTEGER = TypeInfo.of(Integer.class);
    public static final TypeInfo<Float> BOXED_FLOAT = TypeInfo.of(Float.class);
    public static final TypeInfo<Long> BOXED_LONG = TypeInfo.of(Long.class);
    public static final TypeInfo<Double> BOXED_DOUBLE = TypeInfo.of(Double.class);
    public static final TypeInfo<Void> BOXED_VOID = TypeInfo.of(Void.class);

    public static final TypeInfo<Boolean> BOOLEAN = TypeInfo.of(Boolean.TYPE);
    public static final TypeInfo<Byte> BYTE = TypeInfo.of(Byte.TYPE);
    public static final TypeInfo<Short> SHORT = TypeInfo.of(Short.TYPE);
    public static final TypeInfo<Character> CHAR = TypeInfo.of(Character.TYPE);
    public static final TypeInfo<Integer> INTEGER = TypeInfo.of(Integer.TYPE);
    public static final TypeInfo<Float> FLOAT = TypeInfo.of(Float.TYPE);
    public static final TypeInfo<Long> LONG = TypeInfo.of(Long.TYPE);
    public static final TypeInfo<Double> DOUBLE = TypeInfo.of(Double.TYPE);
    public static final TypeInfo<Void> VOID = TypeInfo.of(Void.TYPE);

    public static final TypeInfo<Object> OBJECT = TypeInfo.of(Object.class);
    public static final TypeInfo<String> STRING = TypeInfo.of(String.class);
    public static final TypeInfo<UUID> UUID = TypeInfo.of(UUID.class);
    public static final TypeInfo<TextComponent> TEXT_COMPONENT = TypeInfo.of(TextComponent.class);

    public static final TypeInfo<Map> MAP = TypeInfo.of(Map.class);
    public static final TypeInfo<Map<Object, Object>> MAP_OF_OBJECT = TypeInfo.builderOf(Map.class).of(Object.class, Object.class).buildGeneric();
    public static final TypeInfo<List> LIST = TypeInfo.of(List.class);

    public static final TypeInfo<TypeInfo<?>> TYPE_INFO = new TypeParameterProvider<TypeInfo<?>>() {
    }.createTypeInfo();

    public static final TypeInfo<Class<?>> CLASS = new TypeParameterProvider<Class<?>>() {
    }.createTypeInfo();

    public static final List<TypeInfo<?>> PRIMITIVE = Collections.unmodifiableList(Collections3.listOf(
            BOXED_BOOLEAN, BOOLEAN,
            BYTE, BOXED_BYTE,
            SHORT, BOXED_SHORT,
            CHAR, BOXED_CHAR,
            INTEGER, BOXED_INTEGER,
            FLOAT, BOXED_FLOAT,
            LONG, BOXED_LONG,
            DOUBLE, BOXED_DOUBLE
    ));

    public static boolean isBoolean(TypeInfo<?> type) {
        return BOXED_BOOLEAN.isAssignableFrom(type) || BOOLEAN.isAssignableFrom(type);
    }

    public static boolean isByte(TypeInfo<?> type) {
        return BOXED_BYTE.isAssignableFrom(type) || BYTE.isAssignableFrom(type);
    }

    public static boolean isShort(TypeInfo<?> type) {
        return BOXED_SHORT.isAssignableFrom(type) || SHORT.isAssignableFrom(type);
    }

    public static boolean isChar(TypeInfo<?> type) {
        return BOXED_CHAR.isAssignableFrom(type) || CHAR.isAssignableFrom(type);
    }

    public static boolean isInteger(TypeInfo<?> type) {
        return BOXED_INTEGER.isAssignableFrom(type) || INTEGER.isAssignableFrom(type);
    }

    public static boolean isFloat(TypeInfo<?> type) {
        return BOXED_FLOAT.isAssignableFrom(type) || FLOAT.isAssignableFrom(type);
    }

    public static boolean isLong(TypeInfo<?> type) {
        return BOXED_LONG.isAssignableFrom(type) || LONG.isAssignableFrom(type);
    }

    public static boolean isDouble(TypeInfo<?> type) {
        return BOXED_DOUBLE.isAssignableFrom(type) || DOUBLE.isAssignableFrom(type);
    }

    public static boolean isObject(TypeInfo<?> type) {
        return OBJECT.isAssignableFrom(type);
    }

    public static boolean isString(TypeInfo<?> type) {
        return STRING.isAssignableFrom(type);
    }

    public static boolean isValidBasicType(TypeInfo<?> type) {

        return CommonTypes.isBoolean(type)
                || CommonTypes.isByte(type)
                || CommonTypes.isShort(type)
                || CommonTypes.isChar(type)
                || CommonTypes.isInteger(type)
                || CommonTypes.isFloat(type)
                || CommonTypes.isLong(type)
                || CommonTypes.isDouble(type)
                //|| BOXED_VOID.isAssignableFrom(type)
                //|| VOID.isAssignableFrom(type)
                //|| CommonTypes.isObject(type)
                || CommonTypes.isString(type)
                || CommonTypes.isValidMap(type)
                || CommonTypes.isValidList(type);
    }

    public static boolean isValidMap(TypeInfo<?> map) {
        return map.getTypeClass() == MAP.getTypeClass()
                && map.getTypeParameters().stream().allMatch(CommonTypes::isValidBasicType);
    }

    public static boolean isValidList(TypeInfo<?> list) {
        return list.getTypeClass() == LIST.getTypeClass()
                && list.getTypeParameters().stream().allMatch(CommonTypes::isValidBasicType);
    }
}
