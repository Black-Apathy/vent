/**
 * @file ventRoutes.js
 * @description Express router handling Core Events, Lookup Data, and PDF Exports.
 * Implements JWT authentication and Role-Based Access Control (RBAC) for mutations.
 */

const express = require("express");
const {
  submitData,
  getEvents,
  getEventById,
  deleteEvent,
  updateEvent,
  downloadEventPdf,
  getDepartments,
  getCommittees,
  previewPdfHtml,
} = require("../controllers/ventController");

const authenticateToken = require("../middlewares/authenticateToken");
const authorizeRoles = require("../middlewares/authorizeRoles");

const router = express.Router();

// ==========================================
// PDF GENERATION & REPORTING ROUTES
// ==========================================

// Dev utility: Renders raw HTML template for debugging
router.get("/test-pdf", previewPdfHtml);

// Protected: Generates and serves the official event report PDF
router.get("/events/:id/pdf", authenticateToken, downloadEventPdf);

// ==========================================
// LOOKUP DATA ROUTES (Read-Only)
// ==========================================

// Protected: Fetches metadata for dropdowns and mapping
router.get(
  "/departments",
  authenticateToken,
  authorizeRoles("admin", "teacher", "student"),
  getDepartments,
);
router.get(
  "/committees",
  authenticateToken,
  authorizeRoles("admin", "teacher", "student"),
  getCommittees,
);

// ==========================================
// CORE EVENT ROUTES (CRUD)
// ==========================================

// Publicly accessible event retrieval
router.get("/events", getEvents);
router.get("/events/:id", getEventById);

// Restricted (Admin/Teacher): Event creation
router.post(
  "/events",
  authenticateToken,
  authorizeRoles("admin", "teacher"),
  submitData,
);

// Restricted (Admin/Teacher): Partial event updates
router.patch(
  "/events/:id",
  authenticateToken,
  authorizeRoles("admin", "teacher"),
  updateEvent,
);

// Restricted (Admin/Teacher): Event deletion
router.delete(
  "/events/:id",
  authenticateToken,
  authorizeRoles("admin", "teacher"),
  deleteEvent,
);

module.exports = router;
