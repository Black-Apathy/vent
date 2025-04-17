const connection = require("../db");
const moment = require("moment");

exports.submitData = (req, res) => {
  const { pn, pt, nof, sd, ed, st, et } = req.body;

  if (pn && pt && sd && st) {
    const formattedStartDate = moment(sd, "D/M/YYYY").format("YYYY-MM-DD");
    const formattedEndDate = moment(ed, "D/M/YYYY").format("YYYY-MM-DD");
    const formattedStartTime = moment(st, "hh:mm A").format("HH:mm:ss");
    const formattedEndTime = moment(et, "hh:mm A").format("HH:mm:ss");

    const mysql_qry =
      "INSERT INTO college_events (Program_Name, Program_Type, No_of_Participants, Start_Date, End_Date, Start_Time, End_Time) VALUES (?, ?, ?, ?, ?, ?)";

    connection.query(
      mysql_qry,
      [
        pn,
        pt,
        nof,
        formattedStartDate,
        formattedEndDate,
        formattedStartTime,
        formattedEndTime,
      ],
      (err) => {
        if (err) {
          console.error("Error inserting data:", err);
          res
            .status(500)
            .json({ status: "error", message: "Error inserting data" });
          return;
        }
        res
          .status(201)
          .json({ status: "success", message: "Data inserted successfully" });
      }
    );
  } else {
    res
      .status(400)
      .json({ message: "Missing Program Name, Program Type, Number of Participants, Start Date, or Start Time" });
  }
};

exports.getData = (req, res) => {
  const mysql_qry = "SELECT * FROM college_events";

  connection.query(mysql_qry, (err, results) => {
    if (err) {
      console.error("Error fetching data:", err);
      res.status(500).json({ message: "Error fetching data" });
      return;
    }
    res.status(200).json(results);
  });
};

// Controller function to get a single event by ID
exports.getEventById = (req, res) => {
  const eventId = req.params.id; // Extract event_id from the URL

  const mysql_qry = "SELECT * FROM college_events WHERE event_id = ?"; // Query for a specific event

  connection.query(mysql_qry, [eventId], (err, results) => {
    if (err) {
      console.error("Error fetching data:", err);
      res.status(500).json({ message: "Error fetching data" });
      return;
    }

    if (results.length === 0) {
      return res.status(404).json({ message: "Event not found" });
    }

    res.status(200).json(results[0]); // Return the first (and only) event
  });
};

exports.deleteEvent = (req, res) => {
  const eventId = req.params.event_id;

  if (!eventId) {
    return res.status(400).json({ message: "Event ID is required" });
  }

  const deleteQuery = "DELETE FROM college_events WHERE event_id = ?";

  connection.query(deleteQuery, [eventId], (err, result) => {
    if (err) {
      console.error("Error deleting event:", err);
      return res.status(500).json({ message: "Failed to delete event" });
    }

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: "Event not found" });
    }

    return res.status(200).json({ message: "Event deleted successfully" });
  });
};

// Function to update event details (partial updates)
exports.updateEvent = async (req, res) => {
  const { event_id } = req.params;  // get event_id from the URL
  const updateData = req.body;  // data to be updated

  // Dynamically build the SQL query based on provided fields
  const fieldsToUpdate = [];
  const valuesToUpdate = [];

  // Check if the fields are provided and add them to the update query
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
  // Add more fields as needed

  if (fieldsToUpdate.length === 0) {
    return res.status(400).json({ message: "No valid fields to update" });
  }

  // Add the event_id to the end of the query to identify which event to update
  valuesToUpdate.push(event_id);

  const sqlQuery = `UPDATE college_events SET ${fieldsToUpdate.join(', ')} WHERE event_id = ?`;

  try {
    const result = await new Promise((resolve, reject) => {
      connection.query(sqlQuery, valuesToUpdate, (err, result) => {
        if (err) {
          console.error("Error updating event:", err);
          reject(err);
        } else {
          resolve(result);
        }
      });
    });

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: "Event not found" });
    }

    return res.status(200).json({ message: "Event updated successfully" });
  } catch (error) {
    console.error("Error during update process:", error);
    return res.status(500).json({ message: "An error occurred while updating the event" });
  }
};
