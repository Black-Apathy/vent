package com.example.vent

import Model
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.vent.com.example.vent.utils.AuthTokenProvider
import com.example.vent.network.ApiConstants
import com.example.vent.utils.DateUtils.formatDate
import com.example.vent.utils.TimeUtils.formatTime
import androidx.core.net.toUri


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
        val eventParticipants: TextView = rootView.findViewById(R.id.eventParticipants)
        val eventStartDate: TextView = rootView.findViewById(R.id.eventStartDate)
        val eventEndDate: TextView = rootView.findViewById(R.id.eventEndDate)
        val eventStartTime: TextView = rootView.findViewById(R.id.eventStartTime)
        val eventEndTime: TextView = rootView.findViewById(R.id.eventEndTime)
        val btnDownloadPdf: Button = rootView.findViewById(R.id.btnDownloadPdf)

        eventName.text = event.name
        eventType.text = event.type
        eventParticipants.text = event.participants.toString()
        eventStartDate.text = event.startDate?.let { formatDate(it) } ?: "Unknown Date"
        eventEndDate.text = event.endDate?.let { formatDate(it) } ?: "Unknown Date"
        eventStartTime.text = formatTime(event.startTime)
        eventEndTime.text = formatTime(event.startTime)

        btnDownloadPdf.setOnClickListener {
            downloadPdf(event.id)
        }

        return rootView
    }

    private fun downloadPdf(eventId: String?) {
        if (eventId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Error: Event ID missing", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val downloadUrl = ApiConstants.getEventPdfUrl(eventId)
            val request = DownloadManager.Request(downloadUrl.toUri())

            val token = AuthTokenProvider.getAccessToken(requireContext())

            if (token != null) {
                request.addRequestHeader("Authorization", "Bearer $token")
            } else {
                Toast.makeText(requireContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show()
                return
            }

            request.setTitle("Event Report: ${event.name}")
            request.setDescription("Downloading official event report...")
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Event_${event.name}_Report.pdf")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            // Ensure network types (optional but good practice)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)

            val manager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)

            Toast.makeText(requireContext(), "Download started...", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Download failed: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
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
