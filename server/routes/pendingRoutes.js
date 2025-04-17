const express = require("express");
const {
  getPendingUsers,
  approveUser,
  rejectUser,
} = require("../controllers/pendingController");
const authenticateToken = require("../middlewares/authenticateToken");
const authorizeRoles = require("../middlewares/authorizeRoles");

const router = express.Router();

// GET all pending users
router.get("/pending-user", authenticateToken, authorizeRoles("admin"), getPendingUsers);

// Approve user
router.post("/approve-user", authenticateToken, authorizeRoles("admin"), approveUser);

// Reject user
router.post("/reject-user", authenticateToken, authorizeRoles("admin"), rejectUser);

module.exports = router;
