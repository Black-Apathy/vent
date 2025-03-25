const express = require("express");
const {
  submitData,
  getData,
} = require("../controllers/ventController");

const router = express.Router();

// POST route to submit data
router.post("/submit", submitData);

// GET route to retrieve data
router.get("/data", getData);

module.exports = router;
