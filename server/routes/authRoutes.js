const express = require("express");
const {
  registerUser,
  requestRegistrationOTP,
  verifyRegistrationOTP,
  checkUserStatus,
  handleLogin,
  refreshToken,
  resetPassword,
  requestPasswordResetOTP,
} = require("../controllers/authController");
const router = express.Router();

// POST route for user registration
router.post("/register", registerUser);

// POST route for otp registeration
router.post("/register/request-otp", requestRegistrationOTP);

// POST request to verify OTP
router.post("/register/verify-otp", verifyRegistrationOTP);

// GET route for user registration (for testing purposes)
router.get("/check-user-status", checkUserStatus);

// POST route for user login
router.post("/login", handleLogin);

// POST route to request a password reset OTP
router.post("/forgot-password/request-otp", requestPasswordResetOTP);

// POST route for forgot password logic
router.post("/reset-password", resetPassword);

// POST route for refreshing token
router.post("/refresh-token", refreshToken);

module.exports = router;
