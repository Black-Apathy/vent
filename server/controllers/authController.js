const crypto = require("crypto");
const db = require("../utils/dbUtils");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const { generateTokens } = require("../utils/tokenUtils");
const { sendOTPEmail } = require("../utils/email/emailUtils");

/**
 * Registers a new user by adding them to the pending_users table.
 * Expects: email, password in req.body
 */
exports.registerUser = async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({ message: "All fields are required" });
  }

  try {
    // 1. Check if user is already approved in 'users'
    const [existingApproved] = await db.query(
      "SELECT email FROM users WHERE email = ?",
      [email],
    );
    if (existingApproved) {
      return res.status(400).json({
        message: "User is already registered and approved. Please login.",
      });
    }

    // 2. Check if user is already waiting in 'pending_users'
    const [existingPending] = await db.query(
      "SELECT email FROM pending_users WHERE email = ?",
      [email],
    );
    if (existingPending) {
      return res.status(400).json({
        message:
          "Registration already submitted. Please wait for admin approval.",
      });
    }

    // 3. If neither, proceed with registration
    const saltRounds = 10;
    const hashedPassword = await bcrypt.hash(password, saltRounds);
    const mysql_qry =
      "INSERT INTO pending_users (email, password_hash) VALUES (?, ?)";

    await db.query(mysql_qry, [email, hashedPassword]);
    return res
      .status(201)
      .json({ message: "Registration successful. Awaiting admin approval." });
  } catch (error) {
    console.error("Error during registration:", error);
    return res.status(500).json({ message: "Internal server error" });
  }
};

/**
 * Registers an OTP for a new user by adding them to the email_otps table.
 * Expects: email in req.body
 */
exports.requestRegistrationOTP = async (req, res) => {
  try {
    const { email } = req.body;

    if (!email) {
      return res.status(400).json({ error: "Email is required." });
    }

    // DELETE any old OTPs related to the specific email
    await db.query("DELETE FROM email_otps WHERE email = ? AND purpose = ?", [
      email,
      "registration",
    ]);

    // 1. Generate a cryptographically secure 6-digit OTP
    const otpCode = crypto.randomInt(100000, 999999).toString();

    // 2. Save to the database
    const query = `
        INSERT INTO email_otps (email, otp_code, purpose, expires_at)
        VALUES (?, ?, 'registration', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 05 MINUTE))
    `;

    await db.query(query, [email, otpCode]);

    // 3. Send the email (fire and forget)
    sendOTPEmail(email, otpCode).catch((err) =>
      console.error("Background email failed:", err),
    );

    return res.status(200).json({
      message: "OTP generated successfully. Please check your email.",
    });
  } catch (error) {
    console.error("Error generating OTP:", error);
    return res.status(500).json({ error: "Internal server error." });
  }
};

/**
 * Verifies an OTP for a user.
 * * Flow:
 * 1. Sanitizes/Validates request body.
 * 2. Queries DB for matching active registration OTP.
 * 3. Normalizes response to handle driver-specific return types.
 * 4. Checks for expiration.
 * 5. Deletes OTP (one-time use) and responds.
 */
exports.verifyRegistrationOTP = async (req, res) => {
  try {
    const { email, otpCode } = req.body;

    // 1. Basic validation
    if (!email || !otpCode) {
      return res.status(400).json({ error: "Email and OTP are required." });
    }

    // 2. Query the database and return if OTP is expired
    const query = `
            SELECT
                id,
                (expires_at < CURRENT_TIMESTAMP) AS is_expired
            FROM email_otps
            WHERE email = ? AND otp_code = ? AND purpose = 'registration'
        `;

    // Await the query result
    const queryResult = await db.query(query, [email, otpCode]);

    // 3. Normalize: Driver versions return different formats (Array of results vs Object)
    let rows = Array.isArray(queryResult) ? queryResult[0] : queryResult;
    if (!Array.isArray(rows)) {
      rows = [rows]; // Wrap single object into array
    }

    // 4. Validate existence
    if (rows.length === 0) {
      return res.status(400).json({ error: "Invalid OTP." });
    }

    const otpRecord = rows[0];

    // 5. Runtime Expiration Check
    // MariaDB returns 1 for true, 0 for false
    if (otpRecord.is_expired === 1) {
      return res
        .status(400)
        .json({ error: "This OTP has expired. Please request a new one." });
    }

    // 6. Success: Destroy the OTP (Security: prevent replay attacks)
    await db.query("DELETE FROM email_otps WHERE id = ?", [otpRecord.id]);

    return res.status(200).json({
      message: "Email verified successfully! Proceeding to account creation.",
    });
  } catch (error) {
    console.error("Error verifying OTP:", error);
    return res.status(500).json({ error: "Internal server error." });
  }
};

