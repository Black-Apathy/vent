const db = require("../utils/dbUtils");
const moment = require("moment");

/**
 * Inserts a new event into the college_events table.
 * Expects: pn, pt, nof, sd, ed, st, et in req.body
 */
exports.submitData = async (req, res) => {
  const { pn, pt, nof, sd, ed, st, et } = req.body;

  if (pn && pt && sd && st) {
    const formattedStartDate = moment(sd, "D/M/YYYY").format("YYYY-MM-DD");
    const formattedEndDate = moment(ed, "D/M/YYYY").format("YYYY-MM-DD");
    const formattedStartTime = moment(st, "hh:mm A").format("HH:mm:ss");
    const formattedEndTime = moment(et, "hh:mm A").format("HH:mm:ss");

    const mysql_qry =
      "INSERT INTO college_events (Program_Name, Program_Type, No_of_Participants, Start_Date, End_Date, Start_Time, End_Time) VALUES (?, ?, ?, ?, ?, ?, ?)";

    try {
      await db.query(
        mysql_qry,
        [
          pn,
          pt,
          nof,
          formattedStartDate,
          formattedEndDate,
          formattedStartTime,
          formattedEndTime,
        ]
      );
      res
        .status(201)
        .json({ status: "success", message: "Data inserted successfully" });
    } catch (err) {
      console.error("Error inserting data:", err);
      res
        .status(500)
        .json({ status: "error", message: "Error inserting data" });
    }
  } else {
    res
      .status(400)
      .json({ message: "Missing Program Name, Program Type, Number of Participants, Start Date, or Start Time" });
  }
};

/**
 * Fetches all events from the college_events table.
 */
exports.getData = async (req, res) => {
  const mysql_qry = "SELECT * FROM college_events";

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

  const mysql_qry = "SELECT * FROM college_events WHERE event_id = ?";

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
    const affectedRows = result.affectedRows !== undefined ? result.affectedRows : (result[0]?.affectedRows || 0);
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
  if (updateData.No_of_Participants !== undefined) {
    fieldsToUpdate.push("No_of_Participants = ?");
    valuesToUpdate.push(updateData.No_of_Participants);
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

  if (fieldsToUpdate.length === 0) {
    return res.status(400).json({ message: "No valid fields to update" });
  }

  valuesToUpdate.push(eventId);

  const sqlQuery = `UPDATE college_events SET ${fieldsToUpdate.join(', ')} WHERE event_id = ?`;

  try {
    const result = await db.query(sqlQuery, valuesToUpdate);
    const affectedRows = result.affectedRows !== undefined ? result.affectedRows : (result[0]?.affectedRows || 0);
    if (affectedRows === 0) {
      return res.status(404).json({ message: "Event not found" });
    }
    return res.status(200).json({ message: "Event updated successfully" });
  } catch (error) {
    console.error("Error during update process:", error);
    return res.status(500).json({ message: "An error occurred while updating the event" });
  }
};
