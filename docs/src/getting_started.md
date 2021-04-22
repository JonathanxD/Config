# Getting Started

Config and official backends are available through [jitpack.io](https://jitpack.io/#JonathanxD/Config).

### Gradle (Kotlin)

```kotlin
val config_version by project

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.JonathanxD.Config:Config:$config_version")
    implementation("com.github.JonathanxD.Config:Config-Toml:$config_version") // Toml backend
}
```

## Reading and writing values

Given the following Toml config:

```toml
[server]
addr = "0.0.0.0"
port = 8080
```

You could write something like this to read and write values:

```java
public class ServerConfig {
    private final Config config;
    private final Key<String> addr;
    private final Key<Long> port;
    
    public ServerConfig(Path configPath) {
        ConfigIO io = ConfigIO.path(configPath, StandardCharsets.UTF_8);
        Backend backend = new TomlBackend(io);
        this.config = new Config(backend);
        this.config.load(); // Config file must exists
        
        Key<Void> serverSection = this.config.getRootKey().getKeySection("server"); 
        this.addr = serverSection.getKey("addr", CommonTypes.STRING);
        this.port = serverSection.getKey("port", CommonTypes.LONG);
    }
    
    public String getAddress() {
        return this.addr.getValue();
    }
    
    public long getPort() {
        return this.port.getValue();
    }
    
    public void setPort(long port) {
        this.port.setValue(port);
    }
    
    public void save() {
        this.config.save();
    }
}
```

## Serialization

Config already implements serializers for basic types and date types, and some JwIUtils library types, such as `Text` and `TypeInfo` (you could find all [here](https://github.com/JonathanxD/Config/blob/master/src/main/java/com/github/jonathanxd/config/serializer/Serializers.java)), however, sometimes you want to work with your custom types (or 3rd party types) which does not have default serializers implemented, for this, Config provides `Serializers` class as registry base for `Serializer` implementations.

### Basic Serializer

```java
record User(String name, String email) {
    
}

public class UserSerializer implements Serializer<User> {
    @Override
    public void serialize(User value,
                          Key<User> key,
                          TypeInfo<?> typeInfo,
                          Storage storage,
                          Serializers serializers) {
        key.getKey("name", String.class).setValue(value.name());
        key.getKey("email", String.class).setValue(value.email());
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
```

Also, sometimes you just want to store a single value, for this you could use `Key.getAs` to transform the Key type:

```java
record User(String name) {
    
}

public class UserSerializer implements Serializer<User> {
    @Override
    public void serialize(User value,
                          Key<User> key,
                          TypeInfo<?> typeInfo,
                          Storage storage,
                          Serializers serializers) {
        key.getAs(String.class).setValue(value.name());
    }

    @Override
    public User deserialize(Key<User> key,
                              TypeInfo<?> typeInfo,
                              Storage storage,
                              Serializers serializers) {

        return new User(key.getAs(String.class).getValue());
    }
}
```

### Calling other serializers

Config automatically invoke other serializers to proceed with the serialization when a value is not supported by the backend, so the code below will work correctly and serialize the `LocalDate` as well.

```java
record User(LocalDate registrationDate, String name) {
    
}

public class UserSerializer implements Serializer<User> {
    @Override
    public void serialize(User value,
                          Key<User> key,
                          TypeInfo<?> typeInfo,
                          Storage storage,
                          Serializers serializers) {
        key.getKey("registrationDate", LocalDate.class).setValue(value.email());
        key.getKey("name", String.class).setValue(value.name());
    }

    @Override
    public User deserialize(Key<User> key,
                            TypeInfo<?> typeInfo,
                            Storage storage,
                            Serializers serializers) {

        LocalDate registrationDate = key.getKey("registrationDate", LocalDate.class).getValue();
        String name = key.getKey("name", String.class).getValue();

        return new User(registrationDate, name);
    }
}
```

However, when working with complex types which need an intermediate storage, this is not enough. For these cases, you could use the `Serializers` provided to `serialize` and `deserialize` methods. See below a hardcore version of `serialize` which does the same thing as the version above, however using intermediate storage:

```java
record User(LocalDate registrationDate, String name) {

}

public class UserSerializer implements Serializer<User> {
    
    @Override
    public void serialize(User value, Key<User> key, TypeInfo<?> typeInfo, Storage storage, Serializers serializers) {
        Map<Object, Object> newMap = new LinkedHashMap<>();
        Map<String, Object> temp = new LinkedHashMap<>();

        Storage newStorage = Storage.createMapStorage(key, temp);

        {
            Key<LocalDate> regDateKey = key.getAs("registrationDate", LocalDate.class, newStorage);
            Object date = serializers.serializeUncheckedAndGet(value.getRegistrationDate(), regDateKey);
            temp.clear();
            newMap.put("registrationDate", date);
        }

        {
            Key<String> nameKey = key.getAs("name", String.class, newStorage);
            Object name = serializers.serializeUncheckedAndGet(value.getName(), nameKey);
            temp.clear();
            newMap.put("name", name);
        }

        storage.pushValue(key, newMap);
    }

    @Override
    public User deserialize(Key<User> key,
                            TypeInfo<?> typeInfo,
                            Storage storage,
                            Serializers serializers) {

        LocalDate registrationDate = key.getKey("registrationDate", LocalDate.class).getValue();
        String name = key.getKey("name", String.class).getValue();

        return new User(registrationDate, name);
    }
}
```

This approach is used by `ListSerializer` and `MapSerializer` to correctly serialize values in a controlled `Storage` medium. When you call `serializers.serialize*` with this intermediate storage, instead of serializing the value inside the `Storage` provided to `serialize` method, it does by serializing the value inside the provided `Storage` medium, thus serializing the value inside a controlled context, which will not overwrite or modify the values already stored.

With this, introspecting the values and changing the structure of them is more safe, as it will not damage the main storage medium (commonly the `Config` storage), and at in the end of serialization process, you can push to the main storage only the values you care about.