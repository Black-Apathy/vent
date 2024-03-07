import 'package:flutter/material.dart';
import 'package:dropdown_search/dropdown_search.dart';

class ProgramType extends StatefulWidget {
  final Function(String?) onProgramTypeChanged;
  const ProgramType({Key? key, required this.onProgramTypeChanged})
      : super(key: key);

  @override
  State<ProgramType> createState() => _ProgramTypeState();
}

class _ProgramTypeState extends State<ProgramType> {
  final programtype = ['Educational', 'Cultural', 'Seminar', 'Workshop'];
  String selectedprogramtype = "";

  _ProgramTypeState() {
    selectedprogramtype = programtype[0];
  }

  @override
  Widget build(BuildContext context) {
    return DropdownSearch<String>(
      items: programtype,
      dropdownDecoratorProps: const DropDownDecoratorProps(
        dropdownSearchDecoration: InputDecoration(
            focusedBorder: OutlineInputBorder(
                borderSide: BorderSide(color: Colors.orangeAccent)),
            enabledBorder:
                OutlineInputBorder(borderSide: BorderSide(color: Colors.black)),
            labelText: "Program Type",
            labelStyle: TextStyle(
              color: Color.fromARGB(255, 0, 94, 92),
            ),
            suffixIconColor: Colors.orangeAccent),
      ),
      validator: (programTypeValue) {
        if (programTypeValue == null || programTypeValue.isEmpty) {
          return "Program Type cannot be empty";
        }
        return null;
      },
      popupProps: const PopupProps.dialog(
        dialogProps: DialogProps(
          elevation: 15,
          backgroundColor: Color.fromARGB(255, 255, 211, 154),
        ),
      ),
      onChanged: widget.onProgramTypeChanged,
    );
  }
}
