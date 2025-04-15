const express = require("express");
const { registerUser, checkUserStatus  } = require("../controllers/authController");

const router = express.Router();

// POST route for user registration
router.post("/register", registerUser);

// GET route for user registration (for testing purposes)
router.get("/check-user-status", checkUserStatus);

module.exports = router;
