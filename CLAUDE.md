# RealGrid IE Addon: Project Guide

This is the central project guide for humans and coding agents. The source code and Gradle configuration are authoritative when this document becomes stale.

## Project at a glance

RealGrid is a **Minecraft 1.12.2 Forge** mod (mod ID: `realgrid`) that adds realistic electrical-grid components for **Immersive Engineering**. It is built with GregTechCEu Buildscripts and RetroFuturaGradle.

- Java package: `com.micatechnologies.realgrid`
- Main mod class: `RealGrid.java`
- Build artifact base name: `real-grid-ie`
- Required runtime dependency: Immersive Engineering `0.12+`
- Source target: JVM 8; modern Java syntax is enabled through Jabel
- Test platform: JUnit 5 is enabled, though gameplay/wire behavior also needs in-game validation.

## Build and run

Use a modern JDK for Gradle. Java 21+ is the established local/CI baseline; the build configuration also supports the modern Java 17 run tasks.

```powershell
# First setup, or after a clean that removes the workspace
.\gradlew.bat setupDecompWorkspace

# Compile, test, and package
.\gradlew.bat build
.\gradlew.bat test

# Development Minecraft instances
.\gradlew.bat runClient
.\gradlew.bat runServer

# Maintenance
.\gradlew.bat clean
.\gradlew.bat updateBuildScript
```

Set `JAVA_HOME` to a suitable JDK if Gradle cannot find one. Do not hand-edit the auto-managed `build.gradle`; change mod settings in `buildscript.properties`, dependencies in `dependencies.gradle`, repositories in `repositories.gradle`, and optional project Gradle customizations in `addon.gradle`.

## Source layout

```
src/main/java/com/micatechnologies/realgrid/
├── RealGrid.java                    # @Mod entry point and lifecycle delegation
├── proxy/                           # Common/client setup
├── init/
│   ├── ModBlocks.java               # Static block declarations and Forge registration handlers
│   ├── RealGridRegistry.java        # Central ordered block/item registry
│   ├── ModTileEntities.java         # TE discovery and deduplicated registration
│   └── ModItems.java                # Client item-model registration
├── blocks/
│   ├── transformers/                # Six transformer variants and shared TE/block bases
│   ├── insulators/                  # Forty-nine insulator variants and shared geometry/base TE
│   ├── cutoffs/                     # Six cutoff-switch variants and shared TE/block base
│   ├── lightmounts/                 # Pole light mounts: one block per arm length, TE extends the insulator base
│   └── bankmounts/                  # Decorative transformer bank frames (LADWP/SCE 3-bank, 2-bank), no TE
├── items/                           # Generic ItemBlock wrapper
└── util/BoundsUtil.java             # Horizontal rotation of bounds and wire offsets
```

Assets are under `src/main/resources/assets/realgrid/`. Models are authored facing south for transformers and are rotated through blockstate/model handling; insulator side/dead-end geometry is defined as north-facing then rotated by `BoundsUtil`.

## Registration and lifecycle

`RealGrid` delegates Forge lifecycle events to the sided proxy. During `CommonProxy.preInit()`:

1. `ModBlocks.ensureLoaded()` forces static block construction.
2. Each block base constructor registers itself with `RealGridRegistry`, which also creates its `ItemBlockBase`.
3. `ModTileEntities.register()` iterates that registry and registers each unique TE type.
4. Forge registry events register the collected blocks/items, and the client-only model event registers item models.

There are currently **73 registered blocks**: **6 transformers**, **49 insulators**, **6 cutoff switches**, **8 pole light mounts** plus their item-less tip block, and **3 transformer bank mounts**. Do not maintain a second manual registration list; add a block through the established base-class/self-registration pattern.

## Immersive Engineering wire integration

The major behavioral contract of the mod is implemented in three shared tile-entity bases, all extending IE's `TileEntityImmersiveConnectable`.

### Transformers

`TileEntityRealTransformer` represents two-block-tall, master/dummy multiblocks.

- Connector `0`: invisible top-centre MV/LV relay; accepts multiple connections of exactly one type, copper or electrum.
- Connectors `1` and `2`: HV bushings; steel only. One-wire variants have one bushing/one connection; two-wire variants have two.
- A wire goes to a connector by its type, wherever the transformer is clicked: steel to a bushing, copper or electrum to the relay (`connectorFor`). `getTargetedConnector(TargetingInfo)` maps click location and horizontal facing to a connector; it picks between two bushings and tells the wire cutters which wires to cut, falling back to the other attached type when nothing is attached at the clicked point.
- `getConnectionOffset(Connection)` must agree with the physical model. Per-variant bushing offsets/heights are transformed from the model's authored south-facing coordinate system.
- With two HV bushings, the renderer assigns wires to the nearer bushing based on remote endpoints, keeping the pair uncrossed where possible.
- Cable state is reconstructed from IE's live connection set after placement/load/removal to avoid stale slot/type state from world edits, copying, or duplicate callbacks.

### Insulators

`TileEntityInsulatorBase` is an energy relay accepting copper, electrum, or steel. It permits multiple wires but only a single wire type per insulator at a time.

`InsulatorGeometry` provides each leaf TE's bounding box and attachment point. The five geometry presets are `VISE_TOP`, `F_NECK`, `POST_TOP`, `SIDE_MOUNT`, and `DEAD_END`; the rotating presets use `BoundsUtil`. The base TE reconstructs both wire count and `limitType` from IE's live connection set, including a live fallback in `getCableLimiter` so IE wire cutters continue to work when saved state was stale.

### Cutoff switches

