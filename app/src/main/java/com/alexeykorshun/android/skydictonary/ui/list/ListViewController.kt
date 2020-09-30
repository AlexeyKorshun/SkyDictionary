package com.alexeykorshun.android.skydictonary.ui.list

import com.alexeykorshun.android.skydictonary.network.DictionaryApiImpl
import com.alexeykorshun.android.skydictonary.store.DictionaryStore
import com.alexeykorshun.android.skydictonary.store.DictionaryStoreFactory
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.core.instancekeeper.InstanceKeeperProvider
import com.arkivanov.mvikotlin.core.instancekeeper.getOrCreateStore
import com.arkivanov.mvikotlin.core.lifecycle.Lifecycle
import com.arkivanov.mvikotlin.extensions.reaktive.bind
import com.arkivanov.mvikotlin.extensions.reaktive.events
import com.arkivanov.mvikotlin.extensions.reaktive.states
import com.arkivanov.mvikotlin.logging.store.LoggingStoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.badoo.reaktive.observable.map
import com.badoo.reaktive.observable.mapNotNull

/**
 * @author Alexei Korshun on 29.09.2020.
 */
class ListViewController(
    instanceKeeperProvider: InstanceKeeperProvider,
    private val lifecycle: Lifecycle,
    private val dictionaryApi: DictionaryApiImpl,
    private val outputConsumer: (MeaningItem) -> Unit
) {

    private val store = instanceKeeperProvider.get<DictionaryStore>("dictionary_store")
        .getOrCreateStore(this::createStore)

    fun onViewCreated(listView: ListView) {
        bind(lifecycle, BinderLifecycleMode.START_STOP) {
            listView.events.mapNotNull(eventToIntent) bindTo store
        }

        bind(lifecycle, BinderLifecycleMode.START_STOP) {
            store.states.map(stateToModel) bindTo listView
        }

        bind(lifecycle, BinderLifecycleMode.START_STOP) {
            listView.events.mapNotNull(eventToOutput) bindTo outputConsumer
        }
    }

    private fun createStore() = LoggingStoreFactory(DefaultStoreFactory)
        .let { storeFactory -> DictionaryStoreFactory(storeFactory, dictionaryApi).create() }

}

private val eventToOutput: ListView.Event.() -> MeaningItem? = {
    when (this) {
        is ListView.Event.Translate -> null
        is ListView.Event.Clicked -> item
    }
}

private val eventToIntent: ListView.Event.() -> DictionaryStore.Intent? = {
    when (this) {
        is ListView.Event.Translate -> DictionaryStore.Intent.Find(word)
        is ListView.Event.Clicked -> null
    }
}

private val stateToModel: DictionaryStore.State.() -> ListView.Model = {
    ListView.Model(error, progress, meanings.asMeaningItems())
}

private fun List<DictionaryStore.State.Meanings>.asMeaningItems(): List<MeaningItem> = asSequence()
    .map { meaning -> MeaningItem(meaning.text, meaning.translations.asItems()) }
    .toList()

private fun List<DictionaryStore.State.Translation>.asItems(): List<MeaningItem.TranslationItem> = asSequence()
    .map { translation -> MeaningItem.TranslationItem(translation.translation, translation.images.full, translation.images.preview) }
    .toList()