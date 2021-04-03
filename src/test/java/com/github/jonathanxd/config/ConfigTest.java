/*
 *      Config - Configuration API. <https://github.com/JonathanxD/Config>
 *
 *         The MIT License (MIT)
 *
 *      Copyright (c) 2021 TheRealBuggy/JonathanxD (https://github.com/JonathanxD/ & https://github.com/TheRealBuggy/) <jonathan.scripter@programmer.net>
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

import com.github.jonathanxd.config.common.MapBackend;
import com.github.jonathanxd.config.converter.Converter;
import com.github.jonathanxd.config.key.ConvertKey;
import com.github.jonathanxd.config.key.Key;
import com.github.jonathanxd.config.key.Node;
import com.github.jonathanxd.config.serializer.Serializer;
import com.github.jonathanxd.config.transformer.Transformer;
import com.github.jonathanxd.config.yaml.YamlBackend;
import com.github.jonathanxd.iutils.exception.RethrowException;
import com.github.jonathanxd.iutils.map.MapUtils;
import com.github.jonathanxd.iutils.string.JString;
import com.github.jonathanxd.iutils.type.TypeInfo;
import com.github.jonathanxd.iutils.type.TypeParameterProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by jonathan on 25/06/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class ConfigTest {


    @Before
    public void init() {

    }

    @Test
    public void test() {
        Config<ID> config = new Config<>(new MapBackend());

        Key<String> texto = config.createKey(String.class, config.getPath("TEXTO"), new ColorTransformer())
                .setDefaultValue("&0Titulo &9- &7Bonito");

        texto.setValue("&9Awe");

        texto.setDefaultValue("&0Titulo &9- &7Bonito");

        assertEquals(texto.getValue(), "§9Awe");

        config.getSerializers().registerSerializer(new TimeSetSerializer());

        Key<TimeSet> myKey = config.createKey(TimeSet.class, config.getPath("TEMPO"))
                .setDefaultValue(new TimeSet(5, 6, 1));


        assertEquals("TimeSet[H=5, M=6, S=1]", myKey.getValue().toString());


        Key<List<TimeSet>> timeSetList = config.createKey(new TypeParameterProvider<List<TimeSet>>() {
        }.createTypeInfo(), config.getPath("TODOS_TEMPOS"))
                .setDefaultValue(Arrays.asList(new TimeSet(1, 5, 9), new TimeSet(7, 6, 5)));

        assertEquals("[TimeSet[H=1, M=5, S=9], TimeSet[H=7, M=6, S=5]]", timeSetList.getValue().toString());

        Map<String, TimeSet> myMap = MapUtils.mapOf("Inicio", new TimeSet(1, 50, 0),
                "Fim", new TimeSet(10, 40, 0));

        Key<Map<String, TimeSet>> tempos = config.createKey(new TypeParameterProvider<Map<String, TimeSet>>() {
        }.createTypeInfo(), config.getPath("TMP_I"))
                .setDefaultValue(myMap);


        assertEquals("{Inicio=TimeSet[H=1, M=50, S=0], Fim=TimeSet[H=10, M=40, S=0]}", tempos.getValue().toString());


        Key<String> stringKey = config.createKey(String.class, config.getPath("Mensagem"))
                .setDefaultValue("Olá $nome");

        ConvertKey<JString, String> nome = stringKey.createConvertKey(new Converter<String, JString>() {
            @Override
            public JString convert(String value, Object[] params) {
                return JString.of(value, params);
            }

            @Override
            public String revertConversion(JString converted, Object[] params) {
                return converted.getOriginal();
            }
        });

        JString convertedValue = nome.getConvertedValue(new Object[]{"nome", "Joao"});

        assertEquals("Olá Joao", convertedValue.toString());


        try {

            YamlBackend yamlBackend = new YamlBackend(new File("src/test/resources/myyaml.yml"));

            Config<ID> config1 = new Config<>(yamlBackend);

            config1.getSerializers().registerSerializer(new TimeSetSerializer());

            Key<String> key = config1.createKey(String.class, config1.getPath("key"));

            System.out.println(key.getValue());

            Key<Map<String, TimeSet>> tempos0 = config1.createKey(new TypeParameterProvider<Map<String, TimeSet>>() {
            }.createTypeInfo(), config.getPath("TMP_I"))
                    .setDefaultValue(myMap);

            System.out.println(tempos0.getValue());

            Map<TimeSet, TimeSet> myMap2 = MapUtils.mapOf(new TimeSet(0, 0, 0), new TimeSet(1, 50, 0),
                    new TimeSet(1, 50, 0), new TimeSet(10, 40, 0));

            Key<Map<TimeSet, TimeSet>> tempos1 = config1.createKey(new TypeParameterProvider<Map<TimeSet, TimeSet>>() {
            }.createTypeInfo(), config.getPath("TMP0"))
                    .setDefaultValue(myMap2);

            Key<TypeInfo<?>> gen = config1.createKey(new TypeParameterProvider<TypeInfo<?>>() {}.createTypeInfo(), config.getPath("09"))
                    .setDefaultValue(new TypeParameterProvider<List<String>>() {}.createTypeInfo());



            System.out.println(tempos1.getValue());

            System.out.println(gen.getValue());

            yamlBackend.save();

        } catch (FileNotFoundException e) {
            throw new RethrowException(e);
        }

    }


    enum ID {
        NOME
    }

    static class TimeSet {
        private final int hour;
        private final int minutes;
        private final int seconds;

        TimeSet(int hour, int minutes, int seconds) {
            this.hour = hour;
            this.minutes = minutes;
            this.seconds = seconds;
        }

        public TimeSet() {
            this(0, 0, 0);
        }

        public int getHour() {
            return hour;
        }

        public int getMinutes() {
            return minutes;
        }

        public int getSeconds() {
            return seconds;
        }

        @Override
        public String toString() {
            return "TimeSet[H=" + getHour() + ", M=" + getMinutes() + ", S=" + getSeconds() + "]";
        }
    }

    class TimeSetSerializer implements Serializer<TimeSet> {

        @Override
        public void serialize(TimeSet value, Node node, TypeInfo<?> representation) {
            node.getNode("H").setValue(value.getHour(), int.class);
            node.getNode("M").setValue(value.getMinutes(), int.class);
            node.getNode("S").setValue(value.getSeconds(), int.class);
        }

        @Override
        public TimeSet deserialize(Node node, TypeInfo<?> representation) {
            int hour = node.getNode("H").getValue(int.class);
            int minutes = node.getNode("M").getValue(int.class);
            int seconds = node.getNode("S").getValue(int.class);


            return new TimeSet(hour, minutes, seconds);
        }
    }

    class ColorTransformer implements Transformer<String> {

        @Override
        public String transform(String input) {
            return input.replaceAll("&([0-9a-zA-Z])", "§$1");
        }

        @Override
        public String revertTransformation(String input) {
            return input.replaceAll("§([0-9a-zA-Z])", "&$1");
        }
    }
}
