package com.example.vent

import Model
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.* // This includes the getValue/setValue delegates
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.vent.network.UserApiService.viewEvents
import com.example.vent.utils.AnimationUtils
import org.json.JSONArray

class ViewDataFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().setTitle("View Events")

        return ComposeView(requireContext()).apply {
            setContent {
                // The 'by' delegate now works because of the runtime imports
                var isLoading by remember { mutableStateOf(true) }
                var eventList by remember { mutableStateOf(emptyList<Model>()) }

                LaunchedEffect(Unit) {
                    fetchEvents { fetchedEvents ->
                        eventList = fetchedEvents
                        isLoading = false
                    }
                }

                if (isLoading) {
                    AnimationUtils.LoadingScreen()
                } else {
                    ViewEventsScreen(events = eventList) { selectedEvent ->
                        val eventDetailFragment = EventDetailFragment.newInstance(selectedEvent)
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, eventDetailFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
        }
    }

    private fun fetchEvents(onDataLoaded: (List<Model>) -> Unit) {
        context?.let { ctx ->
            viewEvents(ctx) { success, result ->
                if (success) {
                    try {
                        val response = JSONArray(result)
                        val newList = mutableListOf<Model>()
                        for (i in 0 until response.length()) {
                            val json = response.getJSONObject(i)
                            newList.add(Model(
                                json.getString("event_id"),
                                json.getString("Program_Name"),
                                json.getString("Program_Type"),
                                json.getString("No_of_Participants"),
                                json.getString("Start_Date"),
                                json.optString("End_Date", "N/A"),
                                json.getString("Start_Time"),
                                json.optString("End_Time", "N/A")
                            ))
                        }
                        onDataLoaded(newList)
                    } catch (e: Exception) {
                        Log.e("ViewDataFragment", "Parsing error", e)
                        onDataLoaded(emptyList())
                    }
                } else {
                    onDataLoaded(emptyList())
                }
            }
        }
    }
}