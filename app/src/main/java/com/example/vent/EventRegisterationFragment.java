package com.example.vent;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class EventRegisterationFragment extends Fragment {

    private final String insertUrl = "https://2i8zfg-ip-122-170-2-205.tunnelmole.net/submit";
    private String dynamicTitle = "Event Registration"; // Set this based on the current state

    private DrawerLayout drawerLayout;
    private ImageView hamburger;
    private NavigationView navMenu;

    private String[] programTypes = {"Educational", "Cultural", "WorkShop"};

    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapterItems;

    private TextInputLayout startDate, endDate, startTime, endTime;
    private TextInputEditText programNametxt, startDatetxt, endDatetxt, startTimetxt, endTimetxt, defaulttxt;

    private TextView startDateError, startTimeError, endDateError, endTimeError;

    private Button submitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getActivity() != null) {
            getActivity().setTitle(dynamicTitle);
        }
        View view = inflater.inflate(R.layout.fragment_event_registeration, container, false);

        // Initialize UI components
        initializeUI(view);

        return view;
    }

    private void initializeUI(View view) {
        // Program Type DropDown Selector
        autoCompleteTextView = view.findViewById(R.id.programTypeId);
        if (autoCompleteTextView == null) {
            Log.e("EventRegisterationFragment", "autoCompleteTextView is null");
            return;
        }
        autoCompleteTextView.setContentDescription("Select a program type from the dropdown");

        adapterItems = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, programTypes);
        autoCompleteTextView.setAdapter(adapterItems);

        programNametxt = view.findViewById(R.id.programName);
        startDate = view.findViewById(R.id.startdatelayout);
        startDatetxt = view.findViewById(R.id.startdateinnertxt);
        endDate = view.findViewById(R.id.enddatelayout);
        endDatetxt = view.findViewById(R.id.enddateinnertxt);
        startTime = view.findViewById(R.id.starttimelayout);
        startTimetxt = view.findViewById(R.id.starttimeinnertxt);
        endTime = view.findViewById(R.id.endtimelayout);
        endTimetxt = view.findViewById(R.id.endtimeinnertxt);
        endDateError = view.findViewById(R.id.endDateError);
        endTimeError = view.findViewById(R.id.endTimeError);
        submitButton = view.findViewById(R.id.submitButton);

        // Program Name focus listener
        if (programNametxt != null) {
            programNametxt.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    showKeyboard(v);
                } else {
                    hideKeyboard(v);
                }
            });

            // Program Name click listener
            programNametxt.setOnClickListener(v -> {
                programNametxt.setFocusable(true);
                programNametxt.setFocusableInTouchMode(true);
                programNametxt.requestFocus(); // Set focus to Program Name
            });
        }

        if (autoCompleteTextView != null) {
            autoCompleteTextView.setOnClickListener(v -> {
                programNametxt.setFocusable(false);
                hideKeyboard(autoCompleteTextView); // Hide keyboard if accidentally shown
                autoCompleteTextView.showDropDown(); // Show dropdown manually
            });
        }

        if (startDatetxt != null) {
            startDatetxt.setOnClickListener(v -> {
                programNametxt.setFocusable(false); // Disable focus for Program Name
                hideKeyboard(programNametxt);
                showDatePicker(v);
            });
        }

        if (endDatetxt != null) {
            endDatetxt.setOnClickListener(v -> {
                programNametxt.setFocusable(false); // Disable focus for Program Name
                hideKeyboard(programNametxt);
                showDatePicker(v);
            });
        }

        if (startTimetxt != null) {
            startTimetxt.setOnClickListener(v -> {
                programNametxt.setFocusable(false); // Disable focus for Program Name
                hideKeyboard(programNametxt);
                showTimePicker(v);
            });
        }

        if (endTimetxt != null) {
            endTimetxt.setOnClickListener(v -> {
                programNametxt.setFocusable(false); // Disable focus for Program Name
                hideKeyboard(programNametxt);
                showTimePicker(v);
            });
        }

        // Submit Button listener
        submitButton.setOnClickListener(v -> sendData());
    }

    private void showKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    // Date Picker Function
    private void showDatePicker(View v) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Identify which TextView is clicked (Start or End Date)
        defaulttxt = (v.getId() == R.id.startdateinnertxt) ? startDatetxt : endDatetxt;

        DatePickerDialog dialog = new DatePickerDialog(requireContext(), (view, y, m, dOM) -> {
            // Format the selected date with leading zeros for single-digit days and months
            String selectedDate = String.format("%02d/%02d/%d", dOM, (m + 1), y);

            // Update the TextView with the formatted date
            defaulttxt.setText(selectedDate); // Update the text field

            // Real-time validation: Check if Start Date and End Date are valid
            String startDate = startDatetxt.getText().toString();
            String endDate = endDatetxt.getText().toString();

            // Check if both dates are set before validating
            if (!startDate.isEmpty() && !endDate.isEmpty()) {
                // Call centralized date validation and pass the error text views
                boolean areDatesValid = validateDates(startDate, endDate);

                if (areDatesValid) {
                    // If dates are valid, you can do something here (e.g., remove any error messages)
                    if (startDateError != null) startDateError.setVisibility(View.GONE);
                    if (endDateError != null) endDateError.setVisibility(View.GONE);
                }
            }

        }, year, month, day);

        dialog.show();
    }



    // Time Picker Function

    private void showTimePicker(View v) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR); // 12-hour format
        int minute = calendar.get(Calendar.MINUTE);

        // Identify which TextView is clicked (Start or End Time)
        defaulttxt = (v.getId() == R.id.starttimeinnertxt) ? startTimetxt : endTimetxt;

        TimePickerDialog dialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minuteOfHour) -> {
            // Format the selected time in 12-hour format with AM/PM
            String selectedTime = String.format("%02d:%02d %s", hourOfDay == 0 ? 12 : (hourOfDay > 12 ? hourOfDay - 12 : hourOfDay), minuteOfHour, hourOfDay < 12 ? "AM" : "PM");

            // Update the TextView with the formatted time
            defaulttxt.setText(selectedTime); // Update the text field

            // Real-time validation: Check if Start Time and End Time are valid
            String startTime = startTimetxt.getText().toString();
            String endTime = endTimetxt.getText().toString();

            // Check if both times are set before validating
            if (!startTime.isEmpty() && !endTime.isEmpty()) {
                // Call centralized time validation
                boolean areTimesValid = validateTimes(startTime, endTime);

                if (areTimesValid) {
                    // If times are valid, you can do something here (e.g., remove any error messages)
                    if (startTimeError != null) startTimeError.setVisibility(View.GONE);
                    if (endTimeError != null) endTimeError.setVisibility(View.GONE);
                }
            }

        }, hour, minute, false); // false indicates 12-hour format

        dialog.show();
    }

    // Central Date Validation

    private boolean validateDates(String startDate, String endDate) {
        // Validate Date Formats and Comparison
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date startDateParsed = sdf.parse(startDate);
            Date endDateParsed = sdf.parse(endDate);

            if (startDateParsed == null || endDateParsed == null) {
                if (startDatetxt != null) {
                    startDateError.setText("Invalid date format");
                    startDateError.setVisibility(View.VISIBLE);
                }
                if (endDatetxt != null) {
                    endDateError.setText("Invalid date format");
                    endDateError.setVisibility(View.VISIBLE);
                }
                return false;
            }

            // Ensure End Date is after Start Date
            if (endDateParsed.before(startDateParsed)) {
                if (endDatetxt != null) {
                    endDateError.setText("End Date must be after Start Date");
                    endDateError.setVisibility(View.VISIBLE);
                } else {
                    endDateError.setVisibility(View.GONE);
                }
                return false;
            }

            // Hide error messages if everything is valid
            if (startDateError != null) startDateError.setVisibility(View.GONE);
            if (endDateError != null) endDateError.setVisibility(View.GONE);

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error in date parsing", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true; // All date validations passed
    }

    // Central Time Validation

    private boolean validateTimes(String startTime, String endTime) {
        // Validate Time Formats
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault()); // 12-hour format with AM/PM

        try {
            Date startTimeParsed = timeFormat.parse(startTime);
            Date endTimeParsed = timeFormat.parse(endTime);

            // Check if parsing was successful
            if (startTimeParsed == null || endTimeParsed == null) {
                // Show error messages
                if (startTimetxt != null) {
                    startTimeError.setText("Invalid time format");
                    startTimeError.setVisibility(View.VISIBLE);
                }
                if (endTimetxt != null) {
                    endTimeError.setText("Invalid time format");
                    endTimeError.setVisibility(View.VISIBLE);
                }
                return false;
            }

            // Ensure End Time is after Start Time (not equal, not before)
            if (endTimeParsed.before(startTimeParsed) || endTimeParsed.equals(startTimeParsed)) {
                if (endTimetxt != null) {
                    endTimeError.setText("End Time must be after Start Time");
                    endTimeError.setVisibility(View.VISIBLE);
                }
                return false;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error in time parsing", Toast.LENGTH_SHORT).show();
            return false;
        }

        // If all validations pass
        return true;
    }


    // Validate Input Fields
    private boolean isInputValid(String programName, String programType, String startDate, String endDate, String startTime, String endTime) {
        // Check if any of the required fields are empty

        // Check if Program Name is empty
        if (programName.isEmpty()) {
            if (programNametxt != null) {
                programNametxt.setError("Program Name cannot be blank");
            }
            return false;
        } else {
            if (programNametxt != null) {
                programNametxt.setError(null);  // Clear the error if the field is filled
            }
        }

        // Check if program name is between 3 to 50 characters
        if(programName.length() < 3 || programName.length() > 50) {
            if (programNametxt != null) {
                programNametxt.setError("Program Name must be between 3 to 50 characters");
            }
            return false;
        }

        // Check if Program Type is empty
        if (programType.isEmpty()) {
            if (autoCompleteTextView != null) {
                autoCompleteTextView.setError("Program Type cannot be blank");
            }
            return false;
        } else {
            if (autoCompleteTextView != null) {
                autoCompleteTextView.setError(null);  // Clear the error if the field is filled
            }
        }

        // Check if Start Date is empty
        if (startDate.isEmpty()) {
            if (startDatetxt != null) {
                startDatetxt.setError("Start Date cannot be blank");
            }
            return false;
        } else {
            if (startDatetxt != null) {
                startDatetxt.setError(null);  // Clear the error if the field is filled
            }
        }

        // Check if End Date is empty
        if (endDate.isEmpty()) {
            if (endDatetxt != null) {
                endDatetxt.setError("End Date cannot be blank");
            }
            return false;
        } else {
            if (endDatetxt != null) {
                endDatetxt.setError(null);  // Clear the error if the field is filled
            }
        }

        // Check if Start Time is empty
        if (startTime.isEmpty()) {
            if (startTimetxt != null) {
                startTimetxt.setError("Start Time cannot be blank");
            }
            return false;
        } else {
            if (startTimetxt != null) {
                startTimetxt.setError(null);  // Clear the error if the field is filled
            }
        }

        // Check if End Time is empty
        if (endTime.isEmpty()) {
            if (endTimetxt != null) {
                endTimetxt.setError("End Time cannot be blank");
            }
            return false;
        } else {
            if (endTimetxt != null) {
                endTimetxt.setError(null);  // Clear the error if the field is filled
            }
        }

        // Validate Dates using the centralized function
        if (!validateDates(startDate, endDate)) {
            return false; // Return false if date validation fails
        }

        // Validate Times using the centralized function
        if (!validateTimes(startTime, endTime)) {
            return false; // Return false if time validation fails
        };

        // If all validations pass
        return true;
    }

    // Submit Form Data
    private void sendData() {
        String programName = Objects.requireNonNull(programNametxt.getText()).toString();
        String programType = Objects.requireNonNull(autoCompleteTextView.getText()).toString();
        String startDate = Objects.requireNonNull(startDatetxt.getText()).toString();
        String endDate = Objects.requireNonNull(endDatetxt.getText()).toString();
        String startTime = Objects.requireNonNull(startTimetxt.getText()).toString();
        String endTime = Objects.requireNonNull(endTimetxt.getText()).toString();

        if (isInputValid(programName, programType, startDate, endDate, startTime, endTime)) {
            ProgressDialog progressDialog = new ProgressDialog(requireContext());
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST, insertUrl,
                    response -> {
                        progressDialog.dismiss();
                        Log.d("VolleySuccess", response);  // Log the server response
                        handleResponse(response);
                    },
                    error -> {
                        progressDialog.dismiss();
                        error.printStackTrace();
                        Log.e("VolleyError", error.toString());  // Log network errors
                        if (error.networkResponse != null) {
                            Log.e("VolleyErrorBody", new String(error.networkResponse.data));  // Log error body if available
                        }
                        Toast.makeText(requireContext(), "Volley Error: " + error, Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("pn", programName);
                    params.put("pt", programType);
                    params.put("sd", startDate);
                    params.put("ed", endDate);
                    params.put("st", startTime);
                    params.put("et", endTime);
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
            requestQueue.add(stringRequest);
        }
    }

    // Handle Response from Server
    private void handleResponse(String response) {
        try {
            Log.d("ServerResponse", response);
            JSONObject jsonObject = new JSONObject(response);
            String message = jsonObject.getString("message");
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}