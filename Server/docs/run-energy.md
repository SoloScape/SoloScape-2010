# Run energy (build 592)

This implements normal running and passive recovery for the post-June-2009
era. It is a **reconstruction**, not a verified copy of Jagex's 2010 server
formula. Jagex's announcement confirms that Agility improved both running
distance and recovery, but does not disclose the equations.

## Sources and reconstruction

- [Jagex's 9 June 2009 announcement, preserved copy](https://runescape.fandom.com/wiki/Update:Run_energy_upgrade)
  establishes that Agility improves both drain and recharge, particularly at
  levels 50–99. Modern OSRS and post-March-2013 RuneScape rates do not describe
  this version.
- [Archived Jagex transportation manual](https://www.2011.rs/kb/transportation_introduction)
  describes the run orb, Ctrl-click, Agility recovery and carried-weight drain.
- [2011 player testing and reconstruction](https://rune-server.org/threads/pi-run-energy-perfect-formula-toggling.339759/)
  reports `2260 - 10 * Agility` milliseconds per recovered percentage point.
  We use that recovery interval, retaining fractions each tick rather than
  rounding the interval to a whole tick. This is community evidence, not an
  official equation.
- The [preserved pre-2013 energy reference](https://wiki.darkan.org/Energy)
  reports approximately 62 seconds of unladen running at low Agility and 95
  seconds at 99. We linearly interpolate **duration** between those anchors.
  The intermediate drain curve is our approximation; the reference does not
  provide an exact per-level equation.

For current Agility `A`, clamped to 1–99, and a 600 ms tick:

```
running duration (seconds) = 62 + (A - 1) * 33 / 98
drain (% per tick)         = 60 / running duration
recovery (% per tick)      = 600 / (2260 - 10 * A)
```

| Agility | Unladen run duration | Passive recovery from 0 to 100 |
| --- | --- | --- |
| 1 | ~62 s | ~225 s |
| 50 | ~78.5 s | ~176 s |
| 99 | ~95 s | ~127 s |

Times are rounded to the next game tick. Energy is stored with fractional
precision; the orb receives whole percentages when they change. Only a tick
with an actual second movement step drains energy. Walking and standing
recover it, even with the run toggle enabled. Empty energy disables both the
orb toggle and the current forced-run route. Ctrl-click cannot bypass empty
energy. The orb can be enabled again after at least 1% has recovered.

## Limits

The server has no item-weight definitions or carried-weight calculation.
These rates therefore represent an **unladen player at every equipment load**.
Equipment penalties and weight-reducing clothing require reliable build-592
item weights before this can reproduce loaded running. Musicians,
special drain modifiers and energy persistence are outside this change.
The existing player serializer continues to determine login state.

## Resting

The orb's second menu option (interface 750, child 1) starts normal resting.
The build-592 cache contains sitting sequence 5713 and stand-up sequence 5748;
its script 1741 recognises varp 173 value 3 as normal resting. Rest stops the
current route and plays the sitting sequence. Movement, teleports, other
animations/actions, run toggles and incoming hits interrupt it. The previous
run preference is restored on cancellation, subject to available energy.
Rest cannot start during a blocking action, death, attacking or the existing
combat system's recent-attack window. Full energy is capped at 100%; the
player can remain seated until interrupted.

[Contemporary testing on 9 June 2009](https://forum.tip.it/topic/234949-rest-and-resting-spots/)
reports approximately 35 seconds to restore 0–100 energy while resting.
[Firsthand reports from 1 January 2010](https://runescape.wiki/w/Talk:Rest)
describe roughly 3.75 percentage points per tick at level 94 and suggest
4 at level 99. Those posts explicitly call their formulas theories.

We reconstruct the intermediate levels by linearly interpolating full-recovery
**duration**, from 35 seconds at level 1 to 15 seconds at level 99:

```
resting duration (seconds) = 35 - (clamp(Agility, 1, 99) - 1) * 20 / 98
rest recovery (% / tick)   = 60 / resting duration
```

This is the total recovery while resting, replacing ordinary recovery rather
than stacking with it. At Agility 1, 50 and 99, recovery is approximately
1.714%, 2.4% and 4% per 600 ms tick: 59, 42 and 25 ticks respectively
(35.4, 25.2 and 15 seconds) from empty to full. The exact original server
equation is unverified; this is a reconstruction consistent with those reports.
Resting here restores run energy only; additional life-point regeneration and
musician interactions are not implemented.
