# Runtime cache

Place `main_file_cache.dat2` and every accompanying `main_file_cache.idx*`
directly in `cache/`. Both the game and login server read this directory.
Keep the entire cache together; do not mix files from different downloads.

Landscape keys remain in `../game/data/landscape-keys/`, one `<region>.txt`
file per region containing four signed integers, one per line. Keys must match
the installed cache. The server does not read JSON key files directly.

Cache binaries are local runtime data and are ignored by Git. The cache
library's Java sources are in `../cache/src/`.
