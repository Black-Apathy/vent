const express = require("express");
const { registerUser, checkUserStatus, handleLogin } = require("../controllers/authController");

const router = express.Router();

// POST route for user registration
router.post("/register", registerUser);

// GET route for user registration (for testing purposes)
router.get("/check-user-status", checkUserStatus);

// POST route for user login
router.post("/login", handleLogin)

module.exports = router;
