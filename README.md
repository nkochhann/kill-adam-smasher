# Kill Adam Smasher

A terminal-based cyberpunk auto-battler built with Kotlin.

**Kill Adam Smasher** is a small turn-based combat game inspired by *Cyberpunk 2077*. The player's objective is simple: choose an iconic weapon, prepare for battle, and defeat Adam Smasher.

The game combines dice-based combat mechanics, a lightweight progression system, and a retro cyberpunk terminal interface.

## Gameplay

The game follows an automated combat loop where the player and Adam Smasher exchange attacks.

Each attack and defense action is resolved using a D20 dice roll:

- The attacker rolls a D20 and adds their attack modifier.
- The defender rolls a D20 and adds their defense modifier.
- If the attack result is higher than the defense result, the attack succeeds.
- Successful attacks deal damage according to the weapon's damage dice.

Both characters can attack and receive damage during the same turn.

### Economy and progression

At the end of each turn, the player earns eddies based on the damage dealt and received.

Eddies can be spent on items and upgrades between turns, including:

- Healing items.
- Temporary combat stimulants.
- Defensive enhancements.
- Damage and critical-hit upgrades.

The goal is to survive long enough to defeat Adam Smasher by improving your combat capabilities throughout the fight.

## Interface

The game is designed to run entirely in the terminal, using a custom TUI (Terminal User Interface).

The interface aims to provide:

- A navigable main menu.
- Character health and combat status.
- Pixel art sprites for Adam Smasher.
- Dice roll visualization and combat results.
- A combat log displaying recent actions.
- A simple interface for purchasing upgrades.

The visual direction takes inspiration from retro 16-bit games, combining dark backgrounds, neon accents, and cyberpunk aesthetics.

## Tech Stack

- **Kotlin** — Main programming language.
- **Lanterna** — Terminal UI rendering, keyboard input, and screen management.
- **Java AWT / ImageIO** — Loading and processing sprite images.
