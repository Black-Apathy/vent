const express = require("express");
const { registerUser } = require("../controllers/authController");

const router = express.Router();

// POST route for user registration
router.post("/register", registerUser);

module.exports = router;
