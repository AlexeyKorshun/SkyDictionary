package com.alexeykorshun.skydictonary.network

import com.alexeykorshun.skydictonary.store.DictionaryStore

/**
 * @author Alexei Korshun on 02.04.2021.
 */
interface DictionaryDeserializer {

    fun parse(raw: String): List<DictionaryStore.State.Meanings>
}