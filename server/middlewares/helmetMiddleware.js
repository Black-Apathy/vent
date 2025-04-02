const helmet = require("helmet");

const helmetMiddleware = (app) => {
  app.use(
    helmet({
      contentSecurityPolicy: {
        directives: {
          defaultSrc: ["'self'"], // Only allow content from your server
          scriptSrc: ["'self'"], // No external scripts allowed
          styleSrc: ["'self'", "https:"], // Safe styles, no inline
          imgSrc: ["'self'", "data:"], // Only local and base64 images
          fontSrc: ["'self'", "https:", "data:"], // Local & safe fonts
          objectSrc: ["'none'"], // Prevent embedding Flash, etc.
          frameAncestors: ["'none'"], // Block clickjacking
          connectSrc: ["'self'"], // Prevent external API requests
          upgradeInsecureRequests: [], // Upgrade HTTP to HTTPS
        },
      },
      frameguard: { action: "deny" }, // Prevent embedding in iframes
      referrerPolicy: { policy: "no-referrer" }, // No referrer leakage
      hidePoweredBy: true, // Hide X-Powered-By header
    })
  );

  // Manually remove 'Server' header (NGINX details)
  app.use((req, res, next) => {
    res.removeHeader("Server");
    next();
  });
};

module.exports = helmetMiddleware;
