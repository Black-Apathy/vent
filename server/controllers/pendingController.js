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
  const { request_id } = req.body;

  if (!request_id) {
    return res.status(400).json({ message: "Request ID is required" });
  }

  const getUserQry = "SELECT * FROM pending_users WHERE request_id = ?";
  connection.query(getUserQry, [request_id], (err, results) => {
    if (err || results.length === 0) {
      return res.status(404).json({ message: "No pending user found" });
    }

    const { email, password_hash, role_requested } = results[0];

    const insertUserQry =
      "INSERT INTO vent_users (email, password_hash, role) VALUES (?, ?, ?)";

    connection.query(
      insertUserQry,
      [email, password_hash, role_requested],
      (err) => {
        if (err) {
          return res
            .status(500)
            .json({ message: "Error inserting user into vent_users" });
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

  const deleteQry = "DELETE FROM pending_users WHERE request_id = ?";
  connection.query(deleteQry, [request_id], (err, results) => {
    if (err || results.affectedRows === 0) {
      return res.status(404).json({ message: "No pending user found" });
    }
    res.status(200).json({ message: "User request rejected successfully" });
  });
};
