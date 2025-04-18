const connection = require("../db");
const bcrypt = require("bcrypt");
const crypto = require("crypto");
const jwt = require("jsonwebtoken");
const { generateTokens } = require("../utils/tokenUtils");

exports.registerUser = async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res
      .status(400)
      .json({ message: "All fields are required for registration" });
  }

  try {
    // Hash the password
    const saltRounds = 10;
    const hashedPassword = await bcrypt.hash(password, saltRounds);
    const mysql_qry =
      "INSERT INTO pending_users (email, password_hash) VALUES (?, ?)";

    connection.query(
      mysql_qry,
      [email, hashedPassword],
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
  }
  catch (error) {
    console.error("Error hashing password:", error);
    res.status(500).json({ message: "Internal server error" });
  }
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

exports.handleLogin = async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    console.log("Error: Email or password missing");
    return res.status(400).json({ message: "Email and password are required" });
  }

  console.log("Received email:", email);
  console.log("Received password:", password);

  // First, check user status in the users table
  const statusQuery = "SELECT user_id, email, role, password_hash, approved_date FROM users WHERE email = ?";
  try {
    console.log("Executing status query for email:", email);
    const results = await new Promise((resolve, reject) => {
      connection.query(statusQuery, [email], (err, results) => {
        if (err) {
          console.error("Error executing status query:", err);
          reject(err);
        } else {
          console.log("Status query results:", results);
          resolve(results);
        }
      });
    });

    if (results.length === 0) {
      console.log("No user found for email:", email);
      return res.status(404).json({ message: "User not found" });
    }

    const user = results[0];
    console.log("User found:", user);

    // Check if the user is approved
    if (!user.approved_date) {
      console.log("User is not approved yet:", user.email);
      return res.status(403).json({ message: "User not approved by admin" });
    }

    // Compare the provided password with the stored hashed password
    console.log("Comparing passwords...");
    const isMatch = await bcrypt.compare(password, user.password_hash);
    if (!isMatch) {
      console.log("Invalid password for user:", user.email);
      return res.status(401).json({ message: "Invalid password" });
    }

    console.log("Password match successful.");

    const { accessToken, refreshToken } = generateTokens(user);

    // Store refresh token in DB
    const storeTokenQry = "UPDATE users SET refresh_token = ? WHERE email = ?";
    try {
      console.log("Executing refresh token update for email:", user.email);
      const storeResult = await new Promise((resolve, reject) => {
        connection.query(storeTokenQry, [refreshToken, user.email], (err, result) => {
          if (err) {
            console.error("Error executing refresh token update:", err);
            reject(err);
          } else {
            console.log("Refresh token update result:", result);
            resolve(result);
          }
        });
      });

      if (storeResult.affectedRows === 0) {
        console.error("Refresh token update failed. No rows affected.");
        return res.status(500).json({ message: "Error storing refresh token" });
      }

      console.log("Refresh token successfully stored.");

      // Send response after refresh token is successfully stored
      return res.status(200).json({
        message: "Login successful",
        accessToken,
        refreshToken,
        role: user.role // Include the role here for frontend
      });

    } catch (err) {
      console.error("Error while storing refresh token:", err);
      return res.status(500).json({ message: "Error storing refresh token" });
    }

  } catch (error) {
    console.error("Error during login process:", error);
    return res.status(500).json({ message: "An error occurred during login" });
  }
};

exports.refreshToken = (req, res) => {
  const { refreshToken } = req.body;

  if (!refreshToken) {
    return res.status(400).json({ message: "Refresh token missing" });
  }

  // Verify the refresh token signature & expiry
  jwt.verify(refreshToken, process.env.REFRESH_SECRET, (err, decoded) => {
    if (err) {
      return res.status(403).json({ message: "Invalid or expired refresh token" });
    }

    const email = decoded.email;

    const query = "SELECT user_id, email, role FROM users WHERE refresh_token = ?";
    connection.query(query, [refreshToken], (err, results) => {
      if (err || results.length === 0) {
        return res.status(403).json({ message: "Refresh token not found or already rotated" });
      }

      const user = results[0]; // has user_id, email, role
      const { accessToken, refreshToken: newRefreshToken } = generateTokens(user);

      const updateQuery = "UPDATE users SET refresh_token = ? WHERE user_id = ?";
      connection.query(updateQuery, [newRefreshToken, user.user_id], (err) => {
        if (err) {
          return res.status(500).json({ message: "Error updating refresh token in DB" });
        }

        res.json({
          accessToken,
          refreshToken: newRefreshToken,
        });
      });
    });
  });
};