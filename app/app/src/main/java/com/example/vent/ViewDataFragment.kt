package com.example.vent

import Model
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.RequestQueue
import com.example.vent.network.UserApiService.viewEvents
import org.json.JSONArray
import org.json.JSONObject

class ViewDataFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: EventAdapter
    private val events = mutableListOf<Model>()

    private val dynamicTitle = "View Events"

    private var progressBar: ProgressBar? = null
    private var progressText: TextView? = null
    private var requestQueue: RequestQueue? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (requireActivity() != null) {
            requireActivity()!!.setTitle(dynamicTitle)
        }
        val rootView = inflater.inflate(R.layout.fragment_view_details, container, false)

        // Initialize UI elements
        listView = rootView.findViewById(R.id.programListView)
        progressBar = rootView.findViewById(R.id.progressBar)
        progressText = rootView.findViewById(R.id.progressText)

        // Initialize adapter safely
        context?.let {
            adapter = EventAdapter(it, events)
            listView.adapter = adapter
        }

        // Fetch events when fragment loads
        fetchEvents()

        // Handle item click safely
        listView.setOnItemClickListener { _, _, position, _ ->
            Log.d("ViewDataFragment", "Clicked position: $position, Events size: ${events.size}")

            if (position >= events.size) {
                Log.e("ViewDataFragment", "Invalid position: $position")
                return@setOnItemClickListener
            }

            val event = events[position]
            Log.d("ViewDataFragment", "Event selected: $event")

            val eventDetailFragment = EventDetailFragment.newInstance(event)

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, eventDetailFragment)
                ?.addToBackStack(null)
                ?.commit()
        }


        return rootView
    }

    private fun fetchEvents() {
        // Show loading
        progressBar?.visibility = View.VISIBLE
        progressText?.visibility = View.VISIBLE
        progressText?.text = "Fetching data, please wait..."

        context?.let { ctx ->
            if (!isInternetAvailable(ctx)) {
                progressBar?.visibility = View.GONE
                progressText?.text = "No internet connection. Please check your network."
                return
            }

            // Use your API wrapper
            viewEvents(ctx) { success, result ->
                if (!isAdded) return@viewEvents

                progressBar?.visibility = View.GONE

                if (success) {
                    try {
                        val response = JSONArray(result)
                        progressText?.visibility = View.GONE
                        updateEventList(response)
                    } catch (e: Exception) {
                        progressText?.text = "Unexpected data format."
                        Log.e("ViewDataFragment", "Error parsing JSONArray", e)
                    }
                } else {
                    progressText?.text = result // show the error string
                    Toast.makeText(ctx, result, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateEventList(response: JSONArray) {
        events.clear()
        for (i in 0 until response.length()) {
            val jsonObject: JSONObject = response.getJSONObject(i)
            val event = Model(
                jsonObject.getString("event_id"),
                jsonObject.getString("Program_Name"),
                jsonObject.getString("Program_Type"),
                jsonObject.getString("No_of_Participants"),
                jsonObject.getString("Start_Date"),
                jsonObject.optString("End_Date", "N/A"),
                jsonObject.getString("Start_Time"),
                jsonObject.optString("End_Time", "N/A")
            )
            events.add(event)
        }
        adapter.notifyDataSetChanged()
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requestQueue?.cancelAll { true } // Cancel any pending requests to prevent memory leaks
    }
}