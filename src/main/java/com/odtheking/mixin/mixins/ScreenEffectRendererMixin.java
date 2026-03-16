package com.odtheking.mixin.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.odtheking.odin.features.impl.render.RenderOptimizer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {

    @Inject(method = "renderFire", at = @At("HEAD"), cancellable = true)
    private static void onRenderFireOverlay(PoseStack poseStack, MultiBufferSource multiBufferSource, TextureAtlasSprite textureAtlasSprite, CallbackInfo ci) {
        if (RenderOptimizer.shouldDisableFireOverlay()) ci.cancel();
    }

    @Redirect(method = "getViewBlockingState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isViewBlocking(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"))
    private static boolean onIsViewBlocking(BlockState blockState, BlockGetter level, BlockPos pos) {
        if (RenderOptimizer.canSeeThroughBlock()) return false;
        return blockState.isViewBlocking(level, pos);
    }
}
