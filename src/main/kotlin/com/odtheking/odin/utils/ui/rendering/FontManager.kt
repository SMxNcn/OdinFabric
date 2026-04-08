package com.odtheking.odin.utils.ui.rendering

import com.odtheking.odin.OdinMod.logger
import com.odtheking.odin.OdinMod.mc
import net.minecraft.resources.ResourceLocation
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels

object FontManager {
    private const val FONT_URL = "https://gitee.com/mixturedg/necron-client-repo/raw/master/fonts/"
    private const val FONT_DIR = "config/necron/fonts"
    private const val FONT_NAME = "MiSans-Demibold.ttf"
    private val fontFile = File(FONT_DIR, FONT_NAME)

    fun initFont() {
        val dir = File(FONT_DIR)
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                if (!fontFile.exists()) {
                    try {
                        val website = URL(FONT_URL + FONT_NAME)
                        Channels.newChannel(website.openStream()).use { rbc ->
                            FileOutputStream(FONT_DIR + File.separator + FONT_NAME).use { fos ->
                                fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
                            }
                        }
                        logger.info("Successfully downloaded font: {}", FONT_NAME)
                    } catch (e: Exception) {
                        logger.error("Failed to download font: {}", FONT_NAME, e)
                    }
                }
            }
        }
    }

    fun loadFont(): Font {
        return try {
            File("$FONT_DIR/$FONT_NAME").inputStream().use { inputStream -> Font("Chinese", inputStream) }
        } catch (_: Exception) {
            Font("Default", mc.resourceManager.getResource(ResourceLocation.parse("odin:font.ttf")).get().open())
        }
    }
}
