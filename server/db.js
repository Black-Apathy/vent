// db.js
require("dotenv").config();
const mysql = require("mysql2/promise");

// Create a connection pool
const pool = mysql.createPool({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: process.env.DB_NAME,
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
});

// Test connection
pool.getConnection((err, connection) => {
  if (err) {
    console.error("❌ Error connecting to the database:", err.message);
    return;
  }
  console.log("✅ Connected to the database");
  connection.release();
});

module.exports = pool;
