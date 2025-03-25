// routes/homeRoutes.js
const express = require("express");
const router = express.Router();
const homeController = require("../controllers/homeController");

// Home route for /
router.get("/", homeController.showHomePage);

module.exports = router;
