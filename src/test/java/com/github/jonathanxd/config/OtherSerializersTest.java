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

import com.github.jonathanxd.config.backend.MapBackend;
import com.github.jonathanxd.config.serialize.Serializer;
import com.github.jonathanxd.config.serialize.Serializers;
import com.github.jonathanxd.iutils.collection.Collections3;
import com.github.jonathanxd.iutils.type.TypeInfo;
import com.github.jonathanxd.iutils.type.TypeParameterProvider;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OtherSerializersTest {
    private static final TypeInfo<List<User>> USER_LIST_TYPE = new TypeParameterProvider<List<User>>() {}.createTypeInfo();

    @Test
    public void listTest() {
        Config config = new Config(new MapBackend());
        config.getSerializers().register(TypeInfo.of(User.class), new UserSerializer());

        KeySpec<List<User>> uuids = KeySpec.create("users", USER_LIST_TYPE);
        List<User> uuidList = Collections3.listOf(
                new User(LocalDate.now(), "test"),
                new User(LocalDate.now(), "test2")
        );

        config.getRootKey().get(uuids).setValue(uuidList);

        List<User> value = config.getRootKey().get(uuids).getValue();

        Assert.assertEquals(uuidList, value);
    }

    public static class User {
        private final LocalDate registrationDate;
        private final String name;

        public User(LocalDate registrationDate, String name) {
            this.registrationDate = registrationDate;
            this.name = name;
        }

        public LocalDate getRegistrationDate() {
            return registrationDate;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "registrationDate=" + registrationDate +
                    ", name='" + name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return Objects.equals(this.getRegistrationDate(), user.getRegistrationDate()) && Objects.equals(this.getName(), user.getName());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getRegistrationDate(), this.getName());
        }
    }

    public static class UserSerializer implements Serializer<User> {

        @Override
        public void serialize(User value, Key<User> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) throws SerializationException {
            key.getKey("registrationDate", LocalDate.class).setValue(value.getRegistrationDate());
            key.getKey("name", String.class).setValue(value.getName());
        }

        @Override
        public User deserialize(Key<User> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) throws SerializationException {
            return new User(
                    key.getKey("registrationDate", LocalDate.class).getValue(),
                    key.getKey("name", String.class).getValue()
            );
        }
    }

}
