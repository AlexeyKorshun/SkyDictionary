package com.alexeykorshun.android.skydictonary.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.alexeykorshun.android.skydictonary.R
import com.alexeykorshun.android.skydictonary.ui.details.MeaningDetailsFragment
import com.alexeykorshun.skydictonary.network.DictionaryApiImpl
import com.alexeykorshun.skydictonary.ui.list.ListView.MeaningItem
import com.alexeykorshun.skydictonary.ui.list.ListViewController
import com.arkivanov.mvikotlin.extensions.androidx.instancekeeper.getInstanceKeeperProvider
import com.arkivanov.mvikotlin.extensions.androidx.lifecycle.asMviLifecycle

/**
 * @author Alexei Korshun on 29.09.2020.
 */
class ListFragment : Fragment() {

    private val controller: ListViewController by lazy {
        ListViewController(
            getInstanceKeeperProvider(),
            lifecycle.asMviLifecycle(),
            DictionaryApiImpl(),
            this::showDetails
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        ComposeView(requireContext())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller.onViewCreated(ListViewImpl(view as ComposeView))
        requireActivity().title = getString(R.string.app_name)
    }

    private fun showDetails(item: MeaningItem) {
        parentFragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.container, MeaningDetailsFragment.newInstance(item))
            .addToBackStack(MeaningDetailsFragment::class.java.canonicalName)
            .commit()
    }
}