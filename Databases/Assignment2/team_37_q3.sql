SELECT
    curr."ID" AS appointment_id,
    p."Full_name" AS patient_name,
    d."Full_name" AS doctor_name,
    curr."Date",
    curr."Time",
    COUNT(prev."ID") AS earlier_appointments
FROM "APPOINTMENT" AS curr
JOIN "PATIENT" AS p
    ON p."ID" = curr."Patient_ID"
JOIN "DOCTOR" AS d
    ON d."ID" = curr."Doctor_ID"
JOIN "APPOINTMENT" AS prev
    ON prev."Patient_ID" = curr."Patient_ID"
   AND prev."Date" < curr."Date"
GROUP BY
    curr."ID",
    p."Full_name",
    d."Full_name",
    curr."Date",
    curr."Time"
HAVING COUNT(prev."ID") >= 3
ORDER BY
    patient_name ASC,
    curr."Date" ASC,
    curr."Time" ASC;
