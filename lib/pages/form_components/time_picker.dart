import 'package:flutter/material.dart';

class TimePicker extends StatefulWidget {
  final TextEditingController controllerTime;
  final String labelTime;
  final Function(DateTime) onTimeSelected;
  const TimePicker(
      {Key? key,
      required this.controllerTime,
      required this.labelTime,
      required this.onTimeSelected})
      : super(key: key);

  @override
  State<TimePicker> createState() => _TimePickerState();
}

class _TimePickerState extends State<TimePicker> {
  late TimeOfDay _timeOfDay;
  late TextEditingController _textEditingController;

  @override
  void initState() {
    super.initState();
    _timeOfDay = TimeOfDay.now();
    _textEditingController = TextEditingController();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    _textEditingController.text = _timeOfDay.format(context).toString();
  }

  @override
  void dispose() {
    _textEditingController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final InputDecoration inputDecoration = InputDecoration(
      labelText: widget.labelTime,
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
      controller: _textEditingController,
      readOnly: true,
      onTap: () {
        showTimePicker(
          context: context,
          initialTime: _timeOfDay,
          builder: (context, child) {
            return MediaQuery(
              data:
                  MediaQuery.of(context).copyWith(alwaysUse24HourFormat: false),
              child: Theme(
                data: ThemeData(
                  colorScheme:
                      ColorScheme.fromSwatch(brightness: Brightness.light)
                          .copyWith(primary: Colors.orangeAccent),
                  dialogBackgroundColor:
                      const Color.fromARGB(255, 255, 249, 244),
                ),
                child: child!,
              ),
            );
          },
        ).then((selectedTime) {
          if (selectedTime != null) {
            setState(() {
              _timeOfDay = selectedTime;
              _textEditingController.text =
                  _timeOfDay.format(context).toString();
            });
          }
        });
      },
    );
  }
}
