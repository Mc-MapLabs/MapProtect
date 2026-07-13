<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=2B2D31&height=200&section=header&text=MapProtect&fontSize=60&fontColor=ffffff&desc=Lightweight%20FFA%20Map%20Protection%20Plugin&descFontSize=20&descAlignY=70" alt="MapProtect Banner"/>

<br>

<a href="https://modrinth.com/plugin/mapprotect">
  </a>
<img src="https://img.shields.io/modrinth/dt/MODRINTH_PROJECT_ID?style=flat-square&logo=modrinth&logoColor=white&label=Modrinth%20Downloads&color=00AF5C">
</a>

<img src="https://img.shields.io/badge/Minecraft-1.20%2B-62B47A?style=flat-square&logo=minecraft&logoColor=white">
<img src="https://img.shields.io/badge/Platform-Paper%20%7C%20Spigot-ED8B00?style=flat-square">
<img src="https://img.shields.io/badge/Java-17%2B-5382A1?style=flat-square&logo=openjdk&logoColor=white">

<br><br>

<a href="https://modrinth.com/plugin/mapprotect">
<img src="https://img.shields.io/badge/Download-Modrinth-00AF5C?style=for-the-badge&logo=modrinth&logoColor=white">
</a>

</div>

> **MapProtect** is a lightweight Minecraft protection plugin designed specifically for FFA servers. It prevents players from damaging important arena structures while automatically cleaning up survival-placed blocks without affecting server performance.

MapProtect is built to work alongside automated map rotation systems, allowing FFA servers to maintain clean, reusable arenas without requiring manual resets.

<br>

---

## Features

<table width="100%">
<tr>
<td width="50%" valign="top">

<h3> Arena Protection</h3>

<ul>
<li><b>Block Protection:</b> Prevent players from breaking important map blocks such as bedrock, obsidian, and structural blocks.</li>
<li><b>Creative Bypass:</b> Creative players with permission can freely modify protected areas.</li>
<li><b>FFA Ready:</b> Designed for competitive free-for-all servers.</li>
<li><b>Rotation Compatible:</b> Works perfectly with map cycling plugins.</li>
</ul>

</td>

<td width="50%" valign="top">

<h3> Performance</h3>

<ul>
<li><b>Async Cleanup:</b> Map cleanup tasks run asynchronously to avoid server lag.</li>
<li><b>Lightweight:</b> Minimal resource usage with optimized operations.</li>
<li><b>Large Map Support:</b> Handles arena resets efficiently.</li>
<li><b>RGB Support:</b> Full RGB gradient message support.</li>
</ul>

</td>
</tr>
</table>

<br>

---

## How It Works

MapProtect monitors arena blocks and prevents players from destroying protected structures.

When players place blocks during gameplay, the plugin tracks and removes them during cleanup while leaving the original map untouched.

This allows FFA servers to reuse the same arenas without requiring world resets.

<br>

---

## Commands

| Command | Description |
|---|---|
| `/mapprotect clear` | Force runs the cleanup task and removes all unprotected blocks instantly. |
| `/mapprotect reload` | Reloads `config.yml` and `messages.yml`. |

<br>

---

## Permissions

| Permission | Description | Default |
|---|---|---|
| `mapprotect.creative.bypass` | Allows Creative players to bypass all map protection. | OP |
| `mapprotect.admin` | Access to MapProtect admin commands. | OP |

<br>

---

## Requirements

- Minecraft **1.20+**
- Paper / Spigot based server
- Java **17+**

<br>

---

## Configuration

MapProtect includes configurable files:

```
config.yml
messages.yml
```

Customize protection settings, cleanup behavior, and plugin messages.

<br>

---

## License

GNU-3.0
