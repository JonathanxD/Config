/*
 *      Config-Yaml-1.2 - Yaml backend for Config <https://github.com/JonathanxD/Config/>
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
package com.github.jonathanxd.config.backend.yaml1_2.test;

import com.github.jonathanxd.config.CommonTypes;
import com.github.jonathanxd.config.Config;
import com.github.jonathanxd.config.IndexKey;
import com.github.jonathanxd.config.Key;
import com.github.jonathanxd.config.backend.ConfigIO;
import com.github.jonathanxd.config.backend.yaml1_2.YamlBackend;
import com.github.jonathanxd.iutils.box.IMutableBox;
import com.github.jonathanxd.iutils.box.MutableBox;
import com.github.jonathanxd.iutils.collection.Collections3;
import com.github.jonathanxd.iutils.type.TypeInfo;
import org.junit.Assert;
import org.junit.Test;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.FlowStyle;

import java.util.List;
import java.util.Map;

public class YamlBackendTest {

    @Test
    public void testToString() {
        IMutableBox<String> box = new MutableBox<>();

        LoadSettings loadSettings = LoadSettings.builder().build();

        DumpSettings dumperOptions = DumpSettings.builder()
                .setDefaultFlowStyle(FlowStyle.BLOCK)
                .setMultiLineFlow(true)
                .build();

        YamlBackend yamlBackend = new YamlBackend(
                new Load(loadSettings),
                new Dump(dumperOptions),
                ConfigIO.stringBox(box)
        );

        Config config = new Config(yamlBackend);
        Key<String> key = config.getRootKey().getKey("backend", String.class);
        key.setValue("Yaml backend");
        config.save();

        String first = box.get();

        box.set("backend: Yaml backend Uhu");

        config.load();

        Assert.assertEquals("backend: Yaml backend\n", first);
        Assert.assertEquals("Yaml backend Uhu", key.getValue());

    }

    @Test
    public void testToStringUnsupported() {
        IMutableBox<String> box = new MutableBox<>("- First\n- Second\n- Third\n- addr: \"0.0.0.0\"");

        LoadSettings loadSettings = LoadSettings.builder().build();

        DumpSettings dumperOptions = DumpSettings.builder()
                .setDefaultFlowStyle(FlowStyle.BLOCK)
                .setMultiLineFlow(true)
                .build();

        YamlBackend yamlBackend = new YamlBackend(
                new Load(loadSettings),
                new Dump(dumperOptions),
                ConfigIO.stringBox(box)
        );

        Config config = new Config(yamlBackend);
        config.load();

        Key<String> addr = config.getRootKey().getAs(CommonTypes.LIST_OF_OBJECT)
                .getKey("addr", String.class);

        String addrStr = addr.getValue();

        Key<String> key = config.getRootKey().getAs(CommonTypes.LIST_OF_OBJECT)
                .getKey("backend", String.class);

        key.setValue("Yaml backend");
        config.save();

        String first = box.get();

        box.set("- backend: Yaml backend Uhu\n- A");

        config.load();

        Assert.assertEquals("- First\n" +
                "- Second\n" +
                "- Third\n" +
                "- addr: 0.0.0.0\n" +
                "- backend: Yaml backend\n", first);
        Assert.assertEquals("0.0.0.0", addrStr);
        Assert.assertEquals("Yaml backend Uhu", key.getValue());

    }

    @Test
    public void stringListTestToStringUnsupported() {
        IMutableBox<String> box = new MutableBox<>("- First\n- Second\n- Third");

        LoadSettings loadSettings = LoadSettings.builder().build();

        DumpSettings dumperOptions = DumpSettings.builder()
                .setDefaultFlowStyle(FlowStyle.BLOCK)
                .setMultiLineFlow(true)
                .build();

        YamlBackend yamlBackend = new YamlBackend(
                new Load(loadSettings),
                new Dump(dumperOptions),
                ConfigIO.stringBox(box)
        );

        Config config = new Config(yamlBackend);
        config.load();
        Key<List<String>> key = config.getRootKey().getAs(CommonTypes.LIST_OF_STRING);

        Assert.assertEquals(Collections3.listOf("First", "Second", "Third"), key.getValue());

    }

    @Test
    public void stringListIndexTestToStringUnsupported() {
        IMutableBox<String> box = new MutableBox<>("- First\n- Second\n- Third");

        LoadSettings loadSettings = LoadSettings.builder().build();

        DumpSettings dumperOptions = DumpSettings.builder()
                .setDefaultFlowStyle(FlowStyle.BLOCK)
                .setMultiLineFlow(true)
                .build();

        YamlBackend yamlBackend = new YamlBackend(
                new Load(loadSettings),
                new Dump(dumperOptions),
                ConfigIO.stringBox(box)
        );

        Config config = new Config(yamlBackend);
        config.load();
        Key<List<String>> key = config.getRootKey().getAs(CommonTypes.LIST_OF_STRING);
        Key<String> v = IndexKey.forKeyAndIndex(key, 0);

        Assert.assertEquals(Collections3.listOf("First", "Second", "Third"), key.getValue());
        Assert.assertEquals("First", v.getValue());
        v.setValue("Ffirst");
        Assert.assertEquals("Ffirst", v.getValue());
        Assert.assertEquals(Collections3.listOf("Ffirst", "Second", "Third"), key.getValue());

    }

}
