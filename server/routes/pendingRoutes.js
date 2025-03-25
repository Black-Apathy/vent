const express = require("express");
const {
  getPendingUsers,
  approveUser,
  rejectUser,
} = require("../controllers/pendingController");

const router = express.Router();

// GET all pending users
router.get("/pending-users", getPendingUsers);

// Approve user
router.post("/approve-user", approveUser);

// Reject user
router.post("/reject-user", rejectUser);

module.exports = router;
