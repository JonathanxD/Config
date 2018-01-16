/*
 *      Config - Configuration library <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2017 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/) <jonathan.scripter@programmer.net>
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
import com.github.jonathanxd.iutils.box.MutableBox;
import com.github.jonathanxd.iutils.map.MapUtils;
import com.github.jonathanxd.iutils.type.TypeInfo;
import com.github.jonathanxd.iutils.type.TypeParameterProvider;

import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

public class SimpleTest {

    private final Map<String, Person> personRegistry = new HashMap<>();

    @Test
    public void serializationTest() {
        YamlBackend yamlBackend = new YamlBackend();
        Config config = new Config(yamlBackend);

        this.registerSerializers(config);

        // Keys
        Key<Map<Object, Object>> rootKey = config.getRootKey();
        Key<Map<Person, Score>> scoreKey = rootKey.getKey("score", new TypeParameterProvider<Map<Person, Score>>(){}.createTypeInfo());
        Key<My> myKey = rootKey.getKey("myEnum", new TypeParameterProvider<My>(){}.createTypeInfo());
        // /Keys

        myKey.setValue(My.A);

        Person dev = new Person("Dev", 1);
        Map<Person, Score> scores = new HashMap<>();
        personRegistry.put(dev.getName(), dev);
        scores.put(dev, new Score(MapUtils.mapOf("kills", 1, "deaths", 56)));

        scoreKey.setValue(scores);
        config.save();

        System.out.println(yamlBackend.getYaml());

        // Read
        String yaml = yamlBackend.getYaml();

        yamlBackend.setYaml(yaml);
        config.load();

        Map<Person, Score> value = scoreKey.getValue();
        My my = myKey.getValue();

        System.out.println(value);
        Assert.assertEquals(My.A, my);

        yamlBackend.setYaml(
                "score:\n" +
                "  Dev: {kills: 1, deaths: 56}\n" +
                "myEnum: B\n");
        config.load();

        Assert.assertEquals(My.B, myKey.getValue());
    }

    private void registerSerializers(Config config) {
        config.getSerializers().register(TypeInfo.of(Person.class), new PersonSerializer(this.personRegistry));
        config.getSerializers().register(TypeInfo.of(Score.class), new ScoreSerializer());
        config.getSerializers().registerEnumSerializer(TypeInfo.of(My.class));
    }

    static class YamlBackend implements Backend {
        private final Yaml yaml;
        private final MutableBox<String> yamlStr = new MutableBox<>("");

        YamlBackend() {
            this(new Yaml());
        }

        YamlBackend(Yaml yaml) {
            this.yaml = yaml;
        }

        @Override
        public void save(Map<Object, Object> map) {
            yamlStr.set(yaml.dump(map));
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map<Object, Object> load() {
            return (Map<Object, Object>) yaml.load(yamlStr.get());
        }

        void setYaml(String yaml) {
            this.yamlStr.set(yaml);
        }

        String getYaml() {
            return this.yamlStr.get();
        }

        @Override
        public boolean supports(TypeInfo<?> type) {
            return CommonTypes.isValidBasicType(type);
        }
    }

    public static enum My {
        A,
        B
    }

}
