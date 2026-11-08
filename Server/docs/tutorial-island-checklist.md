# Tutorial Island implementation checklist

Track the complete Tutorial Island implementation in tutorial order. Check items off once implemented and verified in game.

## RuneScape Guide
- [ ] New-player arrival and character creation
- [ ] Introduction dialogue and repeat dialogue for each tutorial stage
- [ ] Camera controls and movement instructions
- [ ] Settings tab introduction and flashing tab guidance
- [ ] Tutorial progress bar, instruction text, and hint arrows
- [ ] Door interaction and progression to the Survival Expert

## Survival Expert
- [ ] Dialogue and starter tools (hatchet, tinderbox, and small fishing net)
- [ ] Inventory tab introduction
- [ ] Woodcutting: chop a tree and receive logs
- [ ] Firemaking: use the tinderbox on logs to light a fire
- [ ] Skills tab introduction and skill experience updates
- [ ] Fishing: catch raw shrimps with the small fishing net
- [ ] Cooking fish: cook shrimps on a fire, including the first burnt attempt and a successful retry
- [ ] Replacement tools and recovery if required items are lost
- [ ] Gate interaction and progression to the Master Chef

## Master Chef
- [ ] Dialogue and cooking ingredients (flour and water)
- [ ] Make bread dough by combining flour and water
- [ ] Bake bread dough on the range
- [ ] Replacement ingredients and recovery after failed or interrupted actions
- [ ] Music tab introduction
- [ ] Emotes tab introduction and perform an emote
- [ ] Running controls and run energy introduction
- [ ] Door and path progression to the Quest Guide

## Quest Guide
- [ ] Introduction and quest explanation dialogue
- [ ] Quest tab introduction and flashing tab guidance
- [ ] Follow-up dialogue after opening the quest tab
- [ ] Ladder interaction and progression into the mining area

## Mining Instructor
- [ ] Dialogue and starter tools (pickaxe and hammer)
- [ ] Prospect copper and tin rocks
- [ ] Mine copper ore and tin ore
- [ ] Smelt copper and tin into a bronze bar at the furnace
- [ ] Open the anvil smithing interface
- [ ] Smith a bronze dagger
- [ ] Replacement tools, ore, and recovery after interrupted actions
- [ ] Gate interaction and progression to the Combat Instructor

## Combat Instructor
- [ ] Dialogue and starter melee equipment
- [ ] Equipment tab and equipment statistics interface introduction
- [ ] Equip the bronze dagger
- [ ] Receive and equip the bronze sword and wooden shield
- [ ] Combat tab and attack style introduction
- [ ] Rat enclosure gate and melee combat instructions
- [ ] Kill a giant rat with melee and advance the tutorial
- [ ] Receive and equip the shortbow and bronze arrows
- [ ] Kill a giant rat with ranged from outside the enclosure
- [ ] Combat restrictions, rat respawns, and safe recovery if combat is interrupted
- [ ] Replacement weapons and ammunition
- [ ] Ladder interaction and progression to the bank

## Financial Advisor
- [ ] Banker dialogue and bank booth interaction
- [ ] Banking interface introduction with item deposit and withdrawal
- [ ] Poll booth interaction and explanation
- [ ] Financial Advisor dialogue explaining money and earning methods
- [ ] Door interaction and progression to Brother Brace

## Brother Brace
- [ ] Introduction and prayer explanation dialogue
- [ ] Prayer tab introduction and flashing tab guidance
- [ ] Friends list introduction
- [ ] Ignore list introduction
- [ ] Dialogue explaining chat, player conduct, and reporting abuse
- [ ] Door interaction and progression to the Magic Instructor

## Magic Instructor
- [ ] Introduction and magic explanation dialogue
- [ ] Magic tab introduction and flashing tab guidance
- [ ] Supply air runes and mind runes
- [ ] Cast Wind Strike on a chicken
- [ ] Replacement runes and recovery after interrupted casting
- [ ] Final dialogue and confirmation to leave Tutorial Island
- [ ] Teleport to Lumbridge and mark the tutorial complete
- [ ] Remove tutorial guidance, restore normal interfaces, and prevent tutorial re-entry

## End-to-end checks
- [ ] Save and restore the exact tutorial step after logout, reconnect, or server restart
- [ ] Gate dialogue, objects, tabs, and actions by the current tutorial step
- [ ] Prevent skipped steps and duplicate starter item rewards
- [ ] Verify NPCs, objects, item IDs, animations, and locations against the build-592 cache
- [ ] Verify tutorial instructions and interfaces in fixed and resizable display modes
- [ ] Complete the full island on a fresh account, including item-loss recovery and reconnects
- [ ] Confirm existing characters retain normal gameplay and completed characters remain complete
