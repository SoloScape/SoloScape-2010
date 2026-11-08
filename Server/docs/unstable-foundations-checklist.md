# Unstable Foundations implementation checklist

Restore the build-592 Unstable Foundations tutorial from new-character creation through the exit into Lumbridge and the advisor/objectives handoff.

The cellar spawn and first-login character-creation code are implemented. Thirteen focused Java tests pass, covering persistence, the appearance packet, and creation-screen state and selections. The client-side creation screen still requires a live in-game acceptance check, and the Unstable Foundations quest itself is not yet implemented. Checked items describe code/cache validation, not a completed playthrough. Earlier Tutorial Island documents are retained as historical planning notes.

## New character and cellar setup

- [x] Spawn new accounts at `(3680, 4960, 0)` in the tutorial cellar
- [x] Confirm the cellar landscape and a clear spawn tile in the local cache
- [x] Update the existing fresh-character/save regression and pass all 10 serializer tests
- [x] Open build-592 character creation for new accounts; block movement and closing before confirmation
- [x] Handle gender, model categories, style choices, colours and presets with client-cache enums
- [x] Persist the accepted appearance and required/confirmed flag; resume unfinished creation and preserve older accounts
- [x] Validate appearance kit encoding and run all 13 focused Java tests
- [ ] Verify the creation screen's real client clicks, Confirm, restart/reconnect and HUD transition in-game
- [ ] Initialize a dedicated Unstable Foundations quest state; remove dependence on the old island completion state
- [ ] Give ordinary new accounts the intended rights and starter state
- [ ] Preserve returning characters' saved position, progress, equipment, and appearance
- [ ] Verify all NPC, item, scenery, animation, interface, sound, and script IDs against build 592
- [ ] Provide the correct personal quest scene so another player cannot advance or reset this player's tutorial

## Sir Vant and the opening scene

- [ ] Spawn Sir Vant, the squire, dragon, and goblin in their appropriate stages
- [ ] Implement introduction dialogue and replay-safe dialogue checkpoints
- [ ] Show the dragon attack and squire death cutscene
- [ ] Restore player camera and controls after the scene
- [ ] Add objective text, flashing controls, and target hints for each action
- [ ] Match dialogue, voice lines, music, and effects to the available 2010 assets

## Equipment and goblin combat

- [ ] Allow the player to collect the squire's equipment
- [ ] Teach inventory, equipping items, and the equipment panel
- [ ] Implement the tutorial goblin fight and count the correct player's kill
- [ ] Handle damage, experience, death prevention, and interrupted fights
- [ ] Advance to Sir Vant's next instructions once the fight is complete
- [ ] Verify the historical equipment and drops instead of importing another tutorial's loadout

## Quest journal and navigation

- [ ] Introduce the quest list and Unstable Foundations journal
- [ ] Update journal objectives as steps finish
- [ ] Introduce the cellar world map and compass
- [ ] Advance on the required interface actions and restore the correct guidance after closing them

## Drugging the dragon

- [ ] Search Sir Vant's bag for drugged meat
- [ ] Reject the high-wall crossing and introduce the plank
- [ ] Take the plank and use it to create a low-wall ramp
- [ ] Support ramp traversal with correct pathing and animations
- [ ] Warn and move the player away from the dragon's dangerous approach
- [ ] Place the meat in the bowl and return to safety
- [ ] Run the feeding/sleeping scene and update the dragon's state
- [ ] Prevent repeated interactions from consuming extra items or replaying a finished scene

## Preparing the pillars

- [ ] Retrieve cloth, oil, fuses, and tinderboxes from the bag
- [ ] Make oily strips by supporting both valid oiling/ripping orders
- [ ] Wrap both wooden pillars and attach their fuses
- [ ] Track each pillar independently and show its changed appearance
- [ ] Give Sir Vant a tinderbox
- [ ] Allow ignition only after all preparation is complete
- [ ] Recover missing supplies without allowing duplicate quest rewards

## Defeating the dragon

