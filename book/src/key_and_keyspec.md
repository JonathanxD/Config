# Key

The most important class of the Config library. A **Key** represents a pointer to a value in the configuration file, tied to a **key-path**. With this pointer you are able to read and write values directly to the `Storage` medium without even needing to hold a reference to the `Storage` itself. Also, you could have different pointers to the same value, and changing a value through one pointer will be reflected in all other pointers (they are not really reflected, they just read the value from the same path, so if the value changes, anyone pointing to this path has access to the new value).

# KeySpec

Is a specification of a `Key` name and type, with `KeySpec` you refer to a part of the key path. It is used to implement a more readable `Key` access with constant path parts.