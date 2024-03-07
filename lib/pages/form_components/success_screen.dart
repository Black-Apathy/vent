import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:animated_text_kit/animated_text_kit.dart';

class SuccessScreen extends StatelessWidget {
  const SuccessScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color.fromARGB(255, 243, 255, 251),
      appBar: AppBar(
        toolbarHeight: 70,
        elevation: 0,
        backgroundColor: const Color.fromARGB(255, 0, 94, 92),
        title: Text(
          "Success",
          style:
              GoogleFonts.openSans(fontSize: 21, fontWeight: FontWeight.w500),
        ),
        centerTitle: true,
      ),
      body: Center(
          child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          const Icon(
            Icons.check_circle,
            size: 170,
            color: Color.fromARGB(255, 0, 152, 116),
          ),
          AnimatedTextKit(
            animatedTexts: [
              TyperAnimatedText("Event registered successfully!",
                  textStyle: GoogleFonts.hindSiliguri(fontSize: 20),
                  speed: const Duration(milliseconds: 50))
            ],
            isRepeatingAnimation: false,
            repeatForever: false,
            totalRepeatCount: 1,
          )
        ],
      )),
    );
  }
}
