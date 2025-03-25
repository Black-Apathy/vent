// controllers/healthController.js
const showHealthStatus = (req, res) => {
    res.status(200).json({
      status: "OK",
      uptime: process.uptime(),
      timestamp: new Date().toISOString(),
    });
  };
  
  module.exports = {
    showHealthStatus,
  };
  