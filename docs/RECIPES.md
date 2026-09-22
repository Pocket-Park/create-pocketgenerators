# Recettes

Deux familles de recettes JSON, sous `src/main/resources/data/pocketgenerators/recipe/` :

- **Recettes de duplication** (`pocketgenerators:resource_generating`) — quels items un duplicateur peut dupliquer, à partir de quel tier, à quelle cadence de base.
- **Recettes de craft** (`minecraft:crafting_shaped` ou `create:mechanical_crafting`) — comment fabriquer les blocs de duplicateur eux-mêmes.

## Recettes de duplication

```json
{
  "type": "pocketgenerators:resource_generating",
  "ingredient": { "item": "minecraft:iron_ore" },
  "tier_required": "andesite",
  "base_ticks": 260
}
```

- `ingredient` : un `Ingredient` vanilla classique (item unique ou tag).
- `tier_required` : `basic` | `andesite` | `brass` | `netherite` | `end`.
- `base_ticks` (optionnel, défaut `200`) : utilisé dans la formule de cadence — `ticksParItem = max(minTicks, base_ticks / (rpm * multiplicateurDuTier))`. Plus haut = plus lent à RPM égal.

Pas de champ `result` : le duplicateur produit une copie de l'item présent dans son filtre, pas un item différent. La recette sert uniquement de liste blanche + condition de déblocage.

**Netherite et End sont des silos** : un générateur Netherite ne peut dupliquer que les recettes `tier_required: netherite`, jamais celles en `end`, et inversement — même si Netherite/End sont "au-dessus" de Brass dans l'ordre de l'enum. Basic/Andesite/Brass cascadent normalement (un générateur Brass peut aussi dupliquer tout ce que Basic et Andesite débloquent).

### Table actuelle

| Tier | Items |
|---|---|
| **Basic** | cobblestone, coal_ore, deepslate_coal_ore, coal_block, copper_ore, deepslate_copper_ore, raw_copper_block, copper_block |
| **Andesite** | iron_ore, deepslate_iron_ore, raw_iron_block, iron_block, redstone_ore, deepslate_redstone_ore, redstone_block, lapis_ore, deepslate_lapis_ore, lapis_block |
| **Brass** | gold_ore, deepslate_gold_ore, raw_gold_block, gold_block |
| **Netherite** *(Nether uniquement)* | nether_quartz_ore, quartz_block, nether_gold_ore, gilded_blackstone, glowstone, ancient_debris, netherite_scrap, netherite_ingot, netherite_block |
| **End** *(End uniquement)* | end_stone, end_stone_bricks, purpur_block, chorus_fruit, shulker_shell |

Diamant, émeraude et leurs dérivés sont volontairement absents de toute la liste (rééquilibrage).

Ajouter un item duplicable = ajouter un fichier JSON dans ce dossier, aucune modification de code nécessaire.

## Recettes de craft (obtenir les blocs)

| Tier | Type | Grille | Ingrédients clés |
|---|---|---|---|
| Basic | `minecraft:crafting_shaped` | 3×3 | cobblestone, iron_nugget, `create:shaft`, redstone |
| Andesite | `create:mechanical_crafting` | 4×4 | `create:andesite_alloy`, iron_ingot, `create:shaft`, + 1 Basic |
| Brass | `create:mechanical_crafting` | 5×5 | `create:brass_ingot`, `create:precision_mechanism`, iron_ingot, + 1 Andesite |
| Netherite | `create:mechanical_crafting` | 5×5 | netherite_ingot, netherite_scrap, `create:precision_mechanism`, + 1 Brass |
| End | `create:mechanical_crafting` | 5×5 | shulker_shell, ender_pearl, chorus_fruit, + 1 Brass |

Netherite et End se craftent tous les deux à partir d'un Brass (branches parallèles, pas de dépendance Netherite→End).

Le format `create:mechanical_crafting` est identique à `minecraft:crafting_shaped` (mêmes champs `key`/`pattern`/`result`/`category`), avec un champ booléen supplémentaire `accept_mirrored` (on utilise `true` partout — le motif peut être posé en miroir dans le Mechanical Crafter). Nécessite un Mechanical Crafter assemblé à une taille au moins égale à la grille de la recette.

Exemple (Brass) :

```json
{
  "type": "create:mechanical_crafting",
  "category": "redstone",
  "accept_mirrored": true,
  "key": {
    "Z": { "item": "create:brass_ingot" },
    "P": { "item": "create:precision_mechanism" },
    "I": { "item": "minecraft:iron_ingot" },
    "D": { "item": "pocketgenerators:resource_generator_andesite" }
  },
  "pattern": [
    "ZZZZZ",
    "ZPIPZ",
    "ZIDIZ",
    "ZPIPZ",
    "ZZZZZ"
  ],
  "result": { "id": "pocketgenerators:resource_generator_brass", "count": 1 }
}
```
