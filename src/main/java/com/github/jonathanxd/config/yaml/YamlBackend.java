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
package com.github.jonathanxd.config.yaml;

import com.github.jonathanxd.config.common.MapBackend;
import com.github.jonathanxd.iutils.exceptions.RethrowException;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;

/**
 * Created by jonathan on 25/06/16.
 */
public class YamlBackend extends MapBackend {


    private static final DumperOptions DUMPER_OPTIONS;

    static {
        DUMPER_OPTIONS = new DumperOptions();
        DUMPER_OPTIONS.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
        DUMPER_OPTIONS.setIndent(2);
        DUMPER_OPTIONS.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    }

    private final File file;
    private static final Yaml yaml = new Yaml(DUMPER_OPTIONS);

    public YamlBackend(File file) throws FileNotFoundException {
        super(load(file, yaml));
        this.file = file;
    }

    private static LinkedHashMap load(File file, Yaml yaml) throws FileNotFoundException {

        Object load = yaml.load(new FileInputStream(file));

        if(load == null)
            return new LinkedHashMap();

        if (!(load instanceof LinkedHashMap)) {
            throw new IllegalArgumentException("Cannot parse YAML. File '" + file + "' -> '"+load+"'!");
        }

        return (LinkedHashMap) load;
    }

    @Override
    public void save() {
        try {
            Files.write(file.toPath(), yaml.dump(this.map).getBytes(Charset.forName("UTF-8")), StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RethrowException(e);
        }
    }

    @Override
    public void reload() {
        try {
            this.map.clear();
            this.map.putAll(load(file, yaml));
        } catch (FileNotFoundException e) {
            throw new RethrowException(e);
        }
    }
}
