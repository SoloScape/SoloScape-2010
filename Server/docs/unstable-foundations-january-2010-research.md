# January 2010 new-account tutorial: Unstable Foundations

## Which tutorial actually existed?

The default new-account introduction in **January 2010** was **Unstable Foundations** (also called Roddeck's Cellars), not the traditional Tutorial Island starting house. Unstable Foundations replaced Tutorial Island on **17 September 2009**, and was removed in May 2011. The cellar is underneath Roddeck's house in Lumbridge.

Important date boundary: RuneScape changed its account-creation process and Unstable Foundations dungeon on **24 March 2010**. Later footage may show the redesigned cellar or login process and should not automatically be treated as a January 2010 reference. An 11 January 2010 patch added dragon stomping sounds; the quest remained available throughout that month.

## Player journey in January 2010

1. **Create character:** choose appearance (gender, hair, beard, skin, clothing and body options), confirm, and choose a character name if necessary. The opening tutorial is set in the cellar; normal gameplay tabs are introduced during the tutorial.
2. **Learn camera and movement:** the controls panel tells players that arrow keys rotate the camera, clicking on the ground or minimap walks, and Ctrl-click runs. It instructs the player to rotate the camera to begin. The centered panel shown in the project's player-supplied video reference has key and mouse illustrations; it is separate from interface 371 (Tutorial Island progress/arrow). This exact visual layout is evidenced by that reference, not by the historical walkthrough text alone.
3. **Find/talk to Sir Vant:** the White Knight introduces the threat from a dragon in the cellar. A cutscene includes the death of his squire in dragonbreath.
4. **Defeat the goblin:** a goblin rummages through the knight's pack. The player uses the fallen squire's equipment to defeat it.
5. **Drug the dragon:** the player uses a plank to reach the bowl beyond the barrier, puts drugged meat into the dragon's food bowl and retreats while the dragon eats and falls asleep.
6. **Collapse the roof:** prepare oil-soaked cloth and fuses on the pillars, give Sir Vant a tinderbox and light the other pillar; escape the collapse.
7. **Roddeck and first skill objective:** Roddeck offers a way out. Choose Mining or Woodcutting, train to level 2 with roots/rocks, clear the matching obstruction, talk to Roddeck and climb the ladder into Lumbridge.

**Do not assume individual tab unlock timings:** the archived quest walkthrough confirms tutorials and skill objectives but does not establish a precise sequence for revealing every HUD tab. Each tab's unlock should be attached to the corresponding implemented objective only when verified from historical footage, cache scripts or contemporary documentation.

## Implementation notes in this branch

* Native character setup is interface 900. The instructional overlay is custom interface 905 and must **not** reuse interface 371.
* Resizable HUD interface 746's child 8 is a **512x334 scene widget**. A **392x410** controls image is too tall for that child. Prefer 746's full-size overlay pane (child 6) when opening 905.
* After confirming setup, deliver the normal HUD root before opening the controls popup on a later player-update tick.
* Camera packet 51 contains camera angles, not a keypress. Establish a fresh post-open angle baseline and require enough visible time before counting a later change as a tutorial completion.
* The saved tutorial controls pending flag persists across reconnects; ensure failure to display the popup does not inadvertently mark it complete.
* Full login server and game server must use the same cache sprite/interface checksums.

## Sources

- Historical RuneScape Wiki: https://runescape.wiki/w/Unstable_Foundations
- Unstable Foundations walkthrough (historical Darkan copy, last edited 2012): https://wiki.darkan.org/Unstable_Foundations
- Archived RuneScape knowledge base: https://2011.rs/kb/how_do_i_get_started0
- Historical tutorial cellar: https://runescape.wiki/w/Tutorial_Cellar
- March 2010 account-creation and dungeon update: https://runescape.wiki/w/Hidden_updates/2010
- Video screenshot reference: supplied by project owner in conversation (not checked against an original Jagex asset).
