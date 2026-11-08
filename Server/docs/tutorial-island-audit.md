# Tutorial Island source audit

Historical island audit. The active onboarding plan is now the [Unstable Foundations checklist](unstable-foundations-checklist.md).

Audited on 8 November 2026 against branch `Feature/Tutorial-Island`. This is a source-code and historical-reference audit, not an in-game acceptance test. It changes the implementation checklist only.

Subsequent spawn change: new accounts now start at `(3680, 4960, 0)` in the Unstable Foundations tutorial cellar. The local build-592 landscape includes Sir Vant's bag, the cupboard door, ladders, rocks, and roots in this region. This moves the spawn only; the quest, dialogue, NPCs, and cutscenes remain unimplemented. The table below records the earlier island audit.

## Historical target

RuneScape replaced Tutorial Island with Unstable Foundations on 17 September 2009. Consequently, a literal live-2010 tutorial would involve Sir Vant beneath Lumbridge, rather than the nine island instructors. This project deliberately retains the requested island route on its build-592 client. Use the pre-removal island flow, checking visual assets and protocol details against the local cache. [Historical quest reference](https://runescape.wiki/w/Unstable_Foundations), [island development history](https://runescape.wiki/w/Tutorial_Island).

Modern OSRS and modern RuneScape guides contain later changes. They must not silently define this restoration's requirements. In particular, remove the OSRS poll-booth lesson, retain the Financial Advisor, and avoid later account-mode onboarding. The OSRS change history records the addition of polling guidance after the original island era. [OSRS island change history](https://oldschool.runescape.wiki/w/Tutorial_Island).

## What exists in this repository

Paths below are relative to `Server/game/src/main/java/net/scapeemulator/game/` unless otherwise stated.

| Area | Observed evidence | Checklist implication |
| --- | --- | --- |
| Registration | `Launcher.java` registers `TutorialIsland`; `content/tutorial/TutorialIsland.java` only registers a persistent varp and coarse string states. | Plugin registration exists; the instructor sequence still needs implementation. |
| New characters | `io/DummyPlayerSerializer.java` sets position `(3094, 3107)`, assigns rights 2 and sample shop stock, and calls `setVar(406, 22)` before `defaults()`. | Arrival position exists, but ordinary tutorial account initialization needs work. Sample items are shop stock, not backpack items. |
| Starting state | `TutorialIsland.java` defaults to `tutorial_complete` (index 22). `model/player/var/Status.defaults()` overwrites pending variables with registered defaults. | Both the explicit index 22 assignment and the subsequent default select completion. New accounts do not enter character creation. |
| State granularity | `TutorialIsland.java` contains 23 values, including two identical `spacer` strings; `PVariable.getIndex()` uses `List.indexOf()`. | Add durable individual checkpoints. The two spacer states cannot be distinguished by string lookup. Preserve existing saved numeric meanings or migrate them explicitly. |
| Guidance | `TutorialIslandProgress.java` opens interface 371 above chat. `TutorialTrigger.java` has an empty progression switch and opens normal tabs only for completed accounts. | Interface scaffolding exists; lesson text, hints, tab unlocks, and action-driven progress do not. |
| NPCs | `cache/MapDataDecoder.decodeSpawns()` is empty and its caller in `model/map/WorldMap.java` is commented out. No production NPC creation/registration was found. | Explicitly implement and verify island inhabitants and fishing spots. Cache landscape objects alone do not provide working NPCs. |
| Interactions | `msg/entity/ObjectClickMessageHandler.java` resolves a region but performs no object action. `msg/Messages.java` binds NPC option two for attacks; no NPC talk-to decoder or tutorial dialogue handlers were found. | Doors, ladders, resource interactions, and instructor conversations are prerequisites. |
| Skilling | The tutorial content directory contains only the three scaffolding classes. No woodcutting, firemaking, fishing, cooking, mining, or smithing lesson handlers were found. | Every practical skill lesson remains unchecked. Skill constants and interfaces alone do not complete a lesson. |
| Combat | `model/player/action/AttackNpcAction.java` approaches an adjacent reachable tile before assigning a combat target. Generic combat and equipment code exists. | Reuse suitable core behaviour, but implement tutorial restrictions and distance-based ranged/magic interactions. Existing NPC attack handling does not establish the ranged lesson. |
| Saves | `Server/game/data/serializer.conf` selects the file serializer. `io/FilePlayerSerializer.java` creates accounts through `DummyPlayerSerializer`, saves persistent varps, and restores them after defaults. | General persistence exists. Verify actual tutorial checkpoints and UI restoration rather than treating saving as wholly absent. |
| Tests | `Server/game/src/test/java/.../io/FilePlayerSerializerTest.java` covers general character round trips, including a synthetic persistent varp. No tutorial-specific tests were found. | Existing save tests do not establish that any instructor step resumes correctly. |

No instructor is marked complete: the source establishes scaffolding, and no island playthrough was performed during this audit.

## Corrections to the original checklist

- Specify actual bread ingredients: a pot of flour and a bucket of water, with the empty containers handled after mixing.
- Keep music before the kitchen exit, then emotes and running on the route to the Quest Guide.
- Keep banking and financial advice as separate checkpoints. Viewing and closing the bank advances the lesson; a forced deposit or withdrawal adds an unnecessary completion condition.
- Include Brother Brace's explanation of prayer drain, altars, and bones. Prayer experience is not a mandatory gate.
- Supply five air and five mind runes for the magic demonstration. Advance on a successful Wind Strike hit, without requiring a chicken kill.
- Add missing departure loadout reconciliation, safe tutorial combat, training limits, recovery paths, and protection of existing saves.

These lesson corrections use the [archived island walkthrough](https://en.wikibooks.org/wiki/Old_School_RuneScape/Guides/Tutorial) and the [community-preserved dialogue and instruction transcript](https://rune-server.org/threads/tutorial-island-dialogue-interfaces.397139/). The latter is supporting historical evidence, not proof of build-592 packet or component IDs.

## Details still requiring verification

- Exact build-592 NPC/object/item IDs, spawn coordinates, interface children, hint packets, scripts, and tab-flash controls. Do not copy IDs from another revision without checking the cache.
- Whether varp 406 is sufficient for the intended progress presentation; keep the detailed server checkpoint distinct from the progress indicator. The current string enumeration is not proof of the client's expected values.
- Exact final backpack, equipment, bank contents, and treatment of surplus tutorial items. Sources describe different arrow quantities during instruction and after departure; verify rather than retaining everything or granting an assumed reward set.
- Exact level-3 experience cap, skills excluded from training, tutorial communication restrictions, and any late-2008/2009 dialogue differences.
- Whether the chosen historical version includes explicit report-abuse instruction. Preserve supported conduct dialogue without adding an unverified compulsory action.

The [implementation checklist](tutorial-island-checklist.md) leaves these as explicit verification tasks. Completion requires a fresh-account playthrough and interrupted-session recovery checks on the actual client.
