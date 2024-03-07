import 'package:flutter/material.dart';
// import 'package:vent/pages/splash.dart';
import 'package:vent/pages/event.dart';
import 'package:vent/pages/about.dart';

void main() {
  runApp(MaterialApp(
    // home: const Splash(),
    home: const Event(),
    routes: {
      // '/splash': (context) => const Splash(),
      '/event': (context) => const Event(),
      'about': (context) => const About(),
    },
  ));
}
