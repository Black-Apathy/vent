// routes/index.js
const express = require("express");
const router = express.Router();

// Import individual route files
const homeRoutes = require("./homeRoutes");
const healthRoutes = require("./healthRoutes");
const authRoutes = require("./authRoutes");
const ventRoutes = require("./ventRoutes");
const pendingRoutes = require("./pendingRoutes");

// ==========================================
// SYSTEM ROUTES (Public)
// ==========================================
// Resolves to: /
router.use("/", homeRoutes);

// Resolves to: /health
router.use("/", healthRoutes);

// ==========================================
// API ROUTES (Version 1)
// ==========================================
// Resolves to: /api/v1/auth/login, /api/v1/auth/register, etc.
router.use("/api/v1/auth", authRoutes);

// Resolves to: /api/v1/events, /api/v1/departments, etc.
router.use("/api/v1", ventRoutes);

// Assuming pending routes are admin tools.
// Resolves to: /api/v1/admin/pending
router.use("/api/v1/admin", pendingRoutes);

module.exports = router;
