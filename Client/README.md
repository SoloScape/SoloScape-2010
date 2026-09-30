# ScapeEmulator 592 Client

This repository contains the build-592 game client binaries, web launcher, and
the bytecode bundler that adapts the client to the ScapeEmulator server.

## Layout

- `asm/data` contains the original client, loader, unpacker, and native inputs.
- `asm/src` contains the bytecode transformations and bundler.
- `www` contains the files served by the game server on port 8080.

## Build

Use JDK 8 because the client pipeline depends on Pack200, which was removed
from newer Java releases.

```bat
build.bat
```

The bundler writes the patched client, loader, Pack200 files, and native
libraries to `../www`. The `Server\run.bat` launcher uses that directory by default. Set
`SCAPE_CLIENT_DIR` to an alternate `www` directory if needed.

The RSA modulus in `RsaTransformer` is public client configuration and must
match the server's key pair if the keys are regenerated.

## Run

Start `Server\run.bat` first, then launch the client:

```bat
run.bat
```

The launcher checks that the client is built and the local server is available,
then opens the signed loader at `http://127.0.0.1:8080/index.html` with a
32-bit Java 8 applet viewer. The loader installs the game and graphics libraries
into the client cache. This build contains only 32-bit Windows native libraries,
so a 64-bit Java runtime cannot run HD mode. Modern browsers no longer support
Java applets; install a 32-bit Java 8 JDK containing `appletviewer.exe`. The
launcher also applies `client.policy` for local cache and native-library access.
