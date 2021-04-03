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
package com.github.jonathanxd.config.yaml;

import com.github.jonathanxd.config.common.MapBackend;

import com.github.jonathanxd.iutils.exception.RethrowException;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.function.Supplier;

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

    private final Supplier<InputStream> inputStreamSupplier;
    private final Supplier<OutputStream> outputStreamSupplier;
    private static final Yaml yaml = new Yaml(DUMPER_OPTIONS);

    public YamlBackend(File file) throws FileNotFoundException {
        this(() -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RethrowException(e, e.getCause());
            }
        }, () -> {
            try {
                return new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                throw new RethrowException(e, e.getCause());
            }
        });
    }

    public YamlBackend(Supplier<InputStream> inputStreamSupplier, Supplier<OutputStream> outputStreamSupplier) {
        super(load(inputStreamSupplier.get(), yaml));
        this.inputStreamSupplier = inputStreamSupplier;
        this.outputStreamSupplier = outputStreamSupplier;
    }

    private static LinkedHashMap load(InputStream inputStream, Yaml yaml) {

        Object load = yaml.load(inputStream);

        if(load == null)
            return new LinkedHashMap();

        if (!(load instanceof LinkedHashMap)) {
            throw new IllegalArgumentException("Cannot parse YAML. Object = '"+load+"'!");
        }

        return (LinkedHashMap) load;
    }

    @Override
    public void save() {
        try {

            OutputStream outputStream = outputStreamSupplier.get();

            YamlBackend.yaml.dump(this.map, new OutputStreamWriter(outputStream));

            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            throw new RethrowException(e);
        }
    }

    @Override
    public void reload() {
        this.map.clear();
        this.map.putAll(load(inputStreamSupplier.get(), yaml));
    }
}
