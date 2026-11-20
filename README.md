# Presence Footsteps [NeoForge]
![License](https://img.shields.io/github/license/Sollace/Presence-Footsteps)
![](https://img.shields.io/badge/api-forge-orange.svg)

_..An Overly complicated Sound Mod..._

---

This is the continuation of Huricaaan (Ha3)'s original mod, maintained and updated to the latest version of Minecraft. All previous features have been restored and some new ones added, with new development promised for the future on the opensource github profile!

---

Expect to see a more rich and immersive Minecraft world, one where Presence Footsteps adds new and more dynamic sounds for every block the player walks on. Every block has its own material assigned to it, and can make their own, customized sounds to better match what they're made of.

---

- Wooden chests are creaky and old,
- stone is dusty and rough.
- Piston heads sound like wood, whilst their bodies are stone,
- even cake sounds like you might destroy if you step in the frosting!
- Tall grass and bristles make a dry, rustling sound as you step through them,
- Magma sounds like it's literally cooking your souls!

---

Not only that, but the latest version includes step sounds for certain non-block entities:

---

Shulkers make the same creaky box noises as Shulker boxes, boats sound like wood, and armour stands creak and produce a stone stepping sound as you walk on their stone slab base.

 ---

## Sound Packs

Presence Footsteps is **fully customisable** with resource packs. The mod by default includes a pack with its own sounds
and material mappings for the vanilla blocks, however if you don't like those sounds or want to use your own, this pack can be turned off
or used together with packs from the community.

Note that you need **at least one** sounds pack enabled for the mod to function.

![image](https://github.com/Sollace/Presence-Footsteps/assets/6429283/9c50aa59-0f59-4324-aab7-cb360acac2ad)

## Mod Support

### External Footstep Timing (NeoForge 1.21.1)

Client mods can subscribe to `eu.ha3.presencefootsteps.api.FootstepTimingEvent` on `NeoForge.EVENT_BUS` to control walking step timing without mixing into the sound generator. The event runs once per simulated entity per client tick, after motion tracking and before automatic walking steps. Use `getEntity()` to restrict control to the players your mod manages.

Cancel the event with `setCanceled(true)` between animation beats. On a beat, leave it uncanceled and call `requestStep()` to request one walking step even if the usual distance threshold has not been reached. Repeated requests during the same event still produce at most one walking step; cancellation takes precedence. Ground, underwater, climbing, sneaking, material, footwear, volume, and left/right foot handling remain with Presence Footsteps. Airborne entities cannot be forced to take walking steps. Jumping, landing, foliage brushing, and stationary sounds keep their own timing.

Listeners that leave the event unchanged preserve automatic timing. Only subscribe on the client when Presence Footsteps is installed. Existing `TerrestrialStepSoundGenerator#simulateFootsteps` mixins continue to work; remove the timing mixin when switching to this event.

### Block Sounds

Presence Footsteps will attempt to figure out the sounds of modded blocks by looking at the vanilla material/sound types they use.
If your mod is using very unique sounds though, you can add mappings to your mod to directly control what sounds presence footsteps uses for your block by
following the [guide on our wiki](https://github.com/Sollace/Presence-Footsteps/wiki/Information-for-Resourcepack-Creators)
