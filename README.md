# SaveCoords:

SaveCoords is a performance-focused Minecraft plugin that lets players save, list and delete custom coordinates.  
Designed for **Spigot, Paper and Folia** and fully compatible with **Minecraft 1.21.X**.

---

## Features:

**Core Features**
- Save and list your personal coordinates.
- Delete stored coordinates.
- Optionally, include a name to facilitate identification.

**Performance**
- Built-in cache system to reduce database load.
- Asynchronous database operations for smooth gameplay.

**Database Support**
- SQLite (default, no setup needed)
- MySQL (optional, for shared multiplayer servers)

**Permission Support**
- Full command permission integration.

---

## Commands:

| Command | Description |
|---------|-------------|
| `/coords help` | Show help page |
| `/coords save <name>` | Save your current location |
| `/coords list` | List saved coordinates |
| `/coords delete <name>` | Delete a saved coordinate |
| `/coords tp <name>` | Teleport to a saved coordinate |

*(Permissions are supported per command for fine-grained control.)*

---

## Installation:

1. Download the latest release `.jar` file.
2. Place the `.jar` inside your server’s `plugins/` folder.
3. Start your server to generate the config files.
4. Configure `config.yml` if needed.

---

## Requirements:

- Minecraft **1.21.8**  
- Spigot / Paper / Folia

---

## Configuration:

After the first run, a `config.yml` will be generated.  
Customize database settings and other options there.

---

## Download:

Get the latest release from the **Releases** tab.

---

## License:

MIT License © Dheamm  
*(Include license file separately if applicable)*

---

## Links:

- GitHub: https://github.com/Dheamm/SaveCoords  
- Modrinth (coming soon)

---

Thanks for using SaveCoords!
