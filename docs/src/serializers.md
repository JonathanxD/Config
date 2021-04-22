# Serializers

Config also supports serializers. This is very important for the library as it only works with `Maps`, `Lists`, `Strings` and primitive types (different backends could provide support for others types, but the official ones does not).

Serializers are registered and provided through `Serializers` class, custom serializers must implement `Serializer` interface and be registered in `Config.getSerializers`.