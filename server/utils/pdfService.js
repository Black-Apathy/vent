const puppeteer = require("puppeteer");
const fs = require("fs");
const path = require("path");

const loadLogo = () => {
  try {
    const logoPath = path.join(__dirname, "../assets/college_logo.png");
    const bitmap = fs.readFileSync(logoPath);
    return `data:image/jpeg;base64,${bitmap.toString("base64")}`;
  } catch (err) {
    return "";
  }
};

const generatePdfBuffer = async (data) => {
  const logoBase64 = loadLogo();

  // --- CONFIGURATION ---
  const societyName = "Vivek Education Society's";
  const collegeName = "VIVEK COLLEGE OF COMMERCE";
  const collegeStatus = "(Autonomous)";
  const addressLine1 = "Vivek College Road, Goregaon West, Mumbai-104";

  // --- HTML TEMPLATE ---
  const htmlContent = `
    <!DOCTYPE html>
    <html>
    <head>
      <style>
        @import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap');
        
        :root {
          --color-blue: #003366;       
          --color-cream: #FFF5E1;      
          --color-orange: #FF6600;     
          --color-light-blue: #AFBBF2;  
          --color-grey: #666666;       
        }

        body { 
          font-family: 'Roboto', sans-serif; 
          margin: 0; 
          padding: 0;
          color: var(--color-blue);
          -webkit-print-color-adjust: exact; 
        }

        /* --- HEADER (Same as before) --- */
        .header-full-width {
          background-color: var(--color-blue);
          color: white;
          padding: 30px 40px;
          display: flex;
          align-items: center;
          border-bottom: 5px solid var(--color-orange);
        }

        .logo-container {
          flex: 0 0 90px;
          margin-right: 25px;
          background-color: white; 
          border-radius: 50%;
          width: 90px;
          height: 90px;
          display: flex;
          align-items: center;
          justify-content: center;
          border: 2px solid white;
        }

        .logo-img { width: 80px; height: auto; }

        .header-text { flex: 1; }

        .society-name {
          font-size: 12px;
          letter-spacing: 1px;
          color: var(--color-cream);
          text-transform: uppercase;
          opacity: 0.9;
        }

        .college-name {
          font-size: 28px; 
          font-weight: 700;
          color: white;
          margin: 4px 0;
          text-transform: uppercase;
        }

        .autonomous-tag {
          color: var(--color-orange);
          font-weight: bold;
          font-size: 14px;
        }

        .address-line {
          font-size: 11px;
          color: #ccc;
          margin-top: 4px;
        }

        /* --- CONTENT AREA --- */
        .content-container {
          padding: 50px 60px;
        }

        .report-heading {
          text-align: center;
          font-size: 14px;
          font-weight: bold;
          text-transform: uppercase;
          letter-spacing: 2px;
          color: var(--color-grey);
          margin-bottom: 30px;
          border-bottom: 1px solid #eee;
          padding-bottom: 10px;
        }

        /* --- THE RECORD GRID --- */
        .record-grid {
          display: grid;
          grid-template-columns: 1fr 1fr; /* Two columns */
          gap: 25px;
          margin-bottom: 40px;
        }

        .grid-full-width {
          grid-column: span 2; /* Spans across both columns */
        }

        .data-box {
          border: 1px solid var(--color-light-blue);
          padding: 20px;
          border-radius: 8px;
          background-color: #fff;
          /* Subtle shadow for depth */
          box-shadow: 0 2px 5px rgba(0,0,0,0.02);
        }

        .box-label {
          font-size: 11px;
          font-weight: 700;
          color: var(--color-grey);
          text-transform: uppercase;
          margin-bottom: 8px;
        }

        .box-value {
          font-size: 18px;
          color: var(--color-blue);
          font-weight: 500;
          line-height: 1.3;
        }
        
        .box-value-highlight {
           font-size: 20px;
           font-weight: 700;
        }

        .time-subtext {
           font-size: 14px;
           color: var(--color-orange);
           margin-top: 4px;
           font-weight: 500;
        }

        /* --- FOOTER --- */
        .footer-section {
          position: absolute;
          bottom: 50px;
          left: 60px;
          right: 60px;
          padding-top: 20px;
          border-top: 1px solid #eee;
          display: flex;
          justify-content: space-between;
          align-items: flex-end;
          color: var(--color-grey);
          font-size: 11px;
        }
        
        .signature-line {
          width: 200px;
          border-top: 2px solid var(--color-blue);
          margin-bottom: 8px;
        }

      </style>
    </head>
    <body>

      <div class="header-full-width">
        <div class="logo-container">
           ${logoBase64 ? `<img src="${logoBase64}" class="logo-img" />` : ''}
        </div>
        <div class="header-text">
          <div class="society-name">${societyName}</div>
          <div class="college-name">${collegeName}</div>
          <div class="autonomous-tag">${collegeStatus}</div>
          <div class="address-line">${addressLine1}</div>
        </div>
      </div>

      <div class="content-container">

        <div class="report-heading">Official Event Record</div>

        <div class="record-grid">
          
          <div class="data-box grid-full-width" style="background-color: var(--color-cream); border-color: var(--color-orange);">
            <div class="box-label" style="color: var(--color-orange);">Program Name</div>
            <div class="box-value box-value-highlight">${data.Program_Name}</div>
          </div>

          <div class="data-box">
            <div class="box-label">Program Type</div>
            <div class="box-value">${data.Program_Type}</div>
          </div>

          <div class="data-box">
            <div class="box-label">Number of Participants</div>
            <div class="box-value">${data.No_of_Participants}</div>
          </div>

          <div class="data-box">
            <div class="box-label">Start Date</div>
            <div class="box-value">${new Date(data.Start_Date).toLocaleDateString()}</div>
            <div class="time-subtext">${data.Start_Time}</div>
          </div>

          <div class="data-box">
            <div class="box-label">End Date</div>
            <div class="box-value">${new Date(data.End_Date).toLocaleDateString()}</div>
            <div class="time-subtext">${data.End_Time}</div>
          </div>

        </div>

        <div class="footer-section">
          <div>
             Generated on: ${new Date().toLocaleString()} <br>
             Event Reference ID: #${data.event_id}
          </div>
          <div style="text-align: center;">
            <div class="signature-line"></div>
            <div>HOD / Coordinator Signature</div>
          </div>
        </div>

      </div>

    </body>
    </html>
  `;

  // --- PUPPETEER LOGIC ---
  const browser = await puppeteer.launch({
    headless: "new",
    args: ["--no-sandbox", "--disable-setuid-sandbox", "--disable-dev-shm-usage"],
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