- [ ] Coordinate Sir Vant's ignition with the player's pillar
- [ ] Teach running to the safe area
- [ ] Delay the roof collapse until the player is safe
- [ ] Play the collapse and dragon defeat scene
- [ ] Change the cellar scenery and remove obsolete actors
- [ ] Restore camera, movement, interfaces, and a resumable quest checkpoint

## Roddeck and the advisor

- [ ] Have Roddeck enter through the cupboard and deliver his dialogue
- [ ] Introduce the advisor button and its help interface
- [ ] Return borrowed equipment to Sir Vant using the verified historical sequence
- [ ] Offer Roddeck's mining or woodcutting escape choice
- [ ] Persist the selected route and its required tool
- [ ] Supply the bronze pickaxe or bronze hatchet as appropriate

## Escape route: mining

- [ ] Require level 2 for the exit blockage
- [ ] Introduce the skills panel and setting a Mining objective
- [ ] Provide the practice rocks and correct mining action
- [ ] Award the verified tutorial experience and detect the target level
- [ ] Clear the blockage and allow passage
- [ ] Handle missing tools and interrupted mining

## Escape route: woodcutting

- [ ] Require level 2 for the exit roots
- [ ] Introduce the skills panel and setting a Woodcutting objective
- [ ] Provide the practice roots and correct woodcutting action
- [ ] Award the verified tutorial experience and detect the target level
- [ ] Clear the roots and allow passage
- [ ] Handle missing tools and interrupted woodcutting

## Leaving the cellar and quest completion

- [ ] Implement passage traversal, final Roddeck dialogue, and ladder exit
- [ ] Verify the exit house, destination tile, and NPC handoff against build 592
- [ ] Reconcile borrowed tools, equipment, and remaining quest supplies
- [ ] Mark the quest complete and update its journal and quest-point total
- [ ] Apply verified route-specific experience rewards once
- [ ] Implement the appropriate completion interface and music transitions
- [ ] Apply any starter money/items at their verified reward or task checkpoint
- [ ] Restore normal gameplay interfaces and remove quest-scene restrictions

## Surface handoff and normal gameplay

- [ ] Implement any final Sir Vant and Roddeck dialogue for the chosen 2010 flow
- [ ] Introduce normal objectives and advisor help outside the quest
- [ ] Verify the first post-tutorial objective and any associated coin reward
- [ ] Allow the player to leave the starting house and explore Lumbridge
- [ ] Prevent quest replay and duplicate rewards while retaining advisor access

## Persistence, recovery, and acceptance

- [ ] Save and restore each step, route choice, actor/scenery state, and pending reward
- [ ] Recover cleanly from logout or restart during combat, dialogue, traversal, and cutscenes
- [ ] Handle full inventories, dropped supplies, cancelled actions, and repeated clicks
- [ ] Reject out-of-order interactions and actions targeting another player's quest scene
- [ ] Verify fixed and resizable interfaces, camera restoration, and hint cleanup
- [ ] Complete a fresh-account mining-route playthrough
- [ ] Complete a fresh-account woodcutting-route playthrough
- [ ] Verify existing characters and completed accounts retain their normal gameplay
- [ ] Confirm quest rewards, tools, and tutorial items cannot be duplicated across reconnects

## Historical references and verification notes

Use [Unstable Foundations](https://runescape.wiki/w/Unstable_Foundations) for the quest sequence and the [period illustrated walkthrough](https://easyrsguide.yolasite.com/quest-guide.php) for equipment, journal/navigation, advisor, and surface-handoff details. [Advisors and Objectives](https://runescape.wiki/w/Update:Advisors_and_Objectives) provides release context.

Target the 2009–2010 quest, with the dragon-and-pillars sequence. Verify details against build-592 assets before implementation. References disagree about practice-action counts and experience; confirm the correct values rather than hard-coding either account. Separate quest-completion rewards from post-quest task rewards. Do not copy the earlier Learning the Ropes three-headed-dragon/ranged-tutor sequence.
