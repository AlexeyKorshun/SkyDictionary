package com.alexeykorshun.android.skydictonary.ui.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.alexeykorshun.android.skydictonary.R
import com.alexeykorshun.android.skydictonary.extension.argument
import com.alexeykorshun.skydictonary.ui.list.ListView.MeaningItem
import kotlinx.android.synthetic.main.f_details.view.*

/**
 * @author Alexey Korshun on 30.09.2020.
 */
class MeaningDetailsFragment : Fragment(R.layout.f_details) {

    companion object {

        fun newInstance(item: MeaningItem): Fragment =
            MeaningDetailsFragment()
                .apply { meaningItem = item.toString() }
    }

    private var meaningItem: String by argument()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.listTranslations.adapter = TranslationAdapter(MeaningItem.fromString(meaningItem).translations)
        requireActivity().title = MeaningItem.fromString(meaningItem).text
    }
}