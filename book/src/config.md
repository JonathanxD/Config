## Config

Config is a fully map-based `Storage` implementation, it uses a `LinkedHashMap` as storage medium, while checks for supported value types using `Backend.supports`. Thus, every value stored in this class is directly stored in the wrapped `LinkedHashMap`, and when configuration need to be saved to file, it just sends an unmodifiable copy of this `map` to the `backend` (it must be a copy and unmodifiable to avoid concurrency issues).

Also, Config is not Thread-safe by default, this means that to concurrently write values, you need to implement a locking logic to allow only one modification at a time. We plan to provide a concurrent capable implementation in the future.
