// // ignore_for_file: use_build_context_synchronously

// import 'package:flutter/material.dart';
// // import 'package:flutter_native_splash/flutter_native_splash.dart';

// class LoadingScreen extends StatefulWidget {
//   const LoadingScreen({Key? key}) : super(key: key);

//   @override
//   State<LoadingScreen> createState() => _LoadingScreenState();
// }

// class _LoadingScreenState extends State<LoadingScreen> {
//   @override
//   void initState() {
//     super.initState();
//     navigateToEventPage(context);
//   }

//   void navigateToEventPage(BuildContext context) async {
//     await Future.delayed(const Duration(seconds: 1));
//     Navigator.pushReplacementNamed(context, '/event');
//   }

//   @override
//   Widget build(BuildContext context) {
//     return Scaffold(
//       body: Center(child: Image.asset('assets/pictures/Static.png')),
//     );
//   }
// }
