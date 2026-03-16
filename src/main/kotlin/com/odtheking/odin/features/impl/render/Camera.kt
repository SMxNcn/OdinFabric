package com.odtheking.odin.features.impl.render

import com.odtheking.odin.clickgui.settings.impl.BooleanSetting
import com.odtheking.odin.clickgui.settings.impl.NumberSetting
import com.odtheking.odin.events.TickEvent
import com.odtheking.odin.events.core.on
import com.odtheking.odin.features.Module
import net.minecraft.client.CameraType

object Camera : Module(
    name = "Camera",
    description = "Disables front camera when enabled."
) {
    private val cameraClip by BooleanSetting("Camera Clip", false, desc = "Allows the camera to clip through blocks.")
    val cameraDistance by NumberSetting("Camera Distance", 4.0f, 2.0f, 10.0f, 0.1f, desc = "Third-person camera distance. (default = 4.0)")

    init {
        on<TickEvent.End> {
            if (mc.options.cameraType == CameraType.THIRD_PERSON_FRONT)
                mc.options.cameraType = CameraType.FIRST_PERSON
        }
    }

    fun canCameraClip(): Boolean {
        return enabled && cameraClip
    }
}
