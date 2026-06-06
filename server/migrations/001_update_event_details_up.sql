-- Rename the existing column to preserve total counts
ALTER TABLE college_events
CHANGE COLUMN No_of_Participants Total_Participants INT(11);

-- Add the new granular columns
ALTER TABLE college_events
ADD COLUMN Male_Participants INT(11) DEFAULT 0,
ADD COLUMN Female_Participants INT(11) DEFAULT 0,
ADD COLUMN Teacher_Coordinator VARCHAR(100) NULL,
ADD COLUMN Budget_Allocated DECIMAL(10, 2) NULL;
