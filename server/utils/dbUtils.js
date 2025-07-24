const util = require("util");
const connection = require("../db");

// Promisify the query method
const query = util.promisify(connection.query).bind(connection);

module.exports = {
    query
};
