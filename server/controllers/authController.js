const connection = require("../db");

exports.registerUser = (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res
      .status(400)
      .json({ message: "All fields are required for registration" });
  }

  const mysql_qry =
    "INSERT INTO pending_users (email, password_hash) VALUES (?, ?)";

  connection.query(
    mysql_qry,
    [email, password],
    (err) => {
      if (err) {
        console.error("Error inserting data into pending_users:", err);
        return res
          .status(500)
          .json({ message: "Error inserting data into pending_users" });
      }
      res
        .status(201)
        .json({ message: "User registration request submitted successfully" });
    }
  );
};

exports.checkUserStatus = (req, res) => {
  const { email } = req.query;

  if (!email) {
    return res.status(400).json({ message: "Email is required" });
  }

  const mysql_qry = "SELECT role, approved_date FROM users WHERE email = ?";

  connection.query(mysql_qry, [email], (err, results) => {
    if (err) {
      console.error("Error fetching user status:", err);
      return res.status(500).json({ message: "Error fetching user status" });
    }

    if (results.length > 0) {
      const user = results[0];

      // Check if approved_date is not null (i.e., user is approved)
      if (user.approved_date) {
        return res.status(200).json({ 
          status: "approved", 
          role: user.role 
        });
      } else {
        return res.status(200).json({ status: "pending" });
      }
    } else {
      return res.status(404).json({ status: "not_found" });
    }
  });
};