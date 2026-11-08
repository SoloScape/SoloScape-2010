# Tutorial Island implementation checklist

Track the complete Tutorial Island implementation in tutorial order. Check items off once implemented and verified in game.

Target: the original nine-instructor Tutorial Island, adapted to this server's build-592 client and cache. Live RuneScape used Unstable Foundations in 2010, after replacing the island on 17 September 2009; this is an intentional restoration of the older island, not a recreation of the live 2010 starting quest. See the [source audit](tutorial-island-audit.md) for evidence, references, and unresolved historical details.

The audit found starting-position, state, interface, and save-system scaffolding, but no working instructor sequence. None of the gameplay items below has been verified in game.

## Prerequisites

- [ ] Initialize genuinely new accounts at character creation; replace the current varp 406 value 22 and completion default used by new-character setup
- [ ] Preserve existing account progress and completed accounts when introducing the new starting state
- [ ] Use normal player rights and the tutorial starter loadout for ordinary new accounts; keep development stock out of the normal onboarding flow
- [ ] Add persistent steps for individual actions and dialogue checkpoints, separate from the coarse progress-bar stages
- [ ] Verify varp 406 values, tutorial interfaces, tab controls, and scripts against build 592 before wiring progression
- [ ] Spawn all nine instructors, bankers, fishing spots, giant rats, and chickens at cache-verified positions
- [ ] Implement NPC talk-to, dialogue continuation and choices, item-on-item, item-on-object, and spell-on-NPC interactions
- [ ] Implement validated object interactions, approach paths, doors, gates, ladders, and collision changes
- [ ] Unlock tabs in tutorial order and restore the correct tabs, instructions, and hints on login at every step

## RuneScape Guide

- [ ] New-player arrival and character creation
- [ ] Introduction dialogue and repeat dialogue for each tutorial stage
- [ ] Camera controls and movement instructions
- [ ] Settings tab introduction and flashing tab guidance
- [ ] Tutorial progress bar, instruction panels, flashing tabs, and hint arrows that follow the current step
- [ ] Door interaction and progression to the Survival Expert

## Survival Expert

- [ ] Dialogue and staged tool handouts (bronze hatchet and tinderbox first; small fishing net at the fishing lesson)
- [ ] Inventory tab introduction
- [ ] Woodcutting: chop a tree and receive logs
- [ ] Firemaking: use the tinderbox on logs to light a fire
- [ ] Skills tab introduction and skill experience updates
- [ ] Fishing: catch raw shrimps with the small fishing net
- [ ] Cooking fish: cook shrimps on a fire, including the first burnt attempt and a successful retry
- [ ] Replacement tools and recovery if required items are lost
- [ ] Gate interaction, minimap navigation guidance, and progression to the Master Chef

## Master Chef

- [ ] Dialogue and cooking ingredients (pot of flour and bucket of water)
- [ ] Make bread dough by combining flour and water
- [ ] Bake bread dough on the range
- [ ] Replacement ingredients and recovery after failed or interrupted actions
- [ ] Music tab introduction
- [ ] Emotes tab introduction and perform an emote
- [ ] Running controls and run energy introduction
- [ ] Require the music lesson before the kitchen exit; teach emotes and running on the path to the Quest Guide
- [ ] Door and path progression to the Quest Guide

## Quest Guide

- [ ] Introduction and quest explanation dialogue
- [ ] Quest tab introduction and flashing tab guidance
- [ ] Follow-up dialogue explaining unstarted, in-progress, and completed quest colours after opening the quest tab
- [ ] Ladder interaction and progression into the mining area

## Mining Instructor

- [ ] Dialogue and staged tool handouts (bronze pickaxe for mining; hammer for smithing)
- [ ] Prospect copper and tin rocks
- [ ] Mine copper ore and tin ore
- [ ] Smelt copper and tin into a bronze bar at the furnace
- [ ] Open the anvil smithing interface
- [ ] Smith a bronze dagger using the bar and hammer; restrict the tutorial smithing selection to the dagger
- [ ] Replacement tools, ore, and recovery after interrupted actions
- [ ] Gate interaction and progression to the Combat Instructor

