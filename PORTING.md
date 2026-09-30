# Porting notes: Create Encased → Create Fly (Fabric, MC 26.2)

## State

**Done, 2026-09-30.** Everything upstream had is ported except the integrations with mods that
have no Fabric 26.2 release (KubeJS, Slice and Dice, Farmer's Delight) and REI. All 743 blocks
register (upstream's 751 minus the 8 Slice and Dice slicers), render, and load their data.
`./gradlew runClientGameTest` passes all 24 gameplay checks and the render check (see *Testing*);
a dedicated server (`runServer`) reaches `Done` with no errors.

Still only checkable by playing: balance, every one of the 743 blocks in motion, contraptions with
the mod's drills/saws/rollers, the automatic clutch screen, belts with every casing, tooltips.

| | |
|---|---|
| Upstream | https://github.com/iglee42/CreateCasing, branch `1.21.1/main` (1.9.0-ht3, commit `f584aaab`) |
| Target | Minecraft 26.2, Fabric Loader 0.19.3, Fabric API 0.160.0+26.2 |
| Create Fly | `maven.modrinth:create-fly:26.2-rc-2-6.0.9-1` |
| Optional | JEI 30.26.0.182 (Modrinth id `x6nG9OT2`) |
| Licence | MIT: keep `LICENSE` with iglee42's copyright notice |

Mod id (`createcasing`), package (`fr.iglee42.createcasing`) and all block and item ids are
unchanged, so recipes and data packs written for upstream apply.

**Read `../create-fly-rc2-src/` for Create Fly code, not `../create-fly/`.** The latter is Create
Fly's master branch and differs a lot from the rc-2 jar this compiles against (encased cogwheel
connected textures, partial models, OBJ models). `create-fly-rc2-src` is the rc-2 jar decompiled
with Vineflower, plus its assets.

## The main design change: blocks join Create's block entity types

Upstream registered one block entity type per block kind (press, mixer, depot, tank, ...), only
because its blocks could not join Create's, and then needed mixins to make Create's renderers,
capabilities and block checks accept them. Here:

- Every block is built from its Create counterpart's class and properties
  (`Properties.ofFullCopy`) and **joins the block entity type its class returns**, through
  Fabric's `addValidBlock` (`EncasedBlockEntities.register`). A brass press is a
  `MechanicalPressBlock` in Create's `MECHANICAL_PRESS` type, so it gets Create Fly's renderer,
  visual, client behaviours, goggle tooltips and item/fluid lookups for free. 28 of
  upstream's 37 `Custom*Block` classes, which mostly only returned another type, are gone.
- **Own types remain only where Create has none or fixes the type in its constructor**:
  creative cogwheel, automatic clutch, configurable gearbox, the mod's shafts and cogwheels
  (wooden, glass, custom metal) and their encased forms, and the tanks. Tanks keep one shared type
  of their own, as upstream: multiblock tanks join by type, so andesite/brass/zinc tanks merge with
  each other but not with Create's. Copycats+ assigns Create's types immutable `Set.copyOf`
  sets, so `EncasedBlockEntities.addValidBlock` swaps a non-`HashSet` back to a mutable one
  (classtweaker makes `validBlocks` mutable). That type needs Create Fly's transfer registration repeated
  (`registerTransfer`) and `FluidTankItemMixin`.
- **`EncasedBlocks.BASE`** maps each block to its Create counterpart. `BaseBlockProviders` hangs
  providers on Create's block-keyed registries (movement and interaction behaviours, mounted
  storage, display sources, stress, capacity, generated RPM) that answer with the counterpart's
  value, looked up lazily, so neither init order nor Create's config reload matters. This replaces
  upstream's per-builder `onRegister(movementBehaviour(...))` calls and its
  `BlockStressValuesMixin` "stress keys". With `encasedBlocksUsesOwnKeys` the mod's own config
  values win, as upstream.
- **`BaseBlockChecks`** is used by the block-check mixins: wherever Create tests
  `state.is(AllBlocks.X)` (chute fans, arm interaction points, depots under presses, boilers,
  steam engines, whistle extensions, pipe rims, packagers, contraption support faces, deployer
  direction, chain conveyor connection, schematic tool, steam engine piston, fluid interface
  middle), a variant of X counts as X.

## Resources

- Registrate's generated JSONs are committed under `src/generated/resources`.
- `tools/migrate_data.py` rewrote the 258 recipes to the 26.2 / Create Fly shapes (adds filling:
  `fluid_ingredient` in droplets, single `result`, `processingTime` → `processing_time`).
  Slice and Dice files (slicer blockstates, models, loot, recipes, advancements) are deleted.
- `tools/gen_item_definitions.py` writes `assets/createcasing/items/*.json`, taking each item's
  model type (`create:model/normal`, `create:model/oversized` with its bounds) from its Create
  counterpart in the Create Fly jar.
