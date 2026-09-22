# Pocket Generators

Addon [Create](https://github.com/Creators-of-Create/Create) pour NeoForge 1.21.1. Ajoute des **duplicateurs** : des blocs cinétiques qui dupliquent l'item placé dans leur filtre tant qu'ils reçoivent assez de rotation, en 5 tiers.

## Fonctionnement

1. Poser un duplicateur, l'alimenter en rotation (arbre, moteur, etc.).
2. Clic droit avec un item en main sur le bloc → définit le filtre (l'item à dupliquer, jamais consommé).
3. Le bloc génère périodiquement une copie de cet item et la pousse dans la belt/l'inventaire situé sur sa face de sortie.
4. Clic droit main vide → récupère la sortie. Shift + clic droit main vide → récupère le filtre.
5. Clé à molette → change la face de sortie (cycle les 6 directions).
6. *(Tier Brass uniquement)* Maintenir clic droit sur la face du dessus → ouvre le cadran natif de Create pour choisir la taille de lot (1 à 64 items par cycle, coût en stress proportionnel).

## Tiers

| Tier | Matériau représentatif | Items duplicables |
|---|---|---|
| Basic | — | Charbon, cuivre (minerais + blocs) |
| Andesite | Alliage Andesite (Create) | + Fer, redstone, lapis |
| Brass | Laiton (Create) | + Or |
| Netherite | Netherite | Items exclusifs au Nether uniquement (silo, ne cascade pas depuis/vers End) |
| End | Carapace de Shulker | Items exclusifs à l'End uniquement (silo, ne cascade pas depuis/vers Netherite) |

Diamant et émeraude sont volontairement exclus de toute la progression normale. Le détail complet des couples tier/item est dans [`docs/RECIPES.md`](docs/RECIPES.md).

Les valeurs de stress, RPM minimum et cadence par tier sont réglables sans recompiler via la config du mod (`config/pocketgenerators-common.toml`).

## Build & test

```bash
./gradlew build        # compile + package le jar (build/libs/)
./gradlew runClient     # lance un client de dev avec le mod chargé
```

Dépendances : NeoForge 21.1.251, Create 6.0.10-280, Ponder 1.0.82, Flywheel 1.0.6, Registrate. JEI (19.21.0.247) est optionnel — le plugin JEI ne s'active que si JEI est présent.

## Documentation

- [`docs/API.md`](docs/API.md) — points d'extension internes (tiers, recettes, config) et API Create utilisées.
- [`docs/RECIPES.md`](docs/RECIPES.md) — format des recettes de duplication et des recettes de craft, avec la liste complète.
