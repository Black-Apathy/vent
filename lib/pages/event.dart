import 'package:flutter/material.dart';
// import 'package:vent/pages/form_components/program_name.dart';
// import 'package:vent/pages/form_components/program_type.dart';
// import 'package:vent/pages/form_components/no_of_participants.dart';
import 'package:vent/pages/form_components/date_picker.dart';
// import 'package:vent/pages/form_components/time_picker.dart';
import 'package:vent/pages/form_components/submit_button.dart';
import 'package:vent/pages/form_components/success_screen.dart';
import 'package:intl/intl.dart';
import 'package:vent/pages/api/insert.dart';
import 'package:logging/logging.dart';

class Event extends StatefulWidget {
  const Event({Key? key}) : super(key: key);

  @override
  State<Event> createState() => _EventState();
}

class _EventState extends State<Event> {
  final Logger _logger = Logger('Vent');

  late DateTime _now;
  late TimeOfDay _timeOfDay;
  late TextEditingController _startDate;
  late TextEditingController _endDate;
  late TextEditingController _startTime;
  late TextEditingController _endTime;
  late bool _isLoading;

  String? programName;
  String? programType;
  int? noofparticipants;
  DateTime startDate = DateTime.now();

  // for the program name
  // void updateProgramName(String? newName) {
  //   programName = newName;
  // }

  // // for the program type
  // void updateProgramType(String? newType) {
  //   programType = newType;
  // }

  // // for the numberfield
  // void updateNoOfParticipants(int? newNumber) {
  //   noofparticipants = newNumber;
  // }

  // for the date
  void updateStartDate(DateTime newDate) {
    setState(() {
      _startDate.text = DateFormat('DD-MM-YYYY').format(newDate);
      startDate = newDate;
      // validateDateRange(context);
    });
  }

  // void updateEndDate(DateTime newDate) {
  //   setState(() {
  //     _endDate.text = DateFormat('d/M/y').format(newDate);
  //     validateDateRange(context);
  //   });
  // }

  // // for the time
  // void updateStartTime(DateTime newDate) {
  //   setState(() {
  //     _startTime.text = DateFormat('H:mm').format(newDate);
  //     validateTimeRange(context);
  //   });
  // }

  // void updateEndTime(DateTime newDate) {
  //   setState(() {
  //     _endTime.text = DateFormat('H:mm').format(newDate);
  //     validateTimeRange(context);
  //   });
  // }

  // // for date checking
  // void validateDateRange(BuildContext context) {
  //   // checking whether the textfields are not empty
  //   if (_startDate.text.isNotEmpty && _endDate.text.isNotEmpty) {
  //     final startDate = DateFormat('d/M/y').parse(_startDate.text);
  //     final endDate = DateFormat('d/M/y').parse(_endDate.text);
  //     // startDate > endDate
  //     if (startDate.isAfter(endDate) || startDate.isAtSameMomentAs(endDate)) {
  //       alertBox(context, "Invalid Date Range!",
  //           "Start Date cannot be greater than End Date");
  //     }
  //     // endDate < startDate
  //     if (endDate.isBefore(startDate)) {
  //       alertBox(context, "Invalid Date Range!",
  //           "End Date cannot be smaller than Start Date");
  //     }
  //   }
  // }

  // // for the time logic
  // void validateTimeRange(BuildContext context) {
  //   if (_startTime.text.isNotEmpty && _endTime.text.isNotEmpty) {
  //     final startTime =
  //         TimeOfDay.fromDateTime(DateFormat.Hm().parse(_startTime.text));
  //     final endTime =
  //         TimeOfDay.fromDateTime(DateFormat.Hm().parse(_endTime.text));

  //     if (startTime.hour > endTime.hour ||
  //         (startTime.hour == endTime.hour &&
  //             startTime.minute >= endTime.minute)) {
  //       alertBox(context, "Invalid Time Range!",
  //           "Start Time cannot be greater than End Time");
  //     } else if (endTime.hour < startTime.hour ||
  //         (endTime.hour == startTime.hour &&
  //             endTime.minute <= startTime.minute)) {
  //       alertBox(context, "Invalid Time Range!",
  //           "End Time cannot be smaller than Start Time");
  //     } else if (endTime.hour == startTime.hour &&
  //         endTime.minute == startTime.minute) {
  //       alertBox(context, "Invalid Time Range!",
  //           "Start Time and End Time cannot be similar");
  //     }
  //   }
  // }

  // Sends the data when the submit button is clicked
  void submitEvent() async {
    if (
        // programName == null &&
        // programType == null &&
        // noofparticipants == null &&
        _startDate.text.isEmpty) {
      alertBox(context, "Empty text", "Please do not leave it blank...");
      return;
    }
    setState(() {
      _isLoading = true;
    });

    try {
      bool success = await sendDatatoPhp(startDate);
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
      if (success) {
        Navigator.push(
          context,
          MaterialPageRoute(builder: (context) => const SuccessScreen()),
        );
      } else {
        alertBox(context, "Sending Error",
            "An error occurred while sending the data to PHP");
      }
    } catch (error, stackTrace) {
      _logger.severe(
          'Error occurred while sending data to PHP', error, stackTrace);
      print("Error: $error");
      print("Stack Trace: $stackTrace");
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
      alertBox(context, "Sending Error Catch",
          "An error occurred while sending the data to PHP");
    }
  }

