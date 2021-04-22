# Backend

Config itself does not implement any configuration format, instead, it backs the configuration loading and saving logic to a backend.

Currently, the following backend implementations are officially supported:

- Config-Jackson (Using [fasterxml.jackson](https://github.com/FasterXML/jackson))
- Config-XML (Using [fasterxml.jackson-xml](https://github.com/FasterXML/jackson-dataformat-xml))
- Config-Json (Using [JSON.simple](https://github.com/fangyidong/json-simple))
- Config-Yaml (Using [snakeyaml](https://bitbucket.org/asomov/snakeyaml/) YAML 1.1 compliant)
- Config-Yaml-1.2 (Using [snakeyaml-engine](https://bitbucket.org/asomov/snakeyaml-engine/) YAML 1.2 compliant)
- Config-Toml (Using [tomlj](https://github.com/tomlj/tomlj) 1.0.0-rc.1 compliant. With an experimental renderer, as of the time the implementation was written, there is no official TOML writer for Java).

You could choose one of these backends to load and save your configuration. Also, you are free to write your own Backend implementation, as they are very simple. Config works solely with Java `Map`, `List`, `String` and primitive types.

Also, Config is not a real-time configuration editor, it will not save the configuration for every update that occurs in the Config object, you need to manually save and load using `Config.save` and `Config.load`. If you need a real-time configuration editor with reload capabilities, you could easily write a File Watcher or Scheduler to reload configuration using those methods.

## Toml Backend

The Toml backend is able to read any TOML configuration file compliant to 1.0.0-rc.1, however, for writing Toml config, it uses an experimental renderer as there is no recent and maintained Toml library for Java which allows Toml rendering.

This means that Toml files saved by TOML backend ends up being different from the original, and is not rendered in the best way it could be following the Toml standards.

## YAML 1.2 Backend

There is a Yaml 1.2 backend which uses snakeyaml-engine to read and write yaml files, however, since `Config` is fully map-oriented, yaml files which does not have any keys, like this one:

```yaml
- First
- Second
- Third
```

Are loaded normally, but an intermediate section is created, named `.`. This section allows loading those values normally, and rendering them is made through a special logic which keeps this structure (as long as there is no new keys defined).

Also, `Config` calls the `Backend` to resolve the root key, then the Yaml backend resolve the `.` as default key, thus allowing to load those values seamlessly without careing about the intermediate section:

```java
public class ConfigLoader {
    public static Config loadYaml() {
        // Yaml loading logic...
        Config config = new Config(new YamlBackend(...));
        TypeInfo<List<String>> stringListTypeInfo = TypeInfo.builderOf(List.class).of(String.class).buildGeneric();
        Key<List<String>> values = config.getRootKey().getAs(stringListTypeInfo);
    }
}
```

It is important to know this, because creating a configuration with a key named `.` with a List value will trigger this behavior. But only for Yaml 1.2 backend.

### Maps inside the List

When there is a map inside the list, like this:

```yaml
- First
- Second
- Third
- Somedata: value
```

Config will be able to handle this situation, but with limited capabilities. It is able to resolve the `somedata` key and change its value:

```java
public class ConfigLoader {
    public static Config loadYaml() {
        // Yaml loading logic...
        Config config = new Config(new YamlBackend(...));
        Key<List<Object>> values = config.getRootKey().getAs(CommonTypes.LIST_OF_OBJECT);
        Key<String> somedata = values.getKey("somedata", String.class);
        somedata.setValue("newValue");
    }
}
```

It is able to create new maps inside the list when needed:

```java
public class ConfigLoader {
    public static Config loadYaml() {
        // Yaml loading logic...
        Config config = new Config(new YamlBackend(...));
        Key<List<Object>> values = config.getRootKey().getAs(CommonTypes.LIST_OF_OBJECT);
        Key<String> somedata = values.getKey("somedata2", String.class); // new map is created to handle this key
        somedata.setValue("newValue2"); 
    }
}
```

And access and change values in a specific index of a list (which must exists):

```java
public class ConfigLoader {
    public static Config loadYaml() {
        // Yaml loading logic...
        Config config = new Config(new YamlBackend(...));
        Key<List<Object>> values = config.getRootKey().getAs(CommonTypes.LIST_OF_OBJECT);
        Key<String> index0 = IndexKey.forKeyAndIndex(values, 0);
        index0.setValue("newValue2"); 
    }
}
```

However, not all features that Yaml 1.2 supports was tested.