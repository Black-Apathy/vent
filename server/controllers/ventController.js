const connection = require("../db");
const moment = require("moment");

exports.submitData = (req, res) => {
  const { pn, pt, sd, ed, st, et } = req.body;

  if (pn && pt && sd && st) {
    const formattedStartDate = moment(sd, "D/M/YYYY").format("YYYY-MM-DD");
    const formattedEndDate = moment(ed, "D/M/YYYY").format("YYYY-MM-DD");
    const formattedStartTime = moment(st, "hh:mm A").format("HH:mm:ss");
    const formattedEndTime = moment(et, "hh:mm A").format("HH:mm:ss");

    const mysql_qry =
      "INSERT INTO vent_app_details (Program_Name, Program_Type, Start_Date, End_Date, Start_Time, End_Time) VALUES (?, ?, ?, ?, ?, ?)";

    connection.query(
      mysql_qry,
      [
        pn,
        pt,
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
      .json({ message: "Missing Program Name, Program Type, Start Date, or Start Time" });
  }
};

exports.getData = (req, res) => {
  const mysql_qry = "SELECT * FROM vent_app_details";

  connection.query(mysql_qry, (err, results) => {
    if (err) {
      console.error("Error fetching data:", err);
      res.status(500).json({ message: "Error fetching data" });
      return;
    }
    res.status(200).json(results);
  });
};
