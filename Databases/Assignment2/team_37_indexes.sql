CREATE INDEX IF NOT EXISTS idx_q1_appointment_doctor_status
    ON "APPOINTMENT" ("Doctor_ID", "Status")
    INCLUDE ("ID");

CREATE INDEX IF NOT EXISTS idx_q1_doctor_department_id
    ON "DOCTOR" ("Department_ID");

CREATE INDEX IF NOT EXISTS idx_q1_doctor_id_fullname
    ON "DOCTOR" ("ID")
    INCLUDE ("Full_name");

CREATE INDEX idx_q2_appointment_doctor_patient ON "APPOINTMENT" ("Doctor_ID", "Patient_ID");

CREATE INDEX idx_q2_doctor_department ON "DOCTOR" ("Department_ID");

CREATE INDEX idx_q2_diagnosis_appointment ON "DIAGNOSIS" ("Appointment_ID");

CREATE INDEX idx_q2_appointment_procedure_appointment ON "APPOINTMENT_PROCEDURE" ("Appointment_ID");

CREATE INDEX idx_q2_appointment_procedure_procedure ON "APPOINTMENT_PROCEDURE" ("Procedure_ID");

CREATE INDEX idx_q2_procedure_price ON "PROCEDURE" ("Price");

CREATE INDEX idx_q3_appointment_patient_date_time
    ON "APPOINTMENT" ("Patient_ID", "Date", "Time");