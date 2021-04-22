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

import com.github.jonathanxd.config.backend.Backend;
import com.github.jonathanxd.iutils.box.MutableBox;
import com.github.jonathanxd.iutils.text.Colors;
import com.github.jonathanxd.iutils.text.Styles;
import com.github.jonathanxd.iutils.text.Text;
import com.github.jonathanxd.iutils.text.TextComponent;
import com.github.jonathanxd.iutils.type.TypeInfo;

import org.junit.Assert;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class ColorTest {

    @Test
    public void serializationTest() {
        YamlBackend yamlBackend = new YamlBackend();
        Config config = new Config(yamlBackend);

        this.registerSerializers(config);

        // Keys
        Key<Map<Object, Object>> rootKey = config.getRootKey();
        Key<Void> messagesSection = rootKey.getKey("messages", Void.TYPE); // Void key is same as a Section
        Key<TextComponent> welcomeMessage = messagesSection.getKey("welcome", TextComponent.class);
        // /Keys

        welcomeMessage.setValue(Text.of(Colors.RED, Styles.BOLD, "Hello ", Colors.GREEN, Text.variable("user"), "."));

        config.save();

        System.out.println(yamlBackend.getYaml());

        // Read
        String yaml = yamlBackend.getYaml();

        yamlBackend.setYaml(yaml);
        config.load();

        Assert.assertEquals(Text.of(Colors.RED, Styles.BOLD, "Hello ", Colors.GREEN, Text.variable("user"), "."),
                welcomeMessage.getValue());

        yamlBackend.setYaml(
                "messages:\n" +
                        "  welcome:\n" +
                        "    componentType: Text\n" +
                        "    children:\n" +
                        "    - {componentType: Color, name: green, red: 85, green: 255, blue: 85, alpha: 1.0}\n" +
                        "    - {componentType: Style, italic: true}\n" +
                        "    - {componentType: String, text: 'Hello '}\n" +
                        "    - {componentType: Color, name: green, red: 85, green: 255, blue: 85, alpha: 1.0}\n" +
                        "    - {componentType: Variable, variable: user}\n" +
                        "    - {componentType: String, text: ', '}\n" +
                        "    - {componentType: String, text: welcome}\n" +
                        "    - {componentType: String, text: .}");
        config.load();

        Assert.assertEquals(Text.of(Colors.GREEN, Styles.ITALIC, "Hello ", Colors.GREEN,
                Text.variable("user"), ", ", "welcome", "."),
                welcomeMessage.getValue());
    }

    private void registerSerializers(Config config) {
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

        String getYaml() {
            return this.yamlStr.get();
        }

        void setYaml(String yaml) {
            this.yamlStr.set(yaml);
        }

        @Override
        public boolean supports(TypeInfo<?> type) {
            return CommonTypes.isValidBasicType(type);
        }
    }

}
