const express = require("express");
const {
  submitData,
  getData,
  getEventById,
  deleteEvent,
  updateEvent,
  downloadEventPdf,
} = require("../controllers/ventController");
const authenticateToken = require("../middlewares/authenticateToken");
const authorizeRoles = require("../middlewares/authorizeRoles");

const router = express.Router();

// Download PDF
router.get("/events/:id/pdf", authenticateToken, downloadEventPdf);

// POST route to create a new event
router.post(
  "/events",
  authenticateToken,
  authorizeRoles("admin", "teacher"),
  submitData
);

// GET route to fetch all events
router.get("/events", getData);

// Route to get a single event by ID
router.get("/events/:id", getEventById);

// DELETE route to delete an event by ID
router.delete(
  "/events/:event_id",
  authenticateToken,
  authorizeRoles("admin", "teacher"),
  deleteEvent
);

// PATCH route to update event partially by ID
router.patch(
  "/events/:event_id",
  authenticateToken,
  authorizeRoles("admin", "teacher"),
  updateEvent
);

module.exports = router;
