package com.odtheking.mixin.mixins;

import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    protected abstract float getMaxZoom(float f);

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
    private float onGetMaxZoom(Camera camera, float originalDistance) {
        com.odtheking.odin.features.impl.render.Camera cameraModule = com.odtheking.odin.features.impl.render.Camera.INSTANCE;
        if (cameraModule.canCameraClip()) {
            return cameraModule.getCameraDistance();
        }

        return getMaxZoom(cameraModule.getCameraDistance());
    }
}