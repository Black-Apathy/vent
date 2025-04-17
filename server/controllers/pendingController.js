const connection = require("../db");

exports.getPendingUsers = (req, res) => {
  const mysql_qry = "SELECT * FROM pending_users";
  connection.query(mysql_qry, (err, results) => {
    if (err) {
      console.error("Error fetching pending users:", err);
      return res.status(500).json({ message: "Error fetching pending users" });
    }
    res.status(200).json(results);
  });
};

exports.approveUser = (req, res) => {
  const { request_id, role } = req.body;

  if (!request_id || !role) {
    return res.status(400).json({ message: "Request ID and role are required" });
  }

  const getUserQry = "SELECT * FROM pending_users WHERE request_id = ?";
  connection.query(getUserQry, [request_id], (err, results) => {
    if (err) {
      console.error("Error fetching pending user:", err);
      return res.status(500).json({ message: "Database error while fetching user" });
    }
    if (err || results.length === 0) {
      return res.status(404).json({ message: "No pending user found" });
    }

    const { email, password_hash } = results[0];

    const insertUserQry =
      "INSERT INTO users (email, password_hash, role) VALUES (?, ?, ?)";

    connection.query(
      insertUserQry,
      [email, password_hash, role],
      (err) => {
        if (err) {
          console.error("Error inserting user into users:", err);
          return res
            .status(500)
            .json({ message: "Error inserting user into users" });
        }
        const deleteQry = "DELETE FROM pending_users WHERE request_id = ?";
        connection.query(deleteQry, [request_id], (err) => {
          if (err) {
            return res
              .status(500)
              .json({ message: "Error deleting pending user" });
          }
          res
            .status(200)
            .json({ message: "User approved and moved successfully" });
        });
      }
    );
  });
};

exports.rejectUser = (req, res) => {
  const { request_id } = req.body;

  if (!request_id) {
    return res.status(400).json({ message: "Request ID is required" });
  }

  const getUserQry = "SELECT * FROM pending_users WHERE request_id = ?";
  connection.query(getUserQry, [request_id], (err, results) => {
    if (err) {
      console.error("Error fetching pending user:", err);
      return res.status(500).json({ message: "Database error while fetching user" });
    }

    if (results.length === 0) {
      return res.status(404).json({ message: "No pending user found" });
    }

    const { email, password_hash, role } = results[0];

    const insertUserQry =
      "INSERT INTO rejected_users (email, password_hash) VALUES (?, ?)";
    connection.query(insertUserQry, [email, password_hash, role], (err) => {
      if (err) {
        console.error("Error inserting user into rejected_users:", err);
        return res.status(500).json({ message: "Error inserting user into rejected_users" });
      }

      const deleteQry = "DELETE FROM pending_users WHERE request_id = ?";
      connection.query(deleteQry, [request_id], (err) => {
        if (err) {
          return res.status(500).json({ message: "Error deleting pending user" });
        }

        res.status(200).json({ message: "User rejected and moved successfully" });
      });
    });
  });
};
