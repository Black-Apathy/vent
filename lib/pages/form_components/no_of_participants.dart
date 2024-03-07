import 'package:flutter/material.dart';

class NoOfParticipants extends StatefulWidget {
  final Function(int?) onNoOfParticipantsChanged;
  const NoOfParticipants({Key? key, required this.onNoOfParticipantsChanged})
      : super(key: key);

  @override
  State<NoOfParticipants> createState() => _NoOfParticipantsState();
}

class _NoOfParticipantsState extends State<NoOfParticipants> {
  @override
  Widget build(BuildContext context) {
    return TextFormField(
      keyboardType: TextInputType.number,
      decoration: const InputDecoration(
        labelText: "Number of Participants",
        labelStyle: TextStyle(
          color: Color.fromARGB(255, 0, 94, 92),
        ),
        focusedBorder: OutlineInputBorder(
            borderSide: BorderSide(color: Colors.orangeAccent)),
        enabledBorder: OutlineInputBorder(
            borderSide: BorderSide(
          color: Colors.black,
        )),
      ),
      cursorColor: Colors.orangeAccent,
      onChanged: (value) {
        final parsedValue = int.tryParse(value);
        widget.onNoOfParticipantsChanged(parsedValue ?? 0);
      },
    );
  }
}