## Combat Instructor

- [ ] Introduction dialogue using the bronze dagger already made in the mining lesson
- [ ] Equipment tab and equipment statistics interface introduction
- [ ] Equip the bronze dagger
- [ ] Receive and equip the bronze sword and wooden shield
- [ ] Combat tab and attack style introduction
- [ ] Rat enclosure gate and melee combat instructions
- [ ] Kill a giant rat with melee and advance the tutorial
- [ ] Receive and equip the shortbow and bronze arrows
- [ ] Kill a giant rat with ranged from outside the enclosure; support attack distance and line of sight rather than melee-only approach logic
- [ ] Combat restrictions, rat respawns, damage and experience, and safe recovery if combat is interrupted
- [ ] Eating cooked shrimps and tutorial protection against player death
- [ ] Replacement weapons and ammunition
- [ ] Ladder interaction and progression to the bank

## Financial Advisor

- [ ] Banker dialogue and bank booth interaction
- [ ] Open the bank through a booth or banker, show the bank explanation, then advance when the player closes the bank
- [ ] Support deposits and withdrawals without making either a compulsory tutorial step
- [ ] Door from the banking area to the Financial Advisor; omit the later OSRS poll-booth lesson
- [ ] Financial Advisor dialogue explaining combat drops, quest rewards, and selling gathered or crafted items
- [ ] Door interaction and progression to Brother Brace

## Brother Brace

- [ ] Introduction and prayer explanation dialogue
- [ ] Prayer tab introduction and flashing tab guidance
- [ ] Explain prayer activation, point drain, altar restoration, and burying bones; do not require prayer experience to progress
- [ ] Friends list introduction
- [ ] Ignore list introduction
- [ ] Follow-up dialogue explaining the lists, online status, and player conduct; verify any report-abuse guidance against the chosen historical transcript
- [ ] Door interaction and progression to the Magic Instructor

## Magic Instructor

- [ ] Introduction and magic explanation dialogue
- [ ] Magic tab introduction and flashing tab guidance
- [ ] Supply five air runes and five mind runes for the lesson
- [ ] Manually cast Wind Strike on a caged chicken from outside its enclosure, with rune consumption, animation, projectile, and hit resolution
- [ ] Advance after a successful spell hit; do not add a chicken-kill requirement
- [ ] Replacement runes and recovery after interrupted casting
- [ ] Final dialogue and confirmation to leave Tutorial Island
- [ ] Teleport to Lumbridge and mark the tutorial complete
- [ ] Verify and implement the historical departure inventory, equipment, bank balance, and rune/arrow quantities; reconcile tutorial items without duplication or item smuggling
- [ ] Remove tutorial guidance, restore normal interfaces, and prevent tutorial re-entry

## End-to-end checks

- [ ] Save and restore the exact tutorial step after logout, reconnect, or server restart
- [ ] Gate dialogue, objects, tabs, and actions by the current tutorial step
- [ ] Prevent skipped steps and duplicate starter item rewards
- [ ] Handle full inventories, dropped or banked tools, expired fires, exhausted ammunition, and missing ingredients without trapping the player
- [ ] Apply the historical level-3 training cap to eligible tutorial skills; confirm exact experience limits and exceptions before implementation
- [ ] Verify optional activities such as burying bones and altar use without adding compulsory steps
- [ ] Verify tutorial chat, trade, and area restrictions against the chosen historical behaviour
- [ ] Verify NPCs, objects, item IDs, animations, and locations against the build-592 cache
- [ ] Verify tutorial instructions and interfaces in fixed and resizable display modes
- [ ] Complete the full island on a fresh account, including item-loss recovery and reconnects
- [ ] Confirm existing characters retain normal gameplay and completed characters remain complete
