# SoloScape 2010

A RuneScape build-592 server and matching client, based on ScapeEmulator.

## Cache setup

Download `cache.zip` from [GitHub Releases](https://github.com/SoloScape/SoloScape-2010/releases)
and extract the cache files into `Server/data/cache` before starting the server:

```text
Server/
  data/
    cache/
      main_file_cache.dat2
      main_file_cache.idx0
      ...
      main_file_cache.idx255
```

The `main_file_cache.*` files must be directly inside `Server/data/cache`.
The separate `Server/cache` module contains the cache library's source code.
Cache data is ignored by Git and distributed through Releases; Git LFS is not
required for the cache.

## Build and run on Windows

1. Install a Java 8 JDK and make `java` and `javac` available on `PATH`.
2. Download and extract the cache as described above.
3. Run `Server/build.bat` and `Client/build.bat`.
4. Start `Server/run.bat`, then `Client/run.bat`.

The client launcher requires a **32-bit Java 8 JDK** with `appletviewer.exe`.
See [Server setup](Server/README.md) and [Client setup](Client/README.md) for details.

## Project layout

```text
Server/
  api/              Shared service interfaces
  cache/            Cache library and tools
  data/cache/       Live game cache (not tracked by Git)
  database/         Optional MySQL schema
  docs/             Gameplay implementation notes
  flooder/          Optional load-testing utility
  game/
    data/           World configuration, equipment data, landscape keys
    src/main/java/  Game engine, content, interfaces, Crosstalk client
    src/test/java/  Game tests
  js5/              Cache-update and world-list services
  login/            Crosstalk/login server
  util/             Shared utilities and key-generation tools
  build.bat         Build the server
  run.bat           Start login and game servers
Client/
  bundler/
    data/           Original client binaries, native libraries, signing key
    src/main/java/  Client transformations and packaging
    src/test/java/  Camera input smoke test
  www/              Web launchers and generated client output
  build.bat         Build and package the client
  run.bat           Launch the client
```

Each Java module uses `src/main/java` and `src/test/java`; generated classes
stay in `target`, and downloaded build libraries stay in `.tools/lib`.

Local `Server/legacy` and `Client/legacy` folders hold retired archives, dumps,
duplicate key collections, unused client inputs, and IDE metadata from the
cleanup. They are ignored by Git and unused by builds and launchers. Deletion
was blocked by the execution policy; these folders can be removed manually.
