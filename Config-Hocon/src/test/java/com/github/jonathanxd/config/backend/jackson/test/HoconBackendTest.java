/*
 *      Config-Hocon - Hocon backend for Config <https://github.com/JonathanxD/Config/>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2022 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
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
package com.github.jonathanxd.config.backend.jackson.test;

import com.github.jonathanxd.config.Config;
import com.github.jonathanxd.config.Key;
import com.github.jonathanxd.config.Storage;
import com.github.jonathanxd.config.backend.configurate.ConfigurateBackend;
import com.github.jonathanxd.config.serialize.Serializer;
import com.github.jonathanxd.config.serialize.Serializers;
import com.github.jonathanxd.iutils.box.IMutableBox;
import com.github.jonathanxd.iutils.box.MutableBox;
import com.github.jonathanxd.iutils.type.TypeInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.*;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class HoconBackendTest {

    @Test
    public void testToString() {
        IMutableBox<String> box = new MutableBox<>();
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .sink(() -> {
                    var sw = new StringBoxWriter(box);
                    return new BufferedWriter(sw);
                })
                .source(() -> new BufferedReader(new StringBoxReader(box)))
                .build();

        ConfigurateBackend jsonBackend = new ConfigurateBackend(loader);

        Config config = new Config(jsonBackend);
        Key<String> key = config.getRootKey().getKey("backend", String.class);
        Key<Byte> byteKey = config.getRootKey().getKey("byte", Byte.class);
        key.setValue("Jackson backend");

        byteKey.setValue((byte) 9);

        config.save();

        String first = box.get();
        Set<String> firstLines = Arrays.stream(first.split("\n")).collect(Collectors.toSet());

        box.set("{\"backend\": \"Jackson backend Uhu\", \"byte\": 9}"); // Hocon is a JSON superset.

        config.load();

        Byte b = byteKey.getValue();

        Assertions.assertAll("After saving config",
            () -> Assertions.assertEquals(Set.of("backend=\"Jackson backend\"", "byte=9"), firstLines, "Hocon resulting lines are not equal as the expected values.")
        );

        Assertions.assertAll("After loading modified config encoded as JSON",
            () -> Assertions.assertEquals("Jackson backend Uhu", key.getValue(), "Value of `backend` key is not equals to loaded value."),
            () -> Assertions.assertEquals(9, (int) b, "Value of `byte` key is not the same as the loaded value.")
        );

        box.set("backend=\"Jackson backend Uhu2\"\nbyte=10");

        config.load();

        Byte b2 = byteKey.getValue();

        Assertions.assertAll("After loading modified config encoded as HOCON",
                () -> Assertions.assertEquals("Jackson backend Uhu2", key.getValue(), "Value of `backend` key is not equals to loaded value."),
                () -> Assertions.assertEquals(10, (int) b2, "Value of `byte` key is not the same as the loaded value.")
        );
    }

    @Test
    public void testUserSerAsJsonSuperset() {
        IMutableBox<String> box = new MutableBox<>();
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .sink(() -> {
                    var sw = new StringBoxWriter(box);
                    return new BufferedWriter(sw);
                })
                .source(() -> new BufferedReader(new StringBoxReader(box)))
                .build();

        ConfigurateBackend jsonBackend = new ConfigurateBackend(loader);

        Config config = new Config(jsonBackend);
        config.getSerializers().register(TypeInfo.of(User.class), new UserSerializer());
        box.set("{\"name\": \"Test\", \"email\": \"examples@example.com\"}"); // Hocon is a JSON superset.

        config.load();

        Key<User> root = config.getRootKey().getAs(User.class);

        User value = root.getValue();

        Assertions.assertEquals("User{name='Test', email='examples@example.com'}", value.toString());

        root.setValue(new User("Test2", "examples@example.com"));

        Assertions.assertEquals("User{name='Test2', email='examples@example.com'}", root.getValue().toString());
        config.save();
        Set<String> lines = Arrays.stream(box.get().split("\n")).collect(Collectors.toSet());
        Assertions.assertEquals(Set.of("name=Test2", "email=\"examples@example.com\""), lines);
    }

    @Test
    public void testUserSerAsPlainHocon() {
        IMutableBox<String> box = new MutableBox<>();
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .sink(() -> {
                    var sw = new StringBoxWriter(box);
                    return new BufferedWriter(sw);
                })
                .source(() -> new BufferedReader(new StringBoxReader(box)))
                .build();

        ConfigurateBackend jsonBackend = new ConfigurateBackend(loader);

        Config config = new Config(jsonBackend);
        config.getSerializers().register(TypeInfo.of(User.class), new UserSerializer());
        box.set("name=Test\nemail=\"examples@example.com\"");

        config.load();

        Key<User> root = config.getRootKey().getAs(User.class);

        User value = root.getValue();

        Assertions.assertEquals("User{name='Test', email='examples@example.com'}", value.toString());

        root.setValue(new User("Test2", "examples@example.com"));

        Assertions.assertEquals("User{name='Test2', email='examples@example.com'}", root.getValue().toString());
        config.save();
        Set<String> lines = Arrays.stream(box.get().split("\n")).collect(Collectors.toSet());
        Assertions.assertEquals(Set.of("name=Test2", "email=\"examples@example.com\""), lines);
    }

    public static class UserSerializer implements Serializer<User> {
        @Override
        public void serialize(User value,
                              Key<User> key,
                              TypeInfo<?> typeInfo,
                              Storage storage,
                              Serializers serializers) {
            key.getKey("name", String.class).setValue(value.getName());
            key.getKey("email", String.class).setValue(value.getEmail());
        }

        @Override
        public User deserialize(Key<User> key,
                                TypeInfo<?> typeInfo,
                                Storage storage,
                                Serializers serializers) {

            String name = key.getKey("name", String.class).getValue();
            String email = key.getKey("email", String.class).getValue();

            return new User(name, email);
        }
    }

    public static final class User {
        private final String name;
        private final String email;

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }

}
