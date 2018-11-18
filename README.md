[![Codacy Badge](https://api.codacy.com/project/badge/Grade/56590355c739455e9311e0eda13935aa)](https://www.codacy.com/app/cout970/Modeler?utm_source=github.com&utm_medium=referral&utm_content=cout970/Modeler&utm_campaign=badger)
[![Build Status](https://travis-ci.org/cout970/Modeler.svg?branch=master)](https://travis-ci.org/cout970/Modeler)
[![Support Server Invite](https://img.shields.io/badge/Join-Magneticraft-7289DA.svg?style=flat-square)](https://discord.gg/EhYbA97)

# Modeler
Open 3D Modeling Tool 

This tool is written in Kotlin, using [LWJGL3](https://www.lwjgl.org/) for rendering and [Legui](https://github.com/cout970/legui) to build user interfaces

### Screenshots
![3d view](https://i.imgur.com/BWWotpp.png)
![4 viewports](https://i.imgur.com/YTJsckU.png)
![texture editing](https://i.imgur.com/5HfHfy4.png)
![animations](https://i.imgur.com/c1KTFaO.png)

### Current state
The program is in beta so if you find a bug please report it as soon as possible.

### Formatts
Valid input formats:
- OBJ: Wavefront .obj file
- TCN: Techne model format (outdated 3d modeler)
- TBL: [Tabula model format](https://github.com/iChun/Tabula)
- JSON: [Minecraft json models](https://minecraft.gamepedia.com/Model)
- MCX: Custom model format used in [ModeLoader](https://minecraft.curseforge.com/projects/modelloader)
- GLTF: mostly supported

Valid output formats:
- OBJ: Wavefront .obj file
- MCX: Custom model format used in [ModeLoader](https://minecraft.curseforge.com/projects/modelloader)
- GLTF: mostly support, has animations
- VS: Vintage Story model format (not all features are supported)

The formats TCN, TBL and JSON are based on storing objects as cubes with properties, they don't allow 
arbitrary kinds of shapes so it's impossible to export complex models to them.

### Features
- Free vertex edit capabilities
- Free UV edit capabilities
- Translation, rotation and scale
- Object, face, edge and vertex individual selection
- Texture import on change
- Automatic backup creation and removal of old backups
- Local and global transformation
- Keyframe based animations
- Helper grids to align objects
- Orthographic and projection cameras
- Multiview up to 4 viewports
- Skybox background
- Texture template export
- Axis Aligned Bounding Box export to text format

### FAQ
- Q: Hot to open/enable animation panel?
- A: Press ALT + B
