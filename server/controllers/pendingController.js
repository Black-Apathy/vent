const db = require("../utils/dbUtils");

/**
 * Fetches all users from the pending_users table.
 */
exports.getPendingUsers = async (req, res) => {
  const mysql_qry = "SELECT * FROM pending_users";
  try {
    const results = await db.query(mysql_qry);
    res.status(200).json(results);
  } catch (err) {
    console.error("Error fetching pending users:", err);
    res.status(500).json({ message: "Error fetching pending users" });
  }
};

/**
 * Fetches all users from the users table.
 */
exports.getAllUsers = async (req, res) => {
  const mysql_qry = "SELECT user_id, email, role, approved_date FROM users ORDER BY role ASC";
  try {
    const results = await db.query(mysql_qry);
    res.status(200).json(results);
  } catch (err) {
    console.error("Error fetching all users:", err);
    res.status(500).json({ message: "Error fetching all users" });
  }
};

/**
 * Approves a pending user: moves them to users table and deletes from pending_users.
 * Expects: request_id and role in req.body
 */
exports.approveUser = async (req, res) => {
  const { request_id, role } = req.body;

  if (!request_id || !role) {
    return res.status(400).json({ message: "Request ID and role are required" });
  }

  try {
    // Get the pending user
    const getUserQry = "SELECT * FROM pending_users WHERE request_id = ?";
    const results = await db.query(getUserQry, [request_id]);
    if (!results || results.length === 0) {
      return res.status(404).json({ message: "No pending user found" });
    }
    const { email, password_hash } = results[0];

    // Insert into users table
    const insertUserQry = "INSERT INTO users (email, password_hash, role) VALUES (?, ?, ?)";
    await db.query(insertUserQry, [email, password_hash, role]);

    // Delete from pending_users
    const deleteQry = "DELETE FROM pending_users WHERE request_id = ?";
    await db.query(deleteQry, [request_id]);

    res.status(200).json({ message: "User approved and moved successfully" });
  } catch (err) {
    console.error("Error approving user:", err);
    res.status(500).json({ message: "Error approving user" });
  }
};

/**
 * Rejects a pending user: moves them to rejected_users table and deletes from pending_users.
 * Expects: request_id in req.body
 */
exports.rejectUser = async (req, res) => {
  const { request_id } = req.body;

  if (!request_id) {
    return res.status(400).json({ message: "Request ID is required" });
  }

  try {
    // Get the pending user
    const getUserQry = "SELECT * FROM pending_users WHERE request_id = ?";
    const results = await db.query(getUserQry, [request_id]);
    if (!results || results.length === 0) {
      return res.status(404).json({ message: "No pending user found" });
    }
    const { email, password_hash } = results[0];

    // Insert into rejected_users table
    const insertUserQry = "INSERT INTO rejected_users (email, password_hash) VALUES (?, ?)";
    await db.query(insertUserQry, [email, password_hash]);

    // Delete from pending_users
    const deleteQry = "DELETE FROM pending_users WHERE request_id = ?";
    await db.query(deleteQry, [request_id]);

    res.status(200).json({ message: "User rejected and moved successfully" });
  } catch (err) {
    console.error("Error rejecting user:", err);
    res.status(500).json({ message: "Error rejecting user" });
  }
};