/**
 * Checks the approval status of a user.
 * Expects: email in req.query
 */
exports.checkUserStatus = async (req, res) => {
  const { email } = req.query;

  if (!email) {
    return res
      .status(400)
      .json({ success: false, message: "Email is required" });
  }

  try {
    // 1. Check the main 'users' table
    const approvedQry = "SELECT role, approved_date FROM users WHERE email = ?";
    const approvedResults = await db.query(approvedQry, [email]);

    if (approvedResults.length > 0) {
      // User is approved (or at least exists in the main table)
      return res.status(200).json({
        success: true,
        status: "approved",
        role: approvedResults[0].role,
      });
    }

    // 2. If not found in 'users', check the 'pending_users' table
    const pendingQry = "SELECT email FROM pending_users WHERE email = ?";
    const pendingResults = await db.query(pendingQry, [email]);

    if (pendingResults.length > 0) {
      // User exists in pending table
      return res.status(200).json({
        success: true,
        status: "pending",
      });
    }

    // 3. Truly not found anywhere
    return res.status(404).json({
      success: false,
      status: "not_found",
      message: "User not found",
    });
  } catch (error) {
    console.error("Error fetching user status:", error);
    return res
      .status(500)
      .json({ success: false, message: "Internal server error" });
  }
};

/**
 * Handles user login, checks credentials, and issues tokens.
 * Expects: email, password in req.body
 */
exports.handleLogin = async (req, res) => {
  const { email, password } = req.body;

  if (!email || !password) {
    return res.status(400).json({
      success: false,
      message: "Email and password are required",
    });
  }

  try {
    const statusQuery = `
      SELECT user_id, email, role, password_hash, approved_date
      FROM users WHERE email = ?
    `;
    const results = await db.query(statusQuery, [email]);

    if (results.length === 0) {
      return res.status(404).json({
        success: false,
        message: "User not found",
      });
    }

    const user = results[0];

    if (!user.approved_date) {
      return res.status(403).json({
        success: false,
        message: "User not approved by admin",
      });
    }

    const isMatch = await bcrypt.compare(password, user.password_hash);

    if (!isMatch) {
      return res.status(401).json({
        success: false,
        message: "Invalid credentials",
      });
    }

    const { accessToken, refreshToken } = generateTokens(user);

    const storeTokenQry = "UPDATE users SET refresh_token = ? WHERE email = ?";

    const storeResult = await db.query(storeTokenQry, [
      refreshToken,
      user.email,
    ]);

    if (storeResult.affectedRows === 0) {
      return res.status(500).json({
        success: false,
        message: "Error storing refresh token",
      });
    }

    return res.status(200).json({
      success: true,
      message: "Login successful",
      accessToken,
      refreshToken,
      role: user.role,
    });
  } catch (error) {
    console.error("Error during login process:", error);
    return res.status(500).json({
      success: false,
      message: "An error occurred during login",
    });
  }
};

/**
 * Generates an OTP for password reset.
 * Expects: email in req.body
 */
exports.requestPasswordResetOTP = async (req, res) => {
  try {
    const { email } = req.body;

    if (!email) {
      return res.status(400).json({ error: "Email is required." });
    }

    // 1. Check if the user actually exists in the main 'users' table
    const userQuery = "SELECT user_id FROM users WHERE email = ?";
    const userResult = await db.query(userQuery, [email]);

    let rows = Array.isArray(userResult) ? userResult[0] : userResult;
    if (!Array.isArray(rows)) rows = [rows];

    // SECURITY: Prevent "User Enumeration".
    if (rows.length === 0) {
      return res.status(200).json({
        message:
          "If an account with that email exists, a reset code has been sent.",
      });
    }

    // 2. Clean up any old reset OTPs for this user
    await db.query("DELETE FROM email_otps WHERE email = ? AND purpose = ?", [
      email,
      "forgot_password",
    ]);

    // 3. Generate a cryptographically secure 6-digit OTP
    const otpCode = crypto.randomInt(100000, 999999).toString();

    // 4. Save to the database
    const insertQuery = `
        INSERT INTO email_otps (email, otp_code, purpose, expires_at)
        VALUES (?, ?, 'forgot_password', DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 5 MINUTE))
    `;
    await db.query(insertQuery, [email, otpCode]);

    // 5. Send the email (fire and forget)
    sendOTPEmail(email, otpCode).catch((err) =>
      console.error("Background email failed:", err),
    );

    return res.status(200).json({
      message:
        "If an account with that email exists, a reset code has been sent.",
    });
  } catch (error) {
    console.error("Error generating reset OTP:", error);
    return res.status(500).json({ error: "Internal server error." });
  }
};

