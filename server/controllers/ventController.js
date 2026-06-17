const db = require("../utils/dbUtils");
const { generatePdfBuffer } = require("../utils/pdf/pdfService");
const { buildEventHtml } = require("../utils/pdf/pdfTemplate");
const moment = require("moment");

/**
 * Inserts a new event into the college_events table.
 * Unified to expect full column names matching the PATCH route.
 */
exports.submitData = async (req, res) => {
  const {
    Program_Name,
    Program_Type,
    Male_Participants,
    Female_Participants,
    Teacher_Coordinator,
    Budget_Allocated,
    Start_Date,
    End_Date,
    Start_Time,
    End_Time,
    department_id,
    committee_id,
  } = req.body;

  if (!Program_Name || !Program_Type || !Start_Date || !Start_Time) {
    return res.status(400).json({
      status: "error",
      message:
        "Missing essential fields (Program Name, Type, Start Date, or Start Time)",
    });
  }

  try {
    // Safely parse dates. If End_Date is missing, default it to Start_Date
    const formattedStartDate = moment(Start_Date, [
      "D/M/YYYY",
      "YYYY-MM-DD",
    ]).format("YYYY-MM-DD");
    const formattedEndDate = End_Date
      ? moment(End_Date, ["D/M/YYYY", "YYYY-MM-DD"]).format("YYYY-MM-DD")
      : formattedStartDate;

    const formattedStartTime = moment(Start_Time, [
      "hh:mm A",
      "HH:mm:ss",
    ]).format("HH:mm:ss");
    const formattedEndTime = End_Time
      ? moment(End_Time, ["hh:mm A", "HH:mm:ss"]).format("HH:mm:ss")
      : formattedStartTime;

    const mysql_qry = `INSERT INTO college_events
            (Program_Name, Program_Type, Male_Participants, Female_Participants, Teacher_Coordinator, Budget_Allocated, Start_Date, End_Date, Start_Time, End_Time, department_id, committee_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`;

    await db.query(mysql_qry, [
      Program_Name,
      Program_Type,
      Male_Participants || 0,
      Female_Participants || 0,
      Teacher_Coordinator || null,
      Budget_Allocated || null,
      formattedStartDate,
      formattedEndDate,
      formattedStartTime,
      formattedEndTime,
      department_id || null,
      committee_id || null,
    ]);

    res
      .status(201)
      .json({ status: "success", message: "Data inserted successfully" });
  } catch (err) {
    console.error("Error inserting data:", err);
    res.status(500).json({ status: "error", message: "Error inserting data" });
  }
};

/**
 * Fetches all events from the college_events table.
 */
exports.getEvents = async (req, res) => {
  const mysql_qry = `
      SELECT ce.*,
      (ce.Male_Participants + ce.Female_Participants) AS Total_Participants,
      ROUND(TIME_TO_SEC(TIMEDIFF(ce.End_Time, ce.Start_Time)) / 3600, 2) AS Duration_Hours,
      d.department_name,
      c.committee_name
      FROM college_events ce
      LEFT JOIN departments d ON ce.department_id = d.id
      LEFT JOIN committees c ON ce.committee_id = c.id`;

  try {
    const results = await db.query(mysql_qry);
    res.status(200).json(results);
  } catch (err) {
    console.error("Error fetching data:", err);
    res.status(500).json({ message: "Error fetching data" });
  }
};

/**
 * Fetches a single event by event_id from the college_events table.
 * Expects: event_id or id in req.params
 */
exports.getEventById = async (req, res) => {
  const eventId = req.params.event_id || req.params.id;

  if (!eventId) {
    return res.status(400).json({ message: "Event ID is required" });
  }

  const mysql_qry = `
      SELECT ce.*,
      (ce.Male_Participants + ce.Female_Participants) AS Total_Participants,
      ROUND(TIME_TO_SEC(TIMEDIFF(ce.End_Time, ce.Start_Time)) / 3600, 2) AS Duration_Hours,
      d.department_name,
      c.committee_name
      FROM college_events ce
      LEFT JOIN departments d ON ce.department_id = d.id
      LEFT JOIN committees c ON ce.committee_id = c.id
      WHERE ce.event_id = ?`;

  try {
    const results = await db.query(mysql_qry, [eventId]);
    if (!results || results.length === 0) {
      return res.status(404).json({ message: "Event not found" });
    }
    res.status(200).json(results[0]);
  } catch (err) {
    console.error("Error fetching event:", err);
    res.status(500).json({ message: "Error fetching event" });
  }
};

