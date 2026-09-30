# Create Encased (Fly Port)

Unofficial Fabric / Minecraft 26.2 port of [Create Encased](https://github.com/iglee42/CreateCasing)
by iglee42, built on ZurrTum's [Create Fly](https://modrinth.com/mod/create-fly).
Not made, maintained or supported by iglee42: report problems with this port here, not upstream.

Create's machines, shafts, cogwheels and fluid blocks in every casing:

- **Casings**: andesite, brass, copper, railway, shadow steel, refined radiance, industrial iron,
  weathered iron, plus new creative and zinc casings. Each set brings its own encased shafts and
  cogwheels, gearboxes, presses, mixers, depots, chain drives, chain conveyors, gearshifts,
  clutches, deployers, fans, harvesters, saws, drills, ploughs, rollers, portable storage
  interfaces and belt casings (as far as upstream made them for that casing).
- **Shafts and cogwheels** in every wood type, brass, copper, zinc, andesite, glass and blackstone,
  and each of them encased in every casing.
- **Fluid blocks** in andesite, brass and zinc: pipes, glass pipes, pumps, smart pipes, valves,
  valve handles, tanks, hose pulleys, item drains, portable fluid interfaces, steam engines,
  whistles and spouts.
- **New blocks**: configurable gearbox, automatic clutch, creative cogwheel.
- Right-click a machine with another casing to swap its casing, or a shaft or cogwheel with a
  material to swap its material (both can be turned off in the config).

743 blocks in all. Every variant works like the Create block it is made from: same stress,
same contraption behaviour, same pipes and transfer, same JEI categories and ponder scenes.

## Requirements

Minecraft 26.2, Fabric Loader 0.19.3+, Fabric API, Create Fly 6.0.9-1+. JEI is optional.

Upstream's KubeJS and Slice and Dice / Farmer's Delight integrations are not included: those mods
have no Fabric 26.2 release.

## Config

`config/createcasing/common.json`: casing and material swapping, the configurable gearbox's shaft
rules, breaking wooden and glass shafts, and per-block stress values (used when
`encasedBlocksUsesOwnKeys` is on; otherwise every block uses its Create counterpart's value).

## Building

```
./gradlew build
```

JDK 25 is required (Gradle picks it up through `gradle/gradle-daemon-jvm.properties`).
`./gradlew runClientGameTest` runs the in-game checks (see `PORTING.md`).

## Licence

MIT, as upstream. Copyright (c) 2025 iglee42; see `LICENSE`.
