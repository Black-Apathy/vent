const puppeteer = require("puppeteer");

const generatePdfBuffer = async (data) => {
  // 1. The HTML Template
  const htmlContent = `
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                body { font-family: sans-serif; padding: 40px; }
                h1 { color: #2c3e50; }
                .row { margin-bottom: 10px; }
                .label { font-weight: bold; }
            </style>
        </head>
        <body>
            <h1>${data.programName}</h1>
            <div class="row">
                <span class="label">Date:</span> ${data.displayStartDate}
            </div>
            <div class="row">
                <span class="label">Participants:</span> ${data.numberOfParticipants}
            </div>
            <p>Generated via Puppeteer on Ubuntu VPS</p>
        </body>
        </html>
    `;

  // 2. Puppeteer Logic
  const browser = await puppeteer.launch({
    headless: "new",
    args: [
      "--no-sandbox",
      "--disable-setuid-sandbox",
      "--disable-dev-shm-usage",
      // "--single-process",
    ],
  });

  const page = await browser.newPage();
  await page.setContent(htmlContent, { waitUntil: "networkidle0" });

  const pdfBuffer = await page.pdf({
    format: "A4",
    printBackground: true,
    margin: { top: "20px", right: "20px", bottom: "20px", left: "20px" },
  });

  await browser.close();
  return pdfBuffer;
};

module.exports = { generatePdfBuffer };
