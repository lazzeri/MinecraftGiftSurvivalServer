# LucaPlugin Commands

All commands are typed in chat (not as slash commands) and can be used by any player. No permissions required.

**Usage:** Type `test <command>` in the chat

Type `test help` in-game to see all available commands.

## Utility Commands

| Command | Description |
|---------|-------------|
| `test help` | Shows all available commands in-game |
| `test ping` | Verification command - returns "Pong!" |
| `test wsconnect` | Connect to the WebSocket backend server |
| `test wsdisconnect` | Disconnect from the WebSocket backend server |
| `test wsstatus` | Check the current WebSocket connection status |

## Mob Spawning Commands

| Command | Description |
|---------|-------------|
| `test spawnwithers` | Spawns 3 withers in front of the player |
| `test spawnzombiearmy` | Spawns a zombie army (2 giant zombies + 50 minions) that disappears after 30 seconds |
| `test spawntemporarywither` | Spawns a wither that disappears after 15 seconds |
| `test spawnzombieccircle` | Spawns 10 zombies in a circle around the player |
| `test spawnrandommob` | Spawns a random hostile mob with a custom nametag |
| `test createraid` | Creates a pillager raid with Pillagers, Ravagers, Evokers, and Vindicators |
| `test netherattack` | Spawns nether mobs (Wither Skeletons, Skeletons, Blazes) and sets time to night |
| `test loadedcreeperattack` | Spawns creepers (some charged) in a circle around the player |
| `test zombieinvasion` | Spawns a zombie wave with Giants, Zombies, Zombie Villagers, and Zombie Horses |
| `test farmtime` | Spawns friendly farm animals (Cows, Chickens, Horses, Pigs, etc.) |
| `test skeletonriders` | Spawns skeleton horse riders, sets night and storm |

## Companion Commands

| Command | Description |
|---------|-------------|
| `test chickencompanion` | Spawns a baby chicken that follows the player |
| `test wolfcompanion` | Spawns a tamed wolf with a random collar color |

## Item Commands

| Command | Description |
|---------|-------------|
| `test spawnarmorstand` | Spawns an armor stand with enchanted diamond armor |
| `test elytraandrockets` | Drops an Elytra and 64 rockets in front of the player |
| `test opsword` | Gives a fully enchanted Netherite sword |
| `test itemsnack` | Removes an item from the player's toolbar (or gives a cookie if empty) |

## Effect Commands

| Command | Description |
|---------|-------------|
| `test adrenalinrush` | Gives a random effect: Jump Boost, Speed, or Blindness |
| `test giveslowpotion` | Applies Slowness effect for 30 seconds |
| `test giveregenpotion` | Applies Regeneration effect for 6 seconds |
| `test oneheart` | Sets player health to 1 heart for 1 minute |
| `test twentyheart` | Sets player health to 20 hearts for 2 minutes |
| `test throwexpbottles` | Gives experience points with particle effects |

## Weather & Environment Commands

| Command | Description |
|---------|-------------|
| `test createthunder` | Strikes lightning near the player |
| `test tntrain` | Rains TNT around the player (40-50 TNT blocks) |
| `test anvilrain` | Rains anvils around the player |
| `test startlava` | Starts the lava game - converts blocks to magma then lava |
| `test magicnotes` | Creates magical note particle effects |

## Teleport Commands

| Command | Description |
|---------|-------------|
| `test randomteleport` | Teleports the player to a random nearby location |
| `test tpnetheroroverworld` | Teleports between the Nether and Overworld |

---

## Notes

- Commands are triggered by typing in the chat, not using slash commands
- All game event commands use the player's name as the "donor name" for display messages
- Temporary effects and spawned entities will automatically expire/despawn after their duration
- WebSocket commands are used to control the connection to the streaming backend server