/**
 * Deletes an event by event_id from the college_events table.
 * Expects: event_id or id in req.params
 */
exports.deleteEvent = async (req, res) => {
  const eventId = req.params.event_id || req.params.id;

  if (!eventId) {
    return res.status(400).json({ message: "Event ID is required" });
  }

  const deleteQuery = "DELETE FROM college_events WHERE event_id = ?";

  try {
    const result = await db.query(deleteQuery, [eventId]);
    // Depending on your dbUtils, result.affectedRows may be in result or result[0]
    const affectedRows =
      result.affectedRows !== undefined
        ? result.affectedRows
        : result[0]?.affectedRows || 0;
    if (affectedRows === 0) {
      return res.status(404).json({ message: "Event not found" });
    }
    return res.status(200).json({ message: "Event deleted successfully" });
  } catch (err) {
    console.error("Error deleting event:", err);
    return res.status(500).json({ message: "Failed to delete event" });
  }
};

/**
 * Updates event details (partial updates supported).
 * Only fields present in req.body will be updated.
 * Expects: event_id or id in req.params, and any updatable fields in req.body
 */
exports.updateEvent = async (req, res) => {
  const eventId = req.params.event_id || req.params.id;
  const updateData = req.body;

  if (!eventId) {
    return res.status(400).json({ message: "Event ID is required" });
  }

  const fieldsToUpdate = [];
  const valuesToUpdate = [];

  if (updateData.Program_Name !== undefined) {
    fieldsToUpdate.push("Program_Name = ?");
    valuesToUpdate.push(updateData.Program_Name);
  }
  if (updateData.Program_Type !== undefined) {
    fieldsToUpdate.push("Program_Type = ?");
    valuesToUpdate.push(updateData.Program_Type);
  }
  if (updateData.Male_Participants !== undefined) {
    fieldsToUpdate.push("Male_Participants = ?");
    valuesToUpdate.push(updateData.Male_Participants);
  }
  if (updateData.Female_Participants !== undefined) {
    fieldsToUpdate.push("Female_Participants = ?");
    valuesToUpdate.push(updateData.Female_Participants);
  }
  if (updateData.Teacher_Coordinator !== undefined) {
    fieldsToUpdate.push("Teacher_Coordinator = ?");
    valuesToUpdate.push(updateData.Teacher_Coordinator);
  }
  if (updateData.Budget_Allocated !== undefined) {
    fieldsToUpdate.push("Budget_Allocated = ?");
    valuesToUpdate.push(updateData.Budget_Allocated);
  }
  if (updateData.Start_Date !== undefined) {
    fieldsToUpdate.push("Start_Date = ?");
    valuesToUpdate.push(updateData.Start_Date);
  }
  if (updateData.End_Date !== undefined) {
    fieldsToUpdate.push("End_Date = ?");
    valuesToUpdate.push(updateData.End_Date);
  }
  if (updateData.Start_Time !== undefined) {
    fieldsToUpdate.push("Start_Time = ?");
    valuesToUpdate.push(updateData.Start_Time);
  }
  if (updateData.End_Time !== undefined) {
    fieldsToUpdate.push("End_Time = ?");
    valuesToUpdate.push(updateData.End_Time);
  }
  if (updateData.department_id !== undefined) {
    fieldsToUpdate.push("department_id = ?");
    valuesToUpdate.push(updateData.department_id);
  }
  if (updateData.committee_id !== undefined) {
    fieldsToUpdate.push("committee_id = ?");
    valuesToUpdate.push(updateData.committee_id);
  }

  if (fieldsToUpdate.length === 0) {
    return res.status(400).json({ message: "No valid fields to update" });
  }

  valuesToUpdate.push(eventId);

  const sqlQuery = `UPDATE college_events SET ${fieldsToUpdate.join(
    ", ",
  )} WHERE event_id = ?`;

  try {
    const result = await db.query(sqlQuery, valuesToUpdate);
    const affectedRows =
      result.affectedRows !== undefined
        ? result.affectedRows
        : result[0]?.affectedRows || 0;
    if (affectedRows === 0) {
      return res.status(404).json({ message: "Event not found" });
    }
    return res.status(200).json({ message: "Event updated successfully" });
  } catch (error) {
    console.error("Error during update process:", error);
    return res
      .status(500)
      .json({ message: "An error occurred while updating the event" });
  }
};

