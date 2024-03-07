import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:loading_animation_widget/loading_animation_widget.dart';

class SubmitButton extends StatefulWidget {
  final bool isLoading;
  final VoidCallback onPressed;
  const SubmitButton({
    Key? key,
    required this.isLoading,
    required this.onPressed,
  }) : super(key: key);

  @override
  State<SubmitButton> createState() => _SubmitButtonState();
}

class _SubmitButtonState extends State<SubmitButton> {
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _isLoading = widget.isLoading;
  }

  @override
  Widget build(BuildContext context) {
    return ElevatedButton(
        onPressed: _isLoading ? null : widget.onPressed,
        style: ButtonStyle(
            elevation: const MaterialStatePropertyAll(10),
            backgroundColor:
                const MaterialStatePropertyAll(Colors.orangeAccent),
            shape: MaterialStatePropertyAll(RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(50))),
            minimumSize:
                const MaterialStatePropertyAll(Size(double.infinity, 50))),
        child: widget.isLoading
            ? LoadingAnimationWidget.waveDots(color: Colors.white, size: 40)
            : Text("Submit",
                style: GoogleFonts.poppins(
                    fontSize: 15,
                    fontWeight: FontWeight.w700,
                    color: Colors.white)));
  }
}
