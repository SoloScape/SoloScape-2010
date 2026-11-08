# ScapeEmulator 592 Server

RuneScape build-592 server, based on ScapeEmulator by Graham and Jonathan.
The matching client and bundler are in `../Client`.

## Build and run on Windows

1. Install a Java 8 JDK with `java` and `javac` on `PATH`.
2. Extract the game cache's `main_file_cache.*` files into `data/cache/`.
3. Put matching landscape keys in `game/data/landscape-keys/`, one region per
   text file with four signed integers on separate lines.
4. Run `build.bat`. Downloaded libraries go into `.tools/lib`; compiled
   server classes and the runtime classpath go into `game/target`.
5. Build the client with `../Client/build.bat`.
6. Run `run.bat` to start the login server and game world, then run
   `../Client/run.bat` to open the client.

`run.bat` serves `../Client/www` by default. Set `SCAPE_CLIENT_DIR` to the
absolute path of another `www` directory if needed. Configuration remains in
`game/data`; the launcher creates `serializer.conf` from its example on first
run if it is missing. Launching does not compile anything.

The default file serializer saves characters locally. The client launcher needs
a 32-bit Java 8 JDK with `appletviewer.exe`.

## Character saves

The active `game/data/serializer.conf` and its example use:

```properties
type=file
directory=data/characters
```

The directory is relative to the game module, so character files live in
`Server/game/data/characters/player-<username>.properties`. These local files
are ignored by Git. Back up this directory to preserve your characters.

The first login creates the account using the entered password; subsequent
logins require that same password. Passwords are stored as bcrypt hashes.
New accounts start at Tutorial Island's starting house (`3094, 3107`, height 0).
Existing starter settings and development rights apply only when creating a
new account. Returning characters restore their backpack, equipment, bank,
personal sample stock, skill XP and levels, appearance, exact tile and height,
run energy, and persistent player settings.

Logout or disconnection saves the character before releasing the account for
another login. Normal server shutdown also saves online characters. Forcefully
terminating the Java process cannot run the shutdown hook. A damaged profile
rejects login instead of replacing the character with a fresh account.

The old `type=dummy` mode remains available for disposable development sessions;
it does not save progress. Characters from earlier dummy sessions had no save
files to import.

## Source and data layout

- `api`, `cache`, `js5`, `login`, `util`: supporting Java modules.
- `game/src/main/java`: game engine, content, interfaces, and Crosstalk client.
- `game/src/test/java`: game tests.
- `game/data`: active world configuration, equipment definitions, landscape keys.
- `data/cache`: live cache binaries, separate from cache-library source code.
- `database/schema.sql`: optional MySQL persistence schema.
- `docs`: gameplay implementation notes.
- `flooder`: optional load-testing utility, separate from the normal launcher.

Every module uses standard Maven source directories. `mvn test` builds the
server modules and runs their unit tests. The Windows batch build compiles the
runtime directly with Java 8; Maven is not required for that workflow.

Local `legacy` folders contain retired development dumps, duplicate or older
key collections, cache archives, IDE metadata, and a redundant bundled Maven
installation. They are unused and ignored by Git. They were isolated because
the execution policy blocked deletion and may be removed manually.

## Optional MySQL persistence

1. Create a MySQL database and import `database/schema.sql`.
2. Set `game/data/serializer.conf`:

   ```properties
   type=jdbc
   url=jdbc:mysql://HOST/DATABASE
   username=USERNAME
   password=PASSWORD
   ```

3. Create a user in the database with a bcrypt password hash. In-game
   registration is not implemented.
4. Launch the servers and log in with that account.

## Development utilities

Run standalone utilities from their module directory so their relative paths
resolve correctly. Cache tools use `../data/cache`; dump and comparison tools
may require their own external inputs. Generated reports are not runtime data.

`util/data/mkkeystore` generates a development signing keystore.
`net.scapeemulator.util.crypto.RsaKeyGenerator` generates server RSA keys;
update the client's `RsaTransformer` modulus when changing the server key pair,
then rebuild both sides. Keep existing signing keys and original client inputs
unless you intend to rebuild that setup.

ScapeEmulator is released under the ISC license. See `LICENSE` for copyright
information and license terms.
