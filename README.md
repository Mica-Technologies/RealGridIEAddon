# Real Grid Addon

A Minecraft Forge 1.12.2 mod by **Mica Technologies** that adds realistic power grid components for [Immersive Engineering](https://www.curseforge.com/minecraft/mc-mods/immersive-engineering).

## Features

### Transformers
- **Class A Transformer (2-Wire)** — 2 HV connections on the sides, invisible MV/LV relay point on top center
- **Class A Transformer (1-Wire)** — 1 HV connection on the side, invisible MV/LV relay point on top center
- **Class C Transformer (2-Wire)** — 2 HV connections on the top, invisible MV/LV relay point on top center
- **Class C Transformer (1-Wire)** — 1 HV connection on the top, invisible MV/LV relay point on top center

All transformers are 2 blocks tall and allow energy to pass between HV and MV/LV networks. The MV/LV relay point supports multiple wire connections of the same type.

### Insulators
Wire relay components that allow energy to pass through and support multiple connections:
- **MacLean F-Neck Post Insulator** — Distribution line post insulator
- **MacLean Dead End Insulator** — For terminating wire runs on poles
- **Hendrix Vise Top Insulator** — Modern distribution line insulator
- **Porcelain Post Top Insulator** — Classic porcelain insulator (white/black variants)
- **Porcelain Dead End Insulator** — Classic porcelain dead end insulator (white/red variants)

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
- Immersive Engineering 0.12-73+

## Setup Instructions

### Prerequisites
- Java Development Kit (JDK) 8
- An internet connection (for Gradle to download dependencies)

### Building

1. **Download the Gradle wrapper JAR**: The `gradle/wrapper/gradle-wrapper.jar` file is not included in version control. Download Gradle 4.9 and place the wrapper JAR at `gradle/wrapper/gradle-wrapper.jar`, or run:
   ```bash
   # On systems with Gradle installed:
   gradle wrapper --gradle-version 4.9
   ```

2. **Set up the development environment**:
   ```bash
   ./gradlew setupDecompWorkspace
   ```

3. **Generate IDE project files** (optional):
   ```bash
   # For Eclipse
   ./gradlew eclipse

   # For IntelliJ IDEA
   ./gradlew genIntellijRuns
   ```

4. **Build the mod**:
   ```bash
   ./gradlew build
   ```
   The built JAR will be in `build/libs/`.

5. **Run the development client**:
   ```bash
   ./gradlew runClient
   ```

### IDE Setup
- **IntelliJ IDEA**: Import as a Gradle project. Run `./gradlew genIntellijRuns` for run configurations.
- **Eclipse**: Run `./gradlew eclipse` and import the project.

## Project Structure
```
RealGridAddon/
├── build.gradle              # Main build script
├── gradle.properties         # Mod metadata and versions
├── settings.gradle           # Project settings
├── dependencies.gradle       # IE dependency
├── repositories.gradle       # Maven repositories
├── gradlew / gradlew.bat     # Gradle wrapper scripts
├── gradle/wrapper/           # Gradle wrapper config
└── src/main/
    ├── java/com/micatechnologies/realgridaddon/
    │   ├── RealGridAddon.java           # Main mod class
    │   ├── proxy/                       # Client/Common proxies
    │   ├── init/                        # Registration classes
    │   ├── blocks/
    │   │   ├── transformers/            # Transformer blocks & TEs
    │   │   ├── insulators/              # Insulator blocks & TEs
    │   │   └── switchgear/              # Switchgear block & TE
    │   └── items/                       # Item classes
    └── resources/
        ├── mcmod.info
        ├── pack.mcmeta
        └── assets/realgridaddon/
            ├── lang/en_us.lang
            ├── blockstates/
            ├── models/block/ & item/
            └── textures/blocks/
```

## License
Copyright (c) Mica Technologies. All rights reserved.
