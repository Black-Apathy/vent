const buildEventHtml = (data, logoBase64) => {
  return `
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

            .content-container {
              padding: 30px 50px;
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

            /* --- PROGRAM NAME BANNER --- */
            .program-banner {
              background-color: var(--color-blue);
              color: white;
              padding: 20px 25px;
              border-radius: 8px;
              margin-bottom: 8px;
              display: flex;
              justify-content: space-between;
              align-items: center;
            }

            .program-banner-name {
              font-size: 22px;
              font-weight: 700;
            }

            .program-type-badge {
              background-color: var(--color-orange);
              color: white;
              font-size: 11px;
              font-weight: 700;
              text-transform: uppercase;
              letter-spacing: 1px;
              padding: 5px 14px;
              border-radius: 20px;
              white-space: nowrap;
              margin-left: 20px;
            }

            /* --- THE RECORD GRID --- */
            .record-grid {
              display: grid;
              grid-template-columns: 1fr 1fr;
              gap: 12px;
              margin-bottom: 20px;
            }

            .data-box {
              border: 1px solid var(--color-light-blue);
              padding: 18px 20px;
              border-radius: 8px;
              background-color: #fff;
              box-shadow: 0 2px 5px rgba(0,0,0,0.02);
            }

            .box-label {
              font-size: 10px;
              font-weight: 700;
              color: var(--color-grey);
              text-transform: uppercase;
              letter-spacing: 0.5px;
              margin-bottom: 8px;
            }

            .box-value {
              font-size: 17px;
              color: var(--color-blue);
              font-weight: 500;
              line-height: 1.3;
            }

            .time-subtext {
              font-size: 13px;
              color: var(--color-orange);
              margin-top: 4px;
              font-weight: 500;
            }

            .muted-subtext {
              font-size: 13px;
              color: var(--color-grey);
              margin-top: 4px;
            }

            /* --- DIVIDER --- */
            .section-divider {
              border: none;
              border-top: 1px solid #eee;
              margin: 0 0 20px 0;
            }

            /* --- FOOTER --- */
            .footer-section {
              margin-top: 50px;
              padding-top: 20px;
              display: flex;
              justify-content: space-between;
              align-items: flex-end;
              color: var(--color-grey);
              font-size: 11px;
            }

            .footer-meta {
              line-height: 1.8;
            }

            .signatures {
              display: flex; gap: 40px;
            }

            .signature-block {
              text-align: center;
            }

            .signature-line {
              width: 140px;
              border-top: 2px solid var(--color-blue);
              margin-bottom: 8px;
              margin-left: auto;
              margin-right: auto;
            }
      </style>
  </head>

  <body>
      <div class="header-full-width">
          <div class="logo-container">
              ${logoBase64 ? `<img src="${logoBase64}" class="logo-img" />` : ""}
          </div>
          <div class="header-text">
              <div class="society-name">Vivek Education Society's</div>
              <div class="college-name">VIVEK COLLEGE OF COMMERCE</div>
              <div class="autonomous-tag">(Autonomous)</div>
          </div>
      </div>

      <div class="content-container">
          <div class="report-heading">Official Event Record</div>

          <!-- PROGRAM NAME + TYPE BANNER -->
          <div class="program-banner">
              <div class="program-banner-name">${data.Program_Name}</div>
              ${
                data.Program_Type
                  ? `
              <div class="program-type-badge">${data.Program_Type}</div>`
                  : ""
              }
          </div>

          <hr class="section-divider" />

          <!-- INFO GRID -->
          <div class="record-grid">

              <div class="data-box">
                  <div class="box-label">Organizing Department / Committee</div>
                  <div class="box-value">
                      ${[data.department_name, data.committee_name].filter(Boolean).join(" / ") || "N/A"}
                  </div>
              </div>

              <div class="data-box">
                  <div class="box-label">Teacher Coordinator</div>
                  <div class="box-value">${data.Teacher_Coordinator || "N/A"}</div>
              </div>

              <div class="data-box">
                  <div class="box-label">Start Date & Time</div>
                  <div class="box-value">${new Date(data.Start_Date).toLocaleDateString("en-IN", { day: "2-digit", month: "long", year: "numeric" })}</div>
                  <div class="time-subtext">${data.Start_Time}</div>
              </div>

              <div class="data-box">
                  <div class="box-label">End Date & Time</div>
                  <div class="box-value">${new Date(data.End_Date).toLocaleDateString("en-IN", { day: "2-digit", month: "long", year: "numeric" })}</div>
                  <div class="time-subtext">${data.End_Time}</div>
              </div>

              <div class="data-box">
                  <div class="box-label">Total Participants</div>
                  <div class="box-value">${data.Total_Participants}</div>
                  <div class="muted-subtext">
                      Male: ${data.Male_Participants || 0} &nbsp;|&nbsp; Female: ${data.Female_Participants || 0}
                  </div>
              </div>

              <div class="data-box">
                  <div class="box-label">Logistics</div>
                  <div class="box-value">${data.Duration_Hours} Hours</div>
                  <div class="time-subtext">Budget Allocated: ₹${data.Budget_Allocated || 0}</div>
              </div>

          </div>

          <!-- FOOTER -->
          <div class="footer-section">
              <div class="footer-meta">
                  Event Reference ID: #${data.event_id} <br> Date of Report: ${new Date().toLocaleDateString("en-IN", { day: "2-digit", month: "long", year: "numeric" })}
              </div>
              <div class="signatures">
                  <div class="signature-block">
                      <div class="signature-line"></div>
                      <div>Teacher Coordinator</div>
                  </div>
                  <div class="signature-block">
                      <div class="signature-line"></div>
                      <div>Principal / HOD</div>
                  </div>
              </div>
          </div>

      </div>
  </body>

  </html>
`;
};
module.exports = { buildEventHtml };
