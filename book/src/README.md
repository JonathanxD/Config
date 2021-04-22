<div style="text-align:center">

![LOGO](logo.png)
</div>

Config is an agnostic key-based configuration library, built on top of the concept of holding reference to a value in the configuration instead of holding the value itself.

# Map oriented

The library is totally map-oriented, it stores and reads values from `Map`s, also even if there is a concept of `Storage` classes, which is the medium of the data storage, and they could store data in any format, most of the common implementations uses Maps and all keys are String-only, this is intentional, and we focus only in storing data in `Map`s. 

All values are serialized upfront before being stored, in other words, when they are sent to a `Storage` medium, they are already serialized using built-in and custom serializers. Any library capable of rendering `Map`s could take advantage of `Config`.

# Key based/Pointer oriented

Config uses a pointer-oriented data access and write, this allows data changes to be reflected all across the application, and config-reloading could be simply implemented by using File Watchers.

# Agnostic

Config does not care about the final format of the configuration file, it only manages data in a `Map`. The class responsible for loading and saving this `Map` in the configuration is the `Backend` class. It also provides which types are supported to be stored in the `Map` without the serialization and deserialization process.

We provide common backend implementations for most used configuration languages, such as Json, Yaml, Toml and XML. Since all of those libraries are able to render `Maps` as Strings, they become very handful for application configuration.  

# Serialization supported

Serialization of custom types are supported through `Serializers` registry and `Serializer` implementation, and it is very easy to implement your own serializer. Also, API provides interfaces for implementing serialization of complex types.

# Type information retention

Config depends on types provided by `TypeInfo` tokens for implementing Serialization for `Map`, `List` and user defined types.

This allows generic types to be serialized correctly and using the right serializer, instead of relying on the type of the object itself. This means that types must be explicitly provided, and the information must be retained at runtime.