package com.example.vent

import Model
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class ViewDataFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var adapter: EventAdapter
    private val events = mutableListOf<Model>()

    private var progressBar: ProgressBar? = null
    private var progressText: TextView? = null
    private var requestQueue: RequestQueue? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        val url = "https://h3edlm-ip-122-170-2-205.tunnelmole.net/data"

        context?.let { ctx ->
            requestQueue = Volley.newRequestQueue(ctx)

            // Show loading indicator safely
            progressBar?.visibility = View.VISIBLE
            progressText?.visibility = View.VISIBLE
            progressText?.text = "Fetching data, please wait..."

            // Check for internet connection
            if (!isInternetAvailable(ctx)) {
                progressBar?.visibility = View.GONE
                progressText?.text = "No internet connection. Please check your network."
                return
            }

            val jsonArrayRequest = JsonArrayRequest(
                Request.Method.GET, url, null,
                { response: JSONArray ->
                    if (isAdded) {
                        progressBar?.visibility = View.GONE
                        progressText?.visibility = View.GONE
                        updateEventList(response)
                    }
                },
                { error ->
                    if (isAdded) {
                        progressBar?.visibility = View.GONE
                        progressText?.text = when (error.networkResponse?.statusCode) {
                            404 -> "Server not found (404). Please try again later."
                            500 -> "Server error (500). Contact support."
                            else -> "Failed to load data. Server might be down or unreachable."
                        }
                        Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                }
            )

            jsonArrayRequest.retryPolicy = DefaultRetryPolicy(
                5000, // 5-second timeout
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            requestQueue?.add(jsonArrayRequest)
        }
    }

    private fun updateEventList(response: JSONArray) {
        events.clear()
        for (i in 0 until response.length()) {
            val jsonObject: JSONObject = response.getJSONObject(i)
            val event = Model(
                jsonObject.getString("Program_Name"),
                jsonObject.getString("Program_Type"),
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
