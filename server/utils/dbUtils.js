// dbUtils.js
const db = require("../db");

async function query(sql, params) {
  const [rows] = await db.query(sql, params);
  return rows;
}

module.exports = { query };
