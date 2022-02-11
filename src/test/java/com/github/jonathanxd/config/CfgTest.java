/*
 *      Config - Configuration library <https://github.com/JonathanxD/Config>
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
package com.github.jonathanxd.config;

import com.github.jonathanxd.config.backend.Backend;
import com.github.jonathanxd.config.backend.FunctionBackend;
import com.github.jonathanxd.config.serialize.Serializer;
import com.github.jonathanxd.config.serialize.Serializers;
import com.github.jonathanxd.iutils.box.MutableBox;
import com.github.jonathanxd.iutils.map.MapUtils;
import com.github.jonathanxd.iutils.object.Pairs;
import com.github.jonathanxd.iutils.type.TypeInfo;
import com.github.jonathanxd.iutils.type.TypeParameterProvider;

import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class CfgTest {

    @Test
    public void test() {
        Config config = new Config(new Backend() {
            Map<Object, Object> map;

            @Override
            public void save(Map<Object, Object> map) {
                this.map = map;
                System.out.println("SAVE -> " + map);
            }

            @Override
            public Map<Object, Object> load() {
                System.out.println(" LOAD ");
                return this.map;
            }
        });

        Key<Map<Object, Object>> rootKey = config.getRootKey().getAs(CommonTypes.MAP_OF_OBJECT);

        Key<String> name = rootKey.getKey("name", TypeInfo.of(String.class)).getKey("n", String.class);


        Assert.assertFalse(name.exists());
        name.setValue("ultra[red]");
        Assert.assertTrue(name.exists());
        System.out.println(name.getValue());
        config.save();

        ValueProcessKey<String> from = ValueProcessKey.from(name, null, new SimpleTransformer());

        String value = from.getValue();

        System.out.println(value);

        Key<List<Member>> members = rootKey.getKey("members", TypeInfo.builderOf(List.class).of(Member.class).buildGeneric());

        members.setValue(new ArrayList<>());

        List<Member> memberList = members.getValue();

        memberList.add(new Member("Jorge", 32));
        memberList.add(new Member("Maria", 25));

        Serializers.GLOBAL.register(TypeInfo.of(Member.class), new MemberSerializer());

        members.setValue(memberList);

        config.save();

        List<Member> memberList2 = members.getValue();

        Assert.assertEquals(memberList, memberList2);
    }

    @Test
    public void testMap() {
        Config config = new Config(new Backend() {
            Map<Object, Object> map;

            @Override
            public void save(Map<Object, Object> map) {
                this.map = map;
                System.out.println("SAVE -> " + map);
            }

            @Override
            public Map<Object, Object> load() {
                System.out.println(" LOAD ");
                return this.map;
            }
        });

        Key<Map<Object, Object>> rootKey = config.getRootKey().getAs(CommonTypes.MAP_OF_OBJECT);

        Map<String, String> mapi = new HashMap<>();

        mapi.put("Name", "Machine");
        mapi.put("Id", "game:machine");

        Key<Map<String, String>> players =
                rootKey.getKey("players", TypeInfo.builderOf(Map.class).of(String.class, String.class).buildGeneric());

        players.setValue(mapi);

        List<Map<String, String>> mapList = new ArrayList<>();

        //noinspection unchecked
        mapList.add(MapUtils.mapFromPairs(Pairs.of("Name", "RN"), Pairs.of("Id", "game:rn")));


        Key<List<Map<String, String>>> oops = rootKey.getKey("oops", TypeInfo.builderOf(List.class)
                .of(TypeInfo.builderOf(Map.class).of(String.class, String.class)).buildGeneric());

        oops.setValue(mapList);

        config.save();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void yamlTest() {
        Yaml yaml = new Yaml();
        MutableBox<String> yamlStr = new MutableBox<>("");

        Config config = new Config(
                new FunctionBackend(map -> {
                    yamlStr.set(yaml.dump(map));
                    System.out.println("Yaml = " + yamlStr.get() + "                  [action=SAVE]");
                }, () -> (Map<Object, Object>) yaml.load(yamlStr.get())));

        Key<Map<Object, Object>> rootKey = config.getRootKey().getAs(CommonTypes.MAP_OF_OBJECT);

        Map<String, String> mapi = new HashMap<>();

        mapi.put("Name", "Machine");
        mapi.put("Id", "game:machine");

        Key<Map<String, String>> players =
                rootKey.getKey("players", TypeInfo.builderOf(Map.class).of(String.class, String.class).buildGeneric());

        players.setValue(mapi);

        List<Map<String, String>> mapList = new ArrayList<>();

        //noinspection unchecked
        mapList.add(MapUtils.mapFromPairs(Pairs.of("Name", "RN"), Pairs.of("Id", "game:rn")));


        Key<List<Map<String, String>>> oops = rootKey.getKey("oops", TypeInfo.builderOf(List.class)
                .of(TypeInfo.builderOf(Map.class).of(String.class, String.class)).buildGeneric());

        oops.setValue(mapList);

        config.save();

        String save1 = yamlStr.get();

        config.load();

        config.save();

        read(config);

        String save2 = yamlStr.get();

        Assert.assertEquals(save1, save2);

        System.out.println("==========================================");
        System.out.println("First save: ");
        System.out.println("==========================================");
        System.out.println(save1);
        System.out.println("/==========================================/");

        System.out.println("==========================================");
        System.out.println("Second save: ");
        System.out.println("==========================================");
        System.out.println(save2);
        System.out.println("/==========================================/");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void uncommonKeyTest() {
        Yaml yaml = new Yaml();
        MutableBox<String> yamlStr = new MutableBox<>("");

        Config config = new Config(
                new FunctionBackend(map -> {
                    yamlStr.set(yaml.dump(map));
                    System.out.println("Yaml = " + yamlStr.get() + "                  [action=SAVE]");
                }, () -> (Map<Object, Object>) yaml.load(yamlStr.get())));

        Key<Map<Object, Object>> rootKey = config.getRootKey().getAs(CommonTypes.MAP_OF_OBJECT);

        Map<String, String> mapi = new HashMap<>();

        mapi.put("Name", "Machine");
        mapi.put("Id", "game:machine");

        Key<Map<String, String>> players =
                rootKey.getKey("players", TypeInfo.builderOf(Map.class).of(String.class, String.class).buildGeneric());

        players.setValue(mapi);

        List<Map<String, String>> mapList = new ArrayList<>();

        //noinspection unchecked
        mapList.add(MapUtils.mapFromPairs(Pairs.of("Name", "RN"), Pairs.of("Id", "game:rn")));


        Key<List<Map<String, String>>> oops = rootKey.getKey("oops", TypeInfo.builderOf(List.class)
                .of(TypeInfo.builderOf(Map.class).of(String.class, String.class)).buildGeneric());

        oops.setValue(mapList);


        ///////////////////////////
        Key<Map<Map<String, String>, String>> exKey = rootKey.getKey("uncommonKey",
                new TypeParameterProvider<Map<Map<String, String>, String>>() {
                }.createTypeInfo());

        Map<Map<String, String>, String> ex = new HashMap<>();
        ex.put(MapUtils.mapOf("key1", "key2"), "value1");

        exKey.setValue(ex);

        ///////////////////////////


        config.save();

        String save1 = yamlStr.get();

        config.load();

        config.save();

        read(config);

        String save2 = yamlStr.get();

        Assert.assertEquals(save1, save2);
    }

    private void read(Config config) {
        Key<Map<Object, Object>> rootKey = config.getRootKey().getAs(CommonTypes.MAP_OF_OBJECT);

        Key<List<Map<String, String>>> oops = rootKey.getKey("oops",
                new TypeParameterProvider<List<Map<String, String>>>() {
                }.createTypeInfo());

        List<Map<String, String>> value = oops.getValue();


        System.out.println(value);
    }

    public static class MemberSerializer implements Serializer<Member> {

        @Override
        public void serialize(Member value,
                              Key<Member> key,
                              TypeInfo<?> typeInfo,
                              Storage storage,
                              Serializers serializers) {
            key.getKey("name", String.class).setValue(value.getName());
            key.getKey("age", Integer.class).setValue(value.getAge());
        }

        @Override
        public Member deserialize(Key<Member> key,
                                  TypeInfo<?> typeInfo,
                                  Storage storage,
                                  Serializers serializers) {

            String name = key.getKey("name", String.class).getValue();
            int age = key.getKey("age", Integer.class).getValue();

            return new Member(name, age);
        }
    }

    public static class Member {
        private final String name;
        private final int age;

        Member(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return this.name;
        }

        public int getAge() {
            return this.age;
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.getName(), this.getAge());
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Member))
                return super.equals(obj);

            return Objects.equals(((Member) obj).getName(), this.getName())
                    && ((Member) obj).getAge() == this.getAge();
        }

        @Override
        public String toString() {
            return "Member[name=" + name + ", age=" + age + "]";
        }
    }


    static class SimpleTransformer implements UnaryOperator<String> {

        @Override
        public String apply(String s) {
            StringBuilder stringBuilder = new StringBuilder();

            char[] chars = s.toCharArray();

            int t = 0;

            for (char aChar : chars) {
                if (aChar == '[')
                    ++t;

                if (aChar == ']')
                    --t;

                if (t > 0)
                    aChar = Character.toUpperCase(aChar);

                stringBuilder.append(aChar);
            }

            return stringBuilder.toString();
        }
    }

}
