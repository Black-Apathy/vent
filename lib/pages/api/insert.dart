import 'package:logging/logging.dart';
import 'package:intl/intl.dart';
import 'package:http/http.dart' as http;

final Logger _logger = Logger("insert");

String phpurl = "http://localhost/flutter_php/insert_data.php";
// String phpurl = "localhost:80";
// String phpurl = "https://files.000webhost.com/insert_data.php";

Future<bool> sendDatatoPhp(
  // String? programName,
  // String? programType,
  // int? noOfParticipants,
  DateTime startDate,
  // DateTime endDate,
  // DateTime startTime,
  // DateTime endTime
) async {
  // try {
  String formattedDate = DateFormat('YYYY-MM-DD').format(startDate);
  var response = await http.post(Uri.parse(phpurl), body: {
    // 'programName': programName,
    // 'programType': programType,
    // 'noOfParticipants': noOfParticipants.toString(),
    'startDate': formattedDate.toString(),
    // 'endDate': endDate,
    // 'startTime': startTime,
    // 'endTime': endTime,
  });
  _logger.info("Server Response: ${response.body}");
  if (response.statusCode == 200) {
    _logger.info("Event registered successfully!");
    return true; // Data sent successfully
  } else {
    _logger.warning("Error: ${response.statusCode}");
    return false; // Error occurred while sending data
  }
  // } catch (error, stackTrace) {
  //   _logger.severe(
  //       "Error: occurred while sending data to PHP", error, stackTrace);
  //   return false; // Error occurred while sending data
  // }
}