/**
 * Generates and downloads an official Event Report PDF.
 * Fetches event data from the database and compiles it into a formatted PDF document.
 * Expects: event ID in req.params.id
 * Returns: Binary PDF file with 'Content-Disposition: attachment' for immediate download.
 */
exports.downloadEventPdf = async (req, res) => {
  try {
    const eventId = req.params.id;

    // 1. Fetch from DB
    // The destructuring [rows] here is actually extracting the first result from the query array
    const [rows] = await db.query(
      `
          SELECT ce.*,
          (ce.Male_Participants + ce.Female_Participants) AS Total_Participants,
          ROUND(TIME_TO_SEC(TIMEDIFF(ce.End_Time, ce.Start_Time)) / 3600, 2) AS Duration_Hours,
          d.department_name,
          c.committee_name
          FROM college_events ce
          LEFT JOIN departments d ON ce.department_id = d.id
          LEFT JOIN committees c ON ce.committee_id = c.id
          WHERE ce.event_id = ?`,
      [eventId],
    );

    // 2. Check if we actually got data
    if (!rows) {
      return res.status(404).json({ error: "Event not found" });
    }

    // --- THE FIX IS HERE ---
    // 'rows' is already the object we want. We don't need rows[0].
    const eventData = rows;

    // 3. Generate PDF
    const pdfBuffer = await generatePdfBuffer(eventData);

    res.set({
      "Content-Type": "application/pdf",
      "Content-Length": pdfBuffer.length,
      "Content-Disposition": `attachment; filename="Event_${eventId}.pdf"`,
    });
    res.send(pdfBuffer);
  } catch (error) {
    console.error("PDF Error:", error);
    res.status(500).json({ error: "Could not generate PDF" });
  }
};

// Temporary route to test the PDF layout
exports.previewPdfHtml = (req, res) => {
  const dummyData = {
    event_id: 999,
    Program_Name: "AWS Cloud Computing Workshop",
    Program_Type: "Educational",
    department_name: "B.Sc. Information Technology",
    Teacher_Coordinator: "Prof. Smitha",
    Total_Participants: 120,
    Male_Participants: 70,
    Female_Participants: 50,
    Duration_Hours: 4.5,
    Start_Date: "2026-06-10T00:00:00.000Z",
    Start_Time: "10:00:00",
    End_Date: "2026-06-10T00:00:00.000Z",
    End_Time: "14:30:00",
    Budget_Allocated: 5000,
    committee_name: null,
  };

  const htmlContent = buildEventHtml(dummyData, "");

  // 1. Force Content-Type to HTML
  // 2. Temporarily override CSP to allow our inline styles and Google Fonts
  res.set({
    "Content-Type": "text/html",
    "Content-Security-Policy":
      "default-src 'self'; style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; font-src https://fonts.gstatic.com; img-src 'self' data:;",
  });

  res.send(htmlContent);
};

// ==========================================
// LOOKUP APIs FOR ANDROID DROPDOWNS
// ==========================================

// GET: Fetch all Departments
exports.getDepartments = async (req, res) => {
  try {
    const query = `
            SELECT
                id AS department_id,
                department_name AS department_name
            FROM departments
            ORDER BY department_name ASC
        `;

    const departments = await db.query(query);
    res.status(200).json(departments);
  } catch (error) {
    console.error("Error fetching departments:", error);
    res.status(500).json({ error: "Failed to fetch departments" });
  }
};

// GET: Fetch all Committees
exports.getCommittees = async (req, res) => {
  try {
    const query = `
            SELECT
                id AS committee_id,
                committee_name AS committee_name
            FROM committees
            ORDER BY committee_name ASC
        `;

    const committees = await db.query(query);
    res.status(200).json(committees);
  } catch (error) {
    console.error("Error fetching committees:", error);
    res.status(500).json({ error: "Failed to fetch committees" });
  }
};
