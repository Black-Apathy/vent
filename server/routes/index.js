// routes/index.js
const express = require("express");
const router = express.Router();

// Import individual route files
const homeRoutes = require("./homeRoutes");
const healthRoutes = require("./healthRoutes");
const pendingRoutes = require("./pendingRoutes");
const ventRoutes = require("./ventRoutes");
const authRoutes = require("./authRoutes");

// Use the routes
router.use("/", homeRoutes);
router.use("/", healthRoutes);
router.use("/", ventRoutes);
router.use("/", authRoutes);
router.use("/", pendingRoutes);

module.exports = router;
