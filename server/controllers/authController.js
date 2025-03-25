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
