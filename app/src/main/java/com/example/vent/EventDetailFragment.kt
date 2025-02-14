package com.example.vent

import Model
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.vent.utils.DateUtils.formatDate
import com.example.vent.utils.PdfUtils
import com.example.vent.utils.TimeUtils.formatTime


class EventDetailFragment : Fragment() {

    private lateinit var event: Model

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_event_detail, container, false)

        // Get data passed from the previous fragment
        event = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("event", Model::class.java) ?: throw IllegalArgumentException("Event data is required")
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("event") ?: throw IllegalArgumentException("Event data is required")
        }

        // Set event data to the views
        val eventName: TextView = rootView.findViewById(R.id.eventName)
        val eventType: TextView = rootView.findViewById(R.id.eventType)
        val eventStartDate: TextView = rootView.findViewById(R.id.eventStartDate)
        val eventEndDate: TextView = rootView.findViewById(R.id.eventEndDate)
        val eventStartTime: TextView = rootView.findViewById(R.id.eventStartTime)
        val eventEndTime: TextView = rootView.findViewById(R.id.eventEndTime)

        eventName.text = event.name
        eventType.text = event.type
        eventStartDate.text = event.startDate?.let { formatDate(it) } ?: "Unknown Date"
        eventEndDate.text = event.endDate?.let { formatDate(it) } ?: "Unknown Date"
        eventStartTime.text = formatTime(event.startTime)
        eventEndTime.text = formatTime(event.startTime)

        // Set up the "Download PDF" button
        val btnDownloadPdf: Button = rootView.findViewById(R.id.btnDownloadPdf)
        btnDownloadPdf.setOnClickListener {
            // Trigger PDF generation
            PdfUtils.createPdf(requireContext(), event)
        }

        return rootView
    }

    companion object {
        fun newInstance(event: Model): EventDetailFragment {
            val fragment = EventDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable("event", event)
            fragment.arguments = bundle
            return fragment
        }
    }
}
