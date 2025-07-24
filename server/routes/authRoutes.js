const express = require("express");
const { registerUser, checkUserStatus, handleLogin, refreshToken, resetPassword } = require("../controllers/authController");
const router = express.Router();

// POST route for user registration
router.post("/register", registerUser);

// GET route for user registration (for testing purposes)
router.get("/check-user-status", checkUserStatus);

// POST route for user login
router.post("/login", handleLogin)

// POST route for forgot password logic
router.post("/reset-password", resetPassword)

// POST route for refreshing token
router.post("/refresh-token", refreshToken);

module.exports = router;
