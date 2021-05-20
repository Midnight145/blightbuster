# Dawn Machine

I've gotten a few questions since starting to maintain BlightBuster about how the Dawn Machine actually works, specifically the numbers behind it.
This is a writeup on how the basics of it work, including the aspects, numbers behind it, and the majority of the code.

<span style="font-size:20px">Table of Contents</span>
1. [Aspects](#aspects)
2. [Numbers](#numbers)  
3. [Code](#code)

<br>

## Aspects

| Keyword    | Meaning                                                                                                                                                                  |
| ---------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Aspect     | The essentia being piped in                                                                                                                                              |
| Multiplier | How one essentia gets translated into the Dawn Machine's internal buffer.<br>For example, 1 Sano essentia piped into the Dawn Machine translates into 512 internal essentia |
| Cost       | The amount of essentia it uses from the internal buffer per operation                                                                                                    |
| Max        | The amount of each essentia it can store inside of it's internal buffer.<br>This value is always 32 \* Multiplier                                                                       |
| Discount   | The amount of RF/operation it needs to halve the essentia cost    

| Aspect   | Multiplier | Cost | Max   | Discount |
| -------- | ---------- | ---- | ----- | -------- |
| Sano     | 512        | 2    | 16384 | 16       |
| Ignis    | 2048       | 2    | 65536 | 4        |
| Aer      | 128        | 2    | 4096  | 63       |
| Cognitio | 128        | 2    | 4096  | 63       |
| Machina  | 128        | 2    | 4096  | 62       |
| Auram    | 1          | 4    | 32    | 16000    |
| Vacuous  | 512        | 2    | 16384 | 16       |
| Ordo     | 128        | 2    | 4096  | 63       |
| Arbor    | 256        | 2    | 8192  | 32       |
| Herba    | 512        | 2    | 16384 | 16       |

| Aspect   | Spent                                                                         |
| -------- | ----------------------------------------------------------------------------- |
| Sano     | Whenever a mob is cleaned                                                     |
| Ignis    | Taint Spore Swarmers<br>Every instance of a taint block                       |
| Aer      | Every 600 ticks                                                               |
| Cognitio | Whenever it skips an already cleaned block                                    |
| Machina  | Every 4 ticks                                                              |
| Auram    | Whenever a node is cured                                                      |
| Vacuous  | Every instance of flux gas/goo<br>Whenever Ignis clears Crusted Taint/similar |
| Ordo     | Generating the next coordinates                                               |
| Arbor    | Whenever it plants a sapling                                                  |
| Herba    | Whenever it plants grass                                                      |

<br>

## Numbers
- Internal RF: 128,000
- Max RF Input: N/A

| Aspect   | Operations/essentia |
| -------- | ------------------- |
| Sano     | 256                 |
| Ignis    | 1024                |
| Aer      | 64                  |
| Cognitio | 64                  |
| Machina  | 64                  |
| Auram    | .25                 |
| Vacuous  | 256                 |
| Ordo     | 64                  |
| Arbor    | 128                 |
| Herba    | 256                 |

<br>

## Code
<span style="font-size:20px">This does not document every function relating to the Dawn Machine, just the ones I deemed most important to explaining how it works.</span>

Important Static Variables:

| Variable      | Value         |
| ------------- | ------------- |
| MAX_RF        | 128000        |
| FULLGREEN_RF  | 80000         |
| FULLYELLOW_RF | 40000         |
| FULLRED_RF    | 20000         |
| DEAD_RF       | 150           |
| AER_COOLDOWN  | 20 * 30 (600) | 

Other Important Variables:

| Variable              | Usage                                                                                            | Values  |
| --------------------- | ------------------------------------------------------------------------------------------------ | ------- |
| ticksSinceLastCleanse | How long it has been since (in ticks) the Dawn Machine has cleaned a block.                      | 0 - 12  |
| cleanseLength         | How quickly the Dawn Machine should run, in ticks.                                               | 4, 12   |
| aerCooldownRemaining  | How long it has been (in ticks) since it has spent Aer essentia.                                 | 0 - 600 |
| columnCrustedtaint    | Used to see how many blocks high crusted taint is while cleaning a block. Used in sapling checks |         |

Important Functions:

| Function Name                                                    | Usage                                                                                   |
| ---------------------------------------------------------------- | --------------------------------------------------------------------------------------- |
| [updateEntity()](#function-updateentity)                         | "Base" function that all TileEntitys have, runs every tick.                             |
| [executeCleanse()](#function-executecleanse)                     | Run inside of updateEntity, does all of the necessary checks and cleans a block.        |
| [hasAnythingToCleanseHere()](#function-hasanythingtocleansehere) | Checks the current biome and whether there are curable mobs at a specific block.        |
| [setUpAerRange()](#function-setupaerrange)                       | Will change the range of the Dawn Machine based on whether you have enough Aer essentia |
| [getNewCleanseCoords()](#function-getnewcleansecoords)           | Will generate the coordinates of the next block to be cleansed                          |
| [cleanseBiome()](#function-cleansebiome)                         | Will cleanse the biome of current coordinates                                           |
| [cleanseBlocks()](#function-cleanseblocks)                       | Will cleanse the blocks of current coordinates                                          |
| [cleanseSingleBlock()](#function-cleansesingleblock)             | Will cleanse the current block it is working on, used by cleanseBlocks                  |
| [cleanseMobs()](#function-cleansmobs)                            | Will purify any mobs in range                                                           |

### Function updateEntity()
1. The first (relevant) thing it does is it decrements the `aerCooldownRemaining` variable by one.
2. It sets `cleanseLength` to either 4 or 12, depending on whether it has enough Machina in it's internal buffer.
3. `ticksSinceLastCleanse` is then set to the remainder of itself and `cleanseLength`
4. If `ticksSinceLastCleanse` is zero (divisible by `cleanseLength`), then it will go on to try to clean the area. This means that it will try to clean an area every `cleanseLength` ticks.
5. Every time the Dawn Machine cleans a block, `ticksSinceLastCleanse` is incremented by 1. Once `ticksSinceLastCleanse` is divisible by `cleanseLength`, it will try to clean the next block.
6. The function [setUpAerRange()](#function-setupaerrange) is ran (when `ticksSinceLastCleanse` is 0). It will modify the Dawn Machine's chunkloader, as well as reset the`airCooldownRemaining` variable when necessary.
7. If [hasAnythingToCleanseHere()](#function-hasanythingtocleansehere) returns false, it will check to see if it has Cognitio, and if so, will spend it and then check the next block without incrementing `ticksSinceLastCleanse`.
	- Since `ticksSinceLastCleanse` was never incremented, if the Dawn Machine has finished cleaning an area, it will spend Cognitio every tick.
	- Likewise, it will spend Ordo every tick
8. If there is something to be done, it will then spend Machina essentia if it is being used (if `cleanseLength` = 4)
9. It cleans the block via [executeCleanse()](#function-executecleanse) and increments `ticksSinceLastCleanse`

### Function executeCleanse()
1. It checks to see if it is time to spend Aer essentia, and if so, it will spend it.
2. It will search for any Tainted Swarm Spawners, kill it if there is sufficient Ignis essentia
	- If there is Vacuous essentia, it will continue
	- If there is not, it will place Flux goo
3. Similarly to the last one, it will do the same with Falling Crusted Taint
4. It goes on to call [cleanseBiome()](#function-cleansebiome), [cleanseBlocks()](#function-cleanseblocks), and (if there is enough Sano) [cleanseMobs()](#function-cleansemobs)

### Function hasAnythingToCleanseHere()
1. It will check if the current biome is Tainted Lands, Eerie, or Magical Forest. If it is, it will return true.
2. It will check if there are any curable mobs at the current coordinate, and return true if there are.
3. It checks if there is a need to use Ignis, Herba, Auram or Vacuous essentia, and will return true
4. In all other cases, there is nothing to cleanse, so it will return false.

### Function setUpAerRange()
1. It modifies the Dawn Machine's chunkloader to use the larger radius
2. Checks to make sure you have enough aer
3. Will reset `airCooldownRemaining	` if necessary



### Function getNewCleanseCoords()
1. It will generate random coordinates to clean if there is not Ordo present
2. If there is Ordo present, it will return the next coordinates in the chunk to be cleansed, and spends the Ordo



### Function cleanseBiome()
1. It will check to make sure it has enough Vacuous essentia
2. It will check if the current biome is Tainted Lands, Eerie, or Magical Forest. 
	- If it is, and the original biome is not Eerie or Magical Forest, it will skip cleansing the biome itself
3. If there is enough Vacuous, it will check to see if there is any Flux that needs cleared, and will clear it if necessary and spend the Vacuous.

### Function cleanseBlocks()
1. It checks if you have enough Herba and Arbor essentia
2. It gets the current block it's working on, and checks if it is a taint block, and if you have enough Ignis, or if it's Flux goo/gas and you have Vacuous, and if you do, it increments `columnCrustedTaint`
3. It calls [cleanseSingleBlock()](#function-cleansesingleblock) to cleanse the current block
4. It gets the biome and places a sapling on the top block if possible and `columnCrustedTaint`	is at least 3

### Function cleanseSingleBlock()
1. It checks for required essentia values and will spend them as necessary.

| Block         | Essentia Required       |
| ------------- | ----------------------- |
| Crusted Taint | Vacuous, Ignis          |
| Tainted Soil  | Ignis, Herba (optional) |
| Fibrous Taint | Ignis                   |
| Flux Goo/Gas  | Vacuous                 |
| Dirt          | Herba (optional)        |
| Tainted Node  | Auram                   |

### Function cleanseMobs()
1. It checks to see if there is a tainted mob at the current location.
2. If so, remove it, and replace it with the untainted version if you have enough Sano
3. Spend Sano if you have enough
