[![Codacy Badge](https://api.codacy.com/project/badge/Grade/56590355c739455e9311e0eda13935aa)](https://www.codacy.com/app/cout970/Modeler?utm_source=github.com&utm_medium=referral&utm_content=cout970/Modeler&utm_campaign=badger)
[![Build Status](https://travis-ci.org/cout970/Modeler.svg?branch=master)](https://travis-ci.org/cout970/Modeler)
[![Support Server Invite](https://img.shields.io/badge/Join-Magneticraft-7289DA.svg?style=flat-square)](https://discord.gg/EhYbA97)

# Modeler
Open 3D Modeling Tool 

This tool is written in Kotlin, using [LWJGL3](https://www.lwjgl.org/) for rendering and [Legui](https://github.com/cout970/legui) to build user interfaces

### Screenshots
![http://prntscr.com/h7www3](https://image.prntscr.com/image/Hq3BMEieSwuXEyt_tdoOrw.png)
![http://prntscr.com/h7wx7a](https://image.prntscr.com/image/zNBA32ZkTQ_kbJcYpLsaoA.png)
![http://prntscr.com/h7wv0i](https://image.prntscr.com/image/zC6SfXSHQg_fZ26qDUI7Zg.png)
![http://prntscr.com/h7wyik](https://image.prntscr.com/image/wc6eE8i_SFi1Ks-iNDGaJA.png)

### Current state
The program is in alpha and there are tons of features that need to be implemented.

I doing this as a side project so don't expect it be finished soon.

### Formatts
Valid input formats:
- OBJ: Wavefront .obj file
- TCN: Techne model format (outdated 3d modeler)
- TBL: [Tabula model format](https://github.com/iChun/Tabula)
- JSON: [Minecraft json models](https://minecraft.gamepedia.com/Model)
- MCX: Custom model format used in [ModeLoader](https://minecraft.curseforge.com/projects/modelloader)

Valid output formats:
- OBJ: Wavefront .obj file
- MCX: Custom model format used in [ModeLoader](https://minecraft.curseforge.com/projects/modelloader)

The formats TCN, TBL and JSON are based in storing objects as cubes with properties, so they don't allow any kind of shape and it's impossible to export to them from OBJ.
