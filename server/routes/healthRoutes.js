// routes/healthRoutes.js
const express = require("express");
const router = express.Router();
const healthController = require("../controllers/healthController");

// Home route for /
router.get("/health", healthController.showHealthStatus);

module.exports = router;