/**
 * Verifies the OTP and resets a user's password.
 * Expects: email, otpCode, newPassword in req.body
 */
exports.resetPassword = async (req, res) => {
  const { email, otpCode, newPassword } = req.body;

  if (!email || !otpCode || !newPassword) {
    return res.status(400).json({
      success: false,
      message: "Email, OTP, and new password are required",
    });
  }

  try {
    // 1. Query the database to verify the OTP and check expiration
    const otpQuery = `
        SELECT id, (expires_at < CURRENT_TIMESTAMP) AS is_expired
        FROM email_otps
        WHERE email = ? AND otp_code = ? AND purpose = 'forgot_password'
    `;
    const otpResult = await db.query(otpQuery, [email, otpCode]);

    // Normalize driver response
    let rows = Array.isArray(otpResult) ? otpResult[0] : otpResult;
    if (!Array.isArray(rows)) rows = [rows];

    // 2. Validate OTP existence
    if (rows.length === 0) {
      return res.status(400).json({ success: false, message: "Invalid OTP." });
    }

    const otpRecord = rows[0];

    // 3. Validate expiration
    if (otpRecord.is_expired === 1) {
      return res.status(400).json({
        success: false,
        message: "This OTP has expired. Please request a new one.",
      });
    }

    // 4. Success: Destroy the OTP immediately so it cannot be reused
    await db.query("DELETE FROM email_otps WHERE id = ?", [otpRecord.id]);

    // 5. Hash the new password
    const SALT_ROUNDS = 10;
    const hashedPassword = await bcrypt.hash(newPassword, SALT_ROUNDS);

    // 6. Update the user's password in the database
    const updateQuery = "UPDATE users SET password_hash = ? WHERE email = ?";
    const updateResult = await db.query(updateQuery, [hashedPassword, email]);

    // Safety check to ensure the row was actually affected
    const affectedRows = Array.isArray(updateResult)
      ? updateResult[0].affectedRows
      : updateResult.affectedRows;
    if (affectedRows === 0) {
      return res.status(500).json({
        success: false,
        message: "Error updating password. User may no longer exist.",
      });
    }

    return res.status(200).json({
      success: true,
      message: "Password reset successful.",
    });
  } catch (error) {
    console.error("Error during forgot password process:", error);
    return res.status(500).json({
      success: false,
      message: "Internal server error",
    });
  }
};

/**
 * Issues a new access token using a valid refresh token.
 * Expects: refreshToken in req.body
 */
exports.refreshToken = async (req, res) => {
  const { refreshToken } = req.body;

  if (!refreshToken) {
    return res.status(400).json({
      success: false,
      message: "Refresh token missing",
    });
  }

  try {
    // Verify refresh token signature & expiry
    const decoded = jwt.verify(refreshToken, process.env.REFRESH_SECRET);
    const email = decoded.email;

    // Check if refresh token exists in DB
    const query =
      "SELECT user_id, email, role FROM users WHERE refresh_token = ?";
    const results = await db.query(query, [refreshToken]);

    if (results.length === 0) {
      return res.status(403).json({
        success: false,
        message: "Refresh token not found or already rotated",
      });
    }

    const user = results[0];
    const { accessToken, refreshToken: newRefreshToken } = generateTokens(user);

    // Update DB with new refresh token
    const updateQuery = "UPDATE users SET refresh_token = ? WHERE user_id = ?";
    await db.query(updateQuery, [newRefreshToken, user.user_id]);

    return res.json({
      success: true,
      accessToken,
      refreshToken: newRefreshToken,
    });
  } catch (err) {
    if (err.name === "JsonWebTokenError" || err.name === "TokenExpiredError") {
      return res.status(403).json({
        success: false,
        message: "Invalid or expired refresh token",
      });
    }

    console.error("Error in refreshToken:", err);
    return res.status(500).json({
      success: false,
      message: "Internal server error",
    });
  }
};
