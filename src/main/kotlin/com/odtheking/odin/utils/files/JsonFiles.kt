package com.odtheking.odin.utils.files

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.odtheking.odin.OdinMod
import com.odtheking.odin.OdinMod.logger
import com.odtheking.odin.features.impl.skyblock.ChatCommands.tipsJson
import com.odtheking.odin.features.impl.skyblock.ChatCommands.weightedItems
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import kotlin.jvm.javaClass

data class TipsData(
    @SerializedName("tips")
    val tips: List<String>
)

data class WeightedItem(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("weight")
    val weight: Double
)

data class WeightedItemsData(
    @SerializedName("items")
    val items: List<WeightedItem>
)

fun getRandomTip() = if (tipsJson.isNotEmpty()) tipsJson.random() else "null"

fun loadFiles() {
    try {
        val tipsIs = OdinMod.javaClass.getResourceAsStream("/assets/odin/json/tips.json") ?: throw IllegalArgumentException("Missing tips file")
        val itemIs = OdinMod.javaClass.getResourceAsStream("/assets/odin/json/weight.json") ?: throw IllegalArgumentException("Missing items file")

        InputStreamReader(tipsIs, StandardCharsets.UTF_8).use { reader ->
            val data = Gson().fromJson(reader, TipsData::class.java)
            tipsJson = data.tips
        }

        InputStreamReader(itemIs, StandardCharsets.UTF_8).use { reader ->
            val data = Gson().fromJson(reader, WeightedItemsData::class.java)
            val items = data.items.toMutableList()
            val total = items.sumOf { it.weight }
            if (total < 1.0) items.add(WeightedItem(null, 1.0 - total))

            weightedItems = items
        }
    } catch (e: Exception) {
        logger.error("Failed to load json file", e)
        tipsJson = listOf("null")
        weightedItems = emptyList()
    }
}

private fun rollOnce(): String? {
    if (weightedItems.isEmpty()) return null

    val totalWeight = weightedItems.sumOf { it.weight }
    if (totalWeight <= 0) return null

    val random = Math.random() * totalWeight
    var accumulated = 0.0

    for (item in weightedItems) {
        accumulated += item.weight
        if (random <= accumulated) {
            return item.name
        }
    }

    return null
}

private fun rollMultiple(times: Int): Map<String, Int> {
    val results = mutableMapOf<String, Int>()

    repeat(times) {
        val result = rollOnce()
        if (result != null) {
            results[result] = results.getOrDefault(result, 0) + 1
        }
    }

    return results
}

fun getRollResult(username: String, times: Int): String {
    val results = rollMultiple(times)

    if (results.isEmpty()) return "$username Nothing :<"

    val sb = StringBuilder("$username unlocked -> ")
    results.forEach { (item, count) ->
        sb.append("$item ×$count ")
    }

    return sb.toString().trim()
}