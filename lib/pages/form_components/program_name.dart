import 'package:flutter/material.dart';

class ProgramName extends StatefulWidget {
  final Function(String) onProgramNameChanged;
  const ProgramName({Key? key, required this.onProgramNameChanged})
      : super(key: key);

  @override
  State<ProgramName> createState() => _ProgramNameState();
}

class _ProgramNameState extends State<ProgramName> {
  @override
  Widget build(BuildContext context) {
    return TextFormField(
      decoration: const InputDecoration(
        labelText: "Program Name", // Placeholder
        labelStyle: TextStyle(
            color: Color.fromARGB(255, 0, 94, 92)), // Placeholder style
        focusedBorder: OutlineInputBorder(
          borderSide: BorderSide(color: Colors.orangeAccent),
        ), // Border when clicked
        enabledBorder: OutlineInputBorder(
          borderSide: BorderSide(color: Colors.black),
        ), // Default border
      ),
      cursorColor: Colors.orangeAccent,
      // logic to check whether the field is empty
      validator: (programNameValue) {
        if (programNameValue!.isEmpty) {
          return "Program Name cannot be empty";
        }
        return null;
      },
      onChanged: widget.onProgramNameChanged,
    );
  }
}