`TileEntityCutoffSwitch` is a relay/switch shared by six visual block variants.

- It accepts up to three attached wires and intentionally allows mixed LV/MV/HV types.
- The switch state controls whether energy may pass; redstone transitions and hammer inversion are handled by the TE.
- Redstone output is limited to the facing axis to avoid the switch reading its own output as input.
- Its present rendered connection offset is the block centre (`0.5, 0.5, 0.5`).

### Pole light mounts

`BlockPoleLightMount` is a street-light arm hung from the side of a pole, one block per arm length (1 to 8). Its state is `facing` (the way the arm reaches; the pole is behind it), `swing` (`straight`, `left` or `right`: the arm turns up to 45 degrees at the bracket, which stays flat on the pole) and `wired` (actual state only, switches to the models with a slack loop from the tip insulator into the fixture). Clicking a pole's side hangs the mount on that side and swings the arm towards the player; the Engineer's Hammer cycles the swing.

- `TileEntityPoleLightMount` extends `TileEntityInsulatorBase` and takes Steel Cable (`STRUCTURE_STEEL`) or LV copper, one type at a time. Its wire point is on the insulator at the tip of the arm, next to the fixture; wire point and bounds follow the block state (length, swing, facing), not the saved `facing` field.
- The tip is clickable through `BlockPoleLightMountTip`, an invisible, item-less block in the block holding the insulator, whose box covers only the insulator and which has no collision. Its tile only forwards `getConnectionMaster` to the mount, so every wire lives on the mount. The mount refuses coils clicked on its bracket (IE's `offset` is zero there) and adds the tip and fixture blocks to `getIgnored` so IE's wire raytrace isn't blocked by them. The mount places its tip in `onBlockAdded` (undoing a player's placement when the spot is taken), removes it on break, and moves it on a hammer swing; swinging is refused while wired. Breaking the tip breaks the mount.
- The arm and fixture overhang other blocks; only the bracket has a box. The fixture is a separate block placed where the arm ends, one block above the mount: the arm tip runs into the arm socket on the back face of CSM's street-light fixtures, which sit in the bottom of their block and face only the four sides, so a swung arm curves round to meet the socket head-on.
- The models are generated by `scripts/gen_pole_light_mounts.py`, which documents their coordinate system and where each arm ends; edit the script and rerun it rather than the `.obj` files. The blockstates list every facing/swing/wired combination, because the wired models differ per swing. The script also writes `PoleLightMountTips.java` (wire point, tip block, tip box and fixture block per length and swing), which `PoleLightMountGeometry` rotates to each facing, so the wire point always lands on the drawn insulator.
- All lengths share one TE class, so `getCacheData` includes the block: IE's wire-model cache keys on property values and cache data, not the block, and would otherwise draw one length's arm for another.

### Transformer bank mounts

`BlockTransformerBankMount` is a decorative steel box frame for a bank of pole-mounted transformers, with no tile entity and no wires. It fills the block in front of the pole (the back of the pole is left for bolt covers), two blocks tall like the transformers, and faces away from the pole. Transformers stand flush against its faces the way they stand flush against a log, their back lugs at the edge of its block: the three-transformer frames (LADWP, X-braced sides; SCE, straight side arms) carry one tank on each side and one on the front, the two-transformer mount is an H-shaped bracket whose cross bars run along the tanks' centre line (a tank's back lugs are 5-11 px into its block's depth) with standoff arms back to the pole, one tank on each side. Clicking the frame's or bracket's faces places a transformer against them. Models are placeholders generated by `scripts/gen_transformer_bank_mounts.py`.

### Wire lifecycle rules

IE's wire coil calls the three-argument `canConnectCable(WireType, TargetingInfo, Vec3i)`, and `TileEntityImmersiveConnectable` implements that one from the LV/MV/HV tier alone, without calling the two-argument form. The transformer, insulator and cutoff bases each override the three-argument form to forward to their two-argument rules; a new connectable base must do the same, or its rules are never consulted.

When modifying any connectable block, preserve the full IE lifecycle: attachment checks, `connectCable`, `getCableLimiter`, `removeCable`, NBT persistence, client notification, and block-break cleanup. Block destruction must use IE's `clearAllConnectionsFor(...)` while the TE is still available so remote endpoints and client wire rendering are also cleaned up.

## Wire-attachment testing checklist

For MCMCP or manual gameplay testing, validate every affected variant in all four horizontal facings:

1. Click each visible attachment point and each intended invisible relay region; verify the selected connector and accepted/rejected wire types.
2. Confirm the rendered wire ends precisely on the model's insulator/bushing rather than the block centre or an unrotated position.
3. Test occupancy limits, same-type relay rules, and mixed-type rejection/acceptance as described above.
4. Attach, cut, and reattach wires; break the block; reload the chunk/world; and verify no ghost wires or stale cable limiter remains.
5. For two-bushing transformers, attach two wires from asymmetric endpoints and confirm they take separate, non-crossing bushings.

Treat the model JSON and the relevant `getConnectionOffset`/geometry values as a coupled change: modifying one normally requires validating the other in-game.

## Coding conventions and practical notes

- Follow the existing Java style and use shared bases rather than duplicating IE integration in leaf variants.
- Keep package and asset namespaces `com.micatechnologies.realgrid` and `realgrid`; older documentation that uses `realgridaddon` is obsolete.
- Preserve client/server separation: model registration is client-only; world/network mutations must not run on the client.
- `Tags` is generated by the buildscript; do not add or manually maintain it.
- Check `git status` before editing. Preserve unrelated work in a dirty tree.