- `tools/split_ct_sheets.py` cuts connected-texture sheets into Create Fly's per-tile sprites. It
  now handles omni, vertical/horizontal (tile i is sheet index i) and rectangle sheets, checked
  against Create's sheets and Create Fly's tiles with `--verify`. Re-run it whenever a
  `*_connected.png` changes; without the tiles blocks render as missing texture with no log line.
- `tools/check_model_refs.py` lists model parents and textures that no resource root provides
  (this mod, the Create Fly jar, vanilla). Fixed that way: whole connected sheets used as a plain
  texture are now tile `/3`, as Create Fly does; Create Fly draws the chain conveyor's bull wheel
  as part of its shaft model, so upstream's `wheel` models and partial are gone, and the conveyor
  casing texture key is `conveyor_casing`.

## Client

- `CasingSetVisuals` / `FluidSetVisuals` hold what upstream's set builders held for the client
  (sprites, belt covers, per-set partial models); the common `CasingSet` / `FluidSet` hold blocks
  only, in an `EnumMap` by `Part`.
- `EncasedModels` registers connected textures, casing connectivity, pipe attachments
  (`EncasedPipeAttachmentModel`, per fluid set), tanks and bracketed shafts, for the mod's blocks
  only. Small encased cogwheels use Create Fly's `EncasedCogCTBehaviour(shift, Couple)`.
- `EncasedPartialSwaps` swaps Create's partial models for a set's own (mixer/drill heads, roller
  frames, conveyor guard and shaft, boiler gauges, valve handles, hose magnets, fluid interface
  tops, spout bottoms, the cogwheel inside a custom encased cogwheel). `RendererPartialsMixin`
  runs it on every `CachedBuffers.partial*` call of the affected renderers and contraption
  renderers; small mixins do the same for the Flywheel visuals.
- Belts: `BeltCasingTypeMixin` adds a casing type per belt casing set and **rebuilds the enum's
  codec**, which Create Fly builds from the values in the same static initialiser (without that,
  a belt's casing would not save). `BeltModelMixin` shifts the brass casing sprite onto the set's
  texture and adds its cover, as Create Fly does for andesite.
- `ServerPlayerGameModeMixin` / `MultiPlayerGameModeMixin` let a sneaking player put a shaft on a
  configurable gearbox (upstream overrode NeoForge's `doesSneakBypassUse`).
- Ponder (`client/ponder`): Create's scenes for the variants are registered under Create's plugin
  (so schematics resolve in Create's namespace), the mod's three scenes and its tag under its own.
- JEI (`client/compat/jei`): catalysts added to Create Fly's categories; encased shaft/cogwheel
  items hidden, as upstream.

## Behaviour changed on purpose

- Pick-block on encased custom shafts/cogwheels gives the casing: 26.2 no longer passes the hit
  to `getCloneItemStack`, so the shaft-or-casing choice upstream made is not possible (Create's
  own encased shafts do the same).
- Upstream's encased cogwheel side textures for shadow steel and refined radiance pointed at
  missing files (`encased_cogwheel/shadow`, `/radiance`); the port uses the sheets that exist.
- Pipes draw rims towards the mod's encased pipes like towards Create's (upstream only patched
  the hose pulley half of that check).

## Testing

- `./gradlew runServer`: dedicated server; checks data loading and that no client class is
  reached on the server. `run/eula.txt` is accepted.
- `./gradlew runClientGameTest` (about 2 minutes) runs:
  - `GameplayCheck`: 24 `ENCASED-TEST PASS/FAIL` checks, fails the run on any failure. Rotation
    through every shaft kind, cogwheels, configurable gearbox, automatic clutch; chain drives
    joining only their casing; tank multiblocks and the steam engine; the transfer API; casing swap
    and encasing through a real `useItemOn`; a railway belt casing; stress and capacity equal to
    Create's; contraption behaviours, display source, arm point; recipes; belt casing, clutch
    settings and rotation after save and reload.
  - `RenderCheck`: every block in a grid, a driven row, close-ups of one casing set and one fluid
    set, five ponder scenes, and the JEI mixing catalysts. It asserts nothing; look at
    `build/run/clientGameTest/screenshots`.
  - The run ends with a Flywheel client-shutdown watchdog crash report after the tests: Create
    Fly's worker threads, harmless.
- A `MixerVisual` NullPointerException ("MechanicalMixerAnimationBehaviour ... is null") showed up
  in two runs where mixers were placed by command in view: Create Fly creates the visual before
  the client behaviour is attached. It is Create Fly's code path, shared by Create's own mixer.
- The camera in `RenderCheck` misses the first shot after a far teleport; shots are ordered around it.
- A cloud sync client on `D:\Documents` once renamed `build/resources/main/assets` to
  `assets (# Name clash ...)` mid-build, so the run had no assets. Delete `build/resources` and
  rebuild if ponder schematics or models go missing all at once.
