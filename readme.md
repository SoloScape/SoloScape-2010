# SoloScape 2010

A RuneScape build-592 server and matching client, based on ScapeEmulator.

## Cache setup

Download `cache.zip` from [GitHub Releases](https://github.com/SoloScape/SoloScape-2010/releases)
and extract the cache files into `Server/cache` before starting the server:

```text
Server/
  cache/
    main_file_cache.dat2
    main_file_cache.idx0
    ...
    main_file_cache.idx255
    src/
    pom.xml
```

The `main_file_cache.*` files must be directly inside `Server/cache`, alongside
the existing cache library source. Avoid an extra nested `cache` folder.
Cache data is ignored by Git and distributed through Releases; Git LFS is not
required for the cache.

## Build and run on Windows

1. Install a Java 8 JDK and make `java` and `javac` available on `PATH`.
2. Download and extract the cache as described above.
3. Run `Server/build.bat` and `Client/build.bat`.
4. Start `Server/run.bat`, then `Client/run.bat`.

The client launcher requires a **32-bit Java 8 JDK** with `appletviewer.exe`.
See [Server setup](Server/README) and [Client setup](Client/README.md) for details.
