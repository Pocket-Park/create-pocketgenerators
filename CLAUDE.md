# Pocket Generators

Addon [Create](https://github.com/Creators-of-Create/Create) pour NeoForge 1.21.1. Ajoute des duplicateurs : des blocs cinétiques qui dupliquent l'item placé dans leur filtre tant qu'ils reçoivent assez de rotation, en 5 tiers (Basic, Andesite, Brass, Netherite, End).

Voir [README.md](README.md) pour le fonctionnement côté joueur, [docs/API.md](docs/API.md) pour les points d'extension internes, [docs/RECIPES.md](docs/RECIPES.md) pour le format et la liste des recettes.

## Stack

- NeoForge 21.1.251, Minecraft 1.21.1, Java 21
- Dépend de Create 6.0.10-280 (required), Ponder 1.0.82 + Flywheel 1.0.6 (transitifs de Create), Registrate
- JEI 19.21.0.247 en dépendance optionnelle (compileOnly, plugin actif seulement si JEI présent au runtime)

## Build & test

```bash
./gradlew build        # compile + package le jar (build/libs/)
./gradlew runClient     # lance un client de dev avec le mod chargé
```

CI GitHub Actions (`.github/workflows/build.yml`) : build seul sur push/PR, pas de tests automatisés (aucun test unitaire dans le projet).

## Structure du code

```
src/main/java/com/pocketpark/pocketgenerators/
  PocketGenerators.java          point d'entrée @Mod, creative tab
  PocketGeneratorsClient.java    setup client (bus MOD, pas GAME)
  Config.java                    ModConfigSpec COMMON, par tier
  block/
    Tier.java                    enum des 5 tiers + valeurs par défaut
    ResourceGeneratorBlock.java  bloc cinétique, wrench = cycle output facing
    ResourceGeneratorBlockEntity.java  logique génération, stress, capability
    ResourceGeneratorBlockItem.java
  client/ResourceGeneratorRenderer.java  rendu item posé + arbre tournant (Flywheel)
  recipe/ResourceGeneratorRecipe.java    type de recette custom (liste blanche, pas de transformation)
  compat/jei/                    plugin JEI optionnel
  registry/                      DeferredRegister pour blocks/items/block entities/recipe types
```

Assets sous `src/main/resources/assets/pocketgenerators/` (blockstates, models, lang), recettes sous `src/main/resources/data/pocketgenerators/recipe/`.

## Points d'attention (pièges déjà rencontrés, voir docs/API.md pour le détail)

- `calculateStressApplied()` et `isSpeedRequirementFulfilled()` sont surchargés au lieu d'utiliser `BlockStressValues` de Create (registre indexé par classe `Block`, incompatible avec un bloc partagé entre 5 tiers via blockstate).
- Netherite et End sont des **silos exclusifs** dans `ResourceGeneratorRecipe.isUnlockedBy()` — ne pas laisser la cascade normale d'ordinal s'appliquer entre eux.
- `@EventBusSubscriber` côté client doit déclarer `bus = EventBusSubscriber.Bus.MOD` explicitement, sinon les listeners d'événements de cycle de vie (FMLClientSetupEvent, etc.) ne se déclenchent jamais silencieusement.
- `useItemOn` du bloc doit laisser passer `WrenchItem` avant sa propre logique.
- Les modèles des 5 tiers réutilisent des textures Create/vanilla existantes (copper_casing, andesite_casing, brass_casing, netherite_block, purpur_block) — aucune texture custom dans le projet actuellement.

## Ce qu'il reste à faire

Voir [TODO.md](TODO.md) pour la liste à jour.
