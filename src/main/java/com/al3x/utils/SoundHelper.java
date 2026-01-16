package com.al3x.utils;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class SoundHelper {

    public static void playSuccessSound(Player player) {
        int index = SoundEvent.getAssetMap().getIndex("SFX_Ore_Hit");
        World world = player.getWorld();
        EntityStore store = world.getEntityStore();
        Ref<EntityStore> playerRef = player.getReference();
        world.execute(() -> {
            TransformComponent transform = store.getStore().getComponent(playerRef, EntityModule.get().getTransformComponentType());
            SoundUtil.playSoundEvent3dToPlayer(playerRef, index, SoundCategory.UI, transform.getPosition(), store.getStore());
        });
    }

    public static void playErrorSound(Player player) {
        int index = SoundEvent.getAssetMap().getIndex("SFX_Frog_Death");
        World world = player.getWorld();
        EntityStore store = world.getEntityStore();
        Ref<EntityStore> playerRef = player.getReference();
        world.execute(() -> {
            TransformComponent transform = store.getStore().getComponent(playerRef, EntityModule.get().getTransformComponentType());
            SoundUtil.playSoundEvent3dToPlayer(playerRef, index, SoundCategory.UI, transform.getPosition(), store.getStore());
        });
    }

}
