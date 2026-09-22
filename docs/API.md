# API interne

Ce document décrit les points d'extension propres au mod, et les API de Create/NeoForge qu'on consomme directement (utile si le comportement d'une de ces API change dans une future version de Create).

## `Tier` (`block/Tier.java`)

Enum à 5 valeurs : `BASIC`, `ANDESITE`, `BRASS`, `NETHERITE`, `END`. Porte les valeurs par défaut de trois stats (RPM minimum, impact de stress, multiplicateur de cadence) ; les valeurs réellement utilisées en jeu passent par `Config` (voir plus bas), pas directement par les constantes de l'enum.

`Tier` implémente `StringRepresentable` (nom sérialisé en minuscules, ex. `"brass"`) et expose un `Codec<Tier>` et un `StreamCodec<ByteBuf, Tier>` pour la sérialisation JSON et réseau.

Ajouter un tier = ajouter une entrée dans cet enum. `Config` s'adapte automatiquement (boucle sur `Tier.values()`), mais il faut aussi : un item + modèle de bloc/item dédiés (`ModItems`, blockstate), et éventuellement des recettes de génération/craft.

## `Config` (`Config.java`)

`ModConfigSpec` NeoForge, type `COMMON`. Pour chaque tier : `minimumRpm`, `stressImpact`, `cadenceMultiplier`, plus un `minTicks` global. Modifiable en jeu sans recompiler (`config/pocketgenerators-common.toml`).

## Recette de duplication (`recipe/ResourceGeneratorRecipe.java`)

Type de recette custom `pocketgenerators:resource_generating`, enregistré via `ModRecipeTypes`/`ModRecipeSerializers`. Implémente `Recipe<SingleRecipeInput>` (pas basé sur `ProcessingRecipe` de Create, volontairement — trop de fonctionnalités inutiles pour notre cas).

Champs : `ingredient` (Ingredient), `tier_required` (Tier), `base_ticks` (int, optionnel, défaut 200). Le résultat n'est **pas** stocké dans la recette : `assemble()` retourne une copie de l'item du filtre lui-même (c'est un duplicateur, pas un four). La recette sert donc de liste blanche + gate de tier, pas de transformation input→output.

`isUnlockedBy(Tier)` : cascade normale pour Basic/Andesite/Brass (`generatorTier.ordinal() >= tierRequired.ordinal()`), mais Netherite et End sont des silos exclusifs — un générateur Netherite ne débloque pas les recettes End et vice-versa, même si les ordinaux le suggéreraient.

Format JSON détaillé dans [`RECIPES.md`](RECIPES.md).

## `ResourceGeneratorBlockEntity`

Étend `KineticBlockEntity` (Create). Points d'intégration notables :

- `calculateStressApplied()` est surchargé au lieu d'enregistrer dans `BlockStressValues` (le registre de Create) : ce registre est indexé uniquement par classe `Block`, sans contexte de state, donc incompatible avec un seul bloc partagé entre tiers. On lit `TIER` directement sur le blockstate.
- `isSpeedRequirementFulfilled()` est surchargé pour la même raison (`IRotate#getMinimumRequiredSpeedLevel()` n'a pas non plus de contexte de state).
- Le sélecteur de quantité (tier Brass) utilise `ScrollValueBehaviour` de Create (`com.simibubi.create.foundation.blockEntity.behaviour.scrollValue`), le même widget que le Speed Controller ou le Sequenced Gearshift — cadran natif, maintenir clic droit sur la face du dessus. `getOutputCount()` clampe défensivement à 1 minimum car `ScrollValueBehaviour#read()` ne vérifie pas la présence du tag NBT et retomberait sinon à 0.
- Capability `IItemHandler` exposée via `RegisterCapabilitiesEvent` (filtre en insertion seule, sortie en extraction seule — même pattern que `MillstoneBlockEntity`).
- Poussée active vers la capability trouvée à la position de sortie (`OUTPUT_FACING`) à chaque tick — les belts ne tirent pas les items, il faut les pousser.

## `ResourceGeneratorBlock`

- `RotatedPillarKineticBlock` (axe de rotation déterminé au placement selon les arbres adjacents).
- La clé à molette (`onWrenched`) est réaffectée : au lieu de tourner l'axe cinétique (comportement par défaut d'`IWrenchable`), elle cycle la face de sortie (`OUTPUT_FACING`, propriété de blockstate à 6 valeurs).
- `useItemOn` doit explicitement laisser passer la clé à molette (`WrenchItem`) avant sa propre logique — sinon elle intercepte le clic avant que `WrenchItem.useOn()` ne s'exécute.

## Rendu client (`client/ResourceGeneratorRenderer.java`)

`BlockEntityRenderer` affichant l'item du filtre posé sur le dessus du bloc. Utilise `ItemRenderer#render(...)` avec un `BakedModel` explicite et le contexte `ItemDisplayContext.FIXED` (pattern du `DepotRenderer` de Create) — la méthode de plus haut niveau `renderStatic(..., GROUND, ...)` produisait un rendu noir.

Un segment d'arbre tournant (`ShaftVisual` de Create, via Flywheel) est enregistré en plus du rendu statique du bloc.

**Piège rencontré** : `@EventBusSubscriber` a pour bus par défaut `GAME`, pas `MOD`. Les événements de cycle de vie (`FMLClientSetupEvent`, `EntityRenderersEvent`, `RegisterPayloadHandlersEvent`) ne sont reçus que sur le bus `MOD` — sans `bus = EventBusSubscriber.Bus.MOD` explicite, le listener ne se déclenche jamais silencieusement.

## JEI (`compat/jei/`)

Plugin optionnel (`@JeiPlugin`), actif seulement si JEI est présent (dépendance `compileOnly` + `optional` dans `neoforge.mods.toml`). Catégorie custom montrant l'item du filtre des deux côtés (input = output, cohérent avec la duplication) plus le tier requis.
