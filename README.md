# Real Grid IE Addon

A Minecraft Forge 1.12.2 mod by **Mica Technologies** that adds realistic power grid components for [Immersive Engineering](https://www.curseforge.com/minecraft/mc-mods/immersive-engineering).

## Features

### Transformers
- **Class A Transformer (2-Wire)** -- 2 HV connections on the sides, invisible MV/LV relay point on top center
- **Class A Transformer (1-Wire)** -- 1 HV connection on the side, invisible MV/LV relay point on top center
- **Class C Transformer (2-Wire)** -- 2 HV connections on the top, invisible MV/LV relay point on top center
- **Class C Transformer (1-Wire)** -- 1 HV connection on the top, invisible MV/LV relay point on top center

All transformers are 2 blocks tall and allow energy to pass between HV and MV/LV networks. The MV/LV relay point supports multiple wire connections of the same type.

### Insulators
Wire relay components that allow energy to pass through and support multiple connections:
- **MacLean F-Neck Post Insulator** -- Distribution line post insulator
- **MacLean Dead End Insulator** -- For terminating wire runs on poles
- **Hendrix Vise Top Insulator** -- Modern distribution line insulator
- **Porcelain Post Top Insulator** -- Classic porcelain insulator (white/black variants)
- **Porcelain Dead End Insulator** -- Classic porcelain dead end insulator (white/red variants)

All insulators accept LV, MV, and HV wire connections.

### Distribution Switchgear
Based on MacLean triple action air release switches:
- Supports up to 3 wire connections (any mix of HV, MV, LV)
- Controlled exclusively by **redstone signal** (no manual toggle)
- When powered: switch opens (power stops flowing)
- When unpowered: switch closes (power flows)
- Invertible with engineer's hammer + sneak
- Provides redstone output

## Requirements
- Minecraft 1.12.2
- Minecraft Forge 14.23.5.2859+
- Immersive Engineering 0.12+

## Setup Instructions

### Prerequisites
- Java Development Kit (JDK) 17 (Azul Zulu Community recommended)
- An internet connection (for Gradle to download dependencies)
- Git (for automatic version detection from tags)

The project uses [Jabel](https://github.com/bsideup/jabel) to allow modern Java 17 syntax while targeting JVM 8. You **must** use a Java 17 JDK for development.

### Build System

This mod uses the [GregTechCEu Buildscripts](https://github.com/GregTechCEu/Buildscripts) with [RetroFuturaGradle](https://github.com/GTNewHorizons/RetroFuturaGradle) as the core Gradle plugin. The `build.gradle` is auto-managed and should **not** be edited manually. Mod-specific configuration is in `buildscript.properties`.

### Building

1. **Set up the development environment** (required first time):
   ```bash
   ./gradlew setupDecompWorkspace
   ```

2. **Build the mod**:
   ```bash
   ./gradlew build
   ```
   The built JAR will be in `build/libs/`.

3. **Run the development client**:
   ```bash
   ./gradlew runClient
   ```

4. **Run the development server**:
   ```bash
   ./gradlew runServer
   ```

### IDE Setup

**IntelliJ IDEA** (recommended): Import as a Gradle project. The buildscript automatically generates IntelliJ run configurations:
- `1. Setup Workspace` -- Decompiles Minecraft and sets up the dev environment
- `2. Run Client` -- Launches the Minecraft client with the mod loaded
- `3. Run Server` -- Launches a dedicated server with the mod loaded
- `4. Run Obfuscated Client` -- Tests with obfuscated names
- `5. Run Obfuscated Server` -- Tests server with obfuscated names
- `6. Build Jars` -- Compiles and packages the mod JAR
- `Update Buildscript` -- Updates `build.gradle` to the latest version
- `FAQ` -- Displays common troubleshooting info

**Eclipse**: Run `./gradlew eclipse` and import the project.

### Updating the Build Script

The `build.gradle` can be updated to the latest version from GregTechCEu/Buildscripts:
```bash
./gradlew updateBuildScript
```

### Key Configuration Files

| File | Purpose |
|---|---|
| `build.gradle` | Auto-managed build script (do NOT edit) |
| `buildscript.properties` | Mod configuration (name, ID, version, features) |
| `gradle.properties` | Gradle JVM settings |
| `settings.gradle` | Plugin management and Blowdryer setup |
| `dependencies.gradle` | Mod dependencies (Immersive Engineering) |
| `repositories.gradle` | Maven repositories for dependencies |

### Version

Version is derived from Git tags. To release a version, create a tag (e.g., `1.0.0`) and the build will use it automatically. If no tag exists, a development version is generated.

### CI/CD

GitHub Actions workflows are included:

- **Test Mod Build (Pull Request)** -- Compiles the mod on every pull request to catch build failures early.
- **Build Mod Release/Pre-Release (Main Branch)** -- On every push to `main`, builds the mod and publishes a GitHub pre-release with the JAR and integrity hashes. Use the manual workflow dispatch with `release: true` to create a full release.
- **Cleanup Old/Outdated Mod Pre-Release(s)** -- Automatically removes pre-releases older than 90 days, keeping at least 3 recent ones.

## Project Structure
```
RealGridIEAddon/
├── build.gradle              # Auto-managed build script (DO NOT EDIT)
├── buildscript.properties    # Mod config (name, ID, features)
├── gradle.properties         # Gradle JVM settings
├── settings.gradle           # Plugin management
├── dependencies.gradle       # IE dependency declaration
├── repositories.gradle       # Blusunrize Maven for IE
├── gradlew / gradlew.bat     # Gradle wrapper scripts
├── gradle/wrapper/           # Gradle wrapper config (v8.5)
└── src/main/
    ├── java/com/micatechnologies/realgridaddon/
    │   ├── RealGridAddon.java           # Main @Mod class
    │   ├── proxy/                       # Client/Common proxies
    │   ├── init/                        # Block, item, tile entity registration
    │   ├── blocks/
    │   │   ├── transformers/            # Transformer blocks & tile entities
    │   │   ├── insulators/              # Insulator blocks & tile entities
    │   │   └── switchgear/              # Distribution switch block & TE
    │   └── items/                       # ItemBlock wrapper
    └── resources/
        ├── mcmod.info                   # Mod metadata (tokens expanded by Gradle)
        ├── pack.mcmeta
        └── assets/realgridaddon/
            ├── lang/en_us.lang
            ├── blockstates/
            ├── models/block/ & item/
            └── textures/blocks/
```

## License
Copyright (c) Mica Technologies. All rights reserved.
