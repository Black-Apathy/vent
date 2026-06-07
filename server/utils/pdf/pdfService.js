// utils/pdfService.js
const puppeteer = require("puppeteer");
const fs = require("fs");
const path = require("path");
const { buildEventHtml } = require("./pdfTemplate");

const loadLogo = () => {
  try {
    const logoPath = path.join(__dirname, "../../assets/college_logo.png");
    const bitmap = fs.readFileSync(logoPath);
    return `data:image/jpeg;base64,${bitmap.toString("base64")}`;
  } catch (err) {
    return "";
  }
};

const generatePdfBuffer = async (data) => {
  const logoBase64 = loadLogo();

  // 1. Get the HTML string from the template file
  const htmlContent = buildEventHtml(data, logoBase64);

  // 2. Launch the Engine
  const browser = await puppeteer.launch({
    headless: "new",
    args: [
      "--no-sandbox",
      "--disable-setuid-sandbox",
      "--disable-dev-shm-usage",
    ],
  });

  const page = await browser.newPage();
  await page.setContent(htmlContent, { waitUntil: "networkidle0" });

  const pdfBuffer = await page.pdf({
    format: "A4",
    printBackground: true,
    margin: { top: "0px", right: "0px", bottom: "0px", left: "0px" },
  });

  await browser.close();
  return pdfBuffer;
};

module.exports = { generatePdfBuffer };
