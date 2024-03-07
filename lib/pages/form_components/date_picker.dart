import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class DatePicker extends StatefulWidget {
  final TextEditingController controllerDate;
  final String labelDate;
  final Function(DateTime) onDateSelected;

  const DatePicker({
    Key? key,
    required this.controllerDate,
    required this.labelDate,
    required this.onDateSelected,
  }) : super(key: key);

  @override
  State<DatePicker> createState() => _DatePickerState();
}

class _DatePickerState extends State<DatePicker> {
  DateTime _selectedDate = DateTime.now();

  @override
  void initState() {
    super.initState();
    widget.controllerDate.text = DateFormat('d/M/y').format(_selectedDate);
  }

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? pickedDate = await showDatePicker(
      context: context,
      initialDate: _selectedDate,
      firstDate: DateTime(2023),
      lastDate: DateTime(2100),
      builder: (context, child) {
        return Theme(
          data: Theme.of(context).copyWith(
            colorScheme: const ColorScheme.light(
              primary: Color.fromARGB(255, 0, 94, 92),
              onPrimary: Color.fromARGB(255, 255, 249, 244),
              onSurface: Color.fromARGB(255, 184, 104, 0),
            ),
          ),
          child: child!,
        );
      },
    );

    if (pickedDate != null) {
      setState(() {
        _selectedDate = pickedDate;
        widget.controllerDate.text = DateFormat('d/M/y').format(_selectedDate);
      });
    }
    widget.onDateSelected(_selectedDate);
  }

  @override
  Widget build(BuildContext context) {
    final InputDecoration inputDecoration = InputDecoration(
      labelText: widget.labelDate,
      labelStyle: const TextStyle(
        color: Color.fromARGB(255, 0, 94, 92),
      ),
      focusedBorder: const OutlineInputBorder(
        borderRadius: BorderRadius.all(Radius.circular(20)),
        borderSide: BorderSide(color: Color.fromARGB(255, 0, 94, 92)),
      ),
      enabledBorder: const OutlineInputBorder(
        borderRadius: BorderRadius.all(Radius.circular(20)),
      ),
    );

    return TextFormField(
      decoration: inputDecoration,
      cursorColor: const Color.fromARGB(255, 0, 94, 92),
      controller: widget.controllerDate,
      readOnly: true,
      onTap: () {
        FocusScope.of(context)
            .requestFocus(FocusNode()); // Remove focus from the TextField
        _selectDate(context); // Show the date picker dialog
      },
    );
  }
}
