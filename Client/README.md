# ScapeEmulator 592 Client

This repository contains the build-592 game client binaries, web launcher, and
the bytecode bundler that adapts the client to the ScapeEmulator server.

## Layout

- `bundler/data` contains the original client, loader, unpacker, and native inputs.
- `bundler/src` contains the bytecode transformations and bundler.
- `www` contains the web launchers and generated files served by the game server on port 8081.
- `.tools/lib` holds downloaded build dependencies; `bundler/target` holds compiled output.
- Local `legacy` folders contain unused input archives and IDE metadata. They are
  ignored by Git and unused by the build; deletion was blocked by the execution
  policy, so they may be removed manually.

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

After building, run the camera input smoke test from `Client` in PowerShell:

```powershell
New-Item -ItemType Directory -Force bundler/target/test-classes | Out-Null
javac -d bundler/target/test-classes bundler/src/test/java/net/scapeemulator/asm/bundler/MiddleMouseCameraTest.java
java '-Xverify:all' '-Djava.awt.headless=true' -cp bundler/target/test-classes net.scapeemulator.asm.bundler.MiddleMouseCameraTest www/runescape.jar
```

## Run

Hold the middle mouse button and drag to rotate the camera horizontally or
vertically. Rotation uses reduced sensitivity and eases out briefly after a drag
for smoother movement; the normal camera pitch limits still apply.

Start `Server\run.bat` first, then launch the client:

```bat
run.bat
```

The launcher checks that the client is built and the local server is available,
then opens the signed loader at `http://127.0.0.1:8081/index.html` with a
32-bit Java 8 applet viewer. The loader installs the game and graphics libraries
into the client cache. This build contains only 32-bit Windows native libraries,
so a 64-bit Java runtime cannot run HD mode. Modern browsers no longer support
Java applets; install a 32-bit Java 8 JDK containing `appletviewer.exe`. The
launcher also applies `client.policy` for local cache and native-library access.
