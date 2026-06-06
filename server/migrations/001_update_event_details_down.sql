-- Remove the newly added columns
ALTER TABLE college_events
DROP COLUMN Male_Participants,
DROP COLUMN Female_Participants,
DROP COLUMN Teacher_Coordinator,
DROP COLUMN Budget_Allocated;

-- Revert the total column back to its original name
ALTER TABLE college_events
CHANGE COLUMN Total_Participants No_of_Participants INT(11);