  // void submitEvent() async {
  //   if (
  //       // programName == null &&
  //       // programType == null &&
  //       // noofparticipants == null &&
  //       _startDate.text.isEmpty) {
  //     alertBox(context, "Empty text", "Please do not leave it blank...");
  //     return;
  //   }
  //   setState(() {
  //     _isLoading = true;
  //   });

  //   bool success = await sendDatatoPhp(startDate);
  //   if (mounted) {
  //     setState(() {
  //       _isLoading = false;
  //     });
  //   }
  //   if (success) {
  //     Navigator.push(
  //       context,
  //       MaterialPageRoute(builder: (context) => const SuccessScreen()),
  //     );
  //   } else {}
  // }

  @override
  void initState() {
    super.initState();
    _now = DateTime.now();
    _startDate = TextEditingController();
    _endDate = TextEditingController();
    _timeOfDay = TimeOfDay.now();
    _startTime = TextEditingController();
    _endTime = TextEditingController();
    _isLoading = false;
  }

  @override
  void dispose() {
    _startDate.dispose();
    _endDate.dispose();
    _startTime.dispose();
    _endTime.dispose();
    super.dispose();
  }

  // for the alert box
  void alertBox(
      BuildContext context, String titlemessage, String contentmessage) {
    showDialog(
        context: context,
        builder: (context) => AlertDialog(
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10)),
              title: Text(titlemessage),
              titleTextStyle:
                  const TextStyle(color: Color.fromARGB(255, 255, 249, 244)),
              content: Text(contentmessage),
              contentTextStyle:
                  const TextStyle(color: Color.fromARGB(200, 255, 249, 244)),
              actions: [
                TextButton(
                  onPressed: () {
                    Navigator.pop(context);
                  },
                  child: const Text("Ok",
                      style:
                          TextStyle(color: Color.fromARGB(255, 253, 253, 253))),
                )
              ],
              elevation: 10,
              backgroundColor: Colors.redAccent,
            ));
  }

  @override
  Widget build(BuildContext context) {
    _startDate.text = DateFormat('d/M/y').format(_now);
    _endDate.text = DateFormat('d/M/y').format(_now);
    _startTime.text = _timeOfDay.format(context);
    _endTime.text = _timeOfDay.format(context);

    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 255, 249, 244),
      appBar: AppBar(
        toolbarHeight: 70,
        elevation: 0,
        backgroundColor: Colors.orangeAccent,
        title: const Text(
          "Event Registration",
          style: TextStyle(
              fontFamily: 'Rufina',
              fontSize: 21,
              fontWeight: FontWeight.w700,
              color: Color.fromRGBO(19, 19, 19, 1)),
        ),
        leading: Image.asset(
          'assets/pictures/logo_300.png',
          scale: 7.5,
        ),
        centerTitle: true,
      ),
      body: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.fromLTRB(16, 50, 16, 10),
          child: Column(
            children: [
              // // Program Name
              // ProgramName(
              //   onProgramNameChanged: updateProgramName,
              // ),
              // const SizedBox(
              //   height: 50,
              // ),

              // // Program Type
              // ProgramType(onProgramTypeChanged: updateProgramType),
              // const SizedBox(
              //   height: 50,
              // ),

              // // Number of Participants
              // NoOfParticipants(
              //     onNoOfParticipantsChanged: updateNoOfParticipants),
              // const SizedBox(
              //   height: 80,
              // ),

              // // Start and End Date
              Row(
                children: [
                  Expanded(
                      child: DatePicker(
                    controllerDate: _startDate,
                    labelDate: "Start Date",
                    onDateSelected: updateStartDate,
                  )),
                  const SizedBox(
                    width: 16,
                  ),
                  //     Expanded(
                  //         child: DatePicker(
                  //       controllerDate: _endDate,
                  //       labelDate: "End Date",
                  //       onDateSelected: updateEndDate,
                  //     ))
                ],
              ),
              const SizedBox(
                height: 50,
              ),
              // // Start and end time
              // Row(
              //   children: [
              //     Expanded(
              //         flex: 1,
              //         child: TimePicker(
              //           controllerTime: _startTime,
              //           labelTime: "Start Time",
              //           onTimeSelected: updateStartTime,
              //         )),
              //     const SizedBox(
              //       width: 16,
              //     ),
              //     Expanded(
              //       flex: 1,
              //       child: TimePicker(
              //           controllerTime: _endTime,
              //           labelTime: "End Time",
              //           onTimeSelected: updateEndTime),
              //     ),
              //   ],
              // ),
              // const SizedBox(
              //   height: 100,
              // ),
              // Submit Button
              SubmitButton(isLoading: _isLoading, onPressed: submitEvent),
            ],
          ),
        ),
      ),
    );
  }
}
