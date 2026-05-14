WITH appointment_stats AS (
    SELECT
        a."Patient_ID",
        COUNT(*) AS total_appointments,
        COUNT(DISTINCT d."Department_ID") AS distinct_departments
    FROM "APPOINTMENT" AS a
    JOIN "DOCTOR" AS d
        ON d."ID" = a."Doctor_ID"
    GROUP BY a."Patient_ID"
),
diagnosis_stats AS (
    SELECT
        a."Patient_ID",
        COUNT(di."ID") AS total_diagnoses
    FROM "APPOINTMENT" AS a
    LEFT JOIN "DIAGNOSIS" AS di
        ON di."Appointment_ID" = a."ID"
    GROUP BY a."Patient_ID"
),
spending_stats AS (
    SELECT
        a."Patient_ID",
        COALESCE(SUM(pr."Price"), 0) AS total_spent
    FROM "APPOINTMENT" AS a
    LEFT JOIN "APPOINTMENT_PROCEDURE" AS ap
        ON ap."Appointment_ID" = a."ID"
    LEFT JOIN "PROCEDURE" AS pr
        ON pr."ID" = ap."Procedure_ID"
    GROUP BY a."Patient_ID"
),
patient_summary AS (
    SELECT
        p."ID" AS patient_id,
        p."Full_name",
        COALESCE(ast.distinct_departments, 0) AS distinct_departments,
        COALESCE(ast.total_appointments, 0) AS total_appointments,
        COALESCE(dst.total_diagnoses, 0) AS total_diagnoses,
        COALESCE(sst.total_spent, 0) AS total_spent
    FROM "PATIENT" AS p
    LEFT JOIN appointment_stats AS ast
        ON ast."Patient_ID" = p."ID"
    LEFT JOIN diagnosis_stats AS dst
        ON dst."Patient_ID" = p."ID"
    LEFT JOIN spending_stats AS sst
        ON sst."Patient_ID" = p."ID"
),
avg_spending AS (
    SELECT AVG(total_spent) AS avg_total_spent
    FROM patient_summary
    WHERE total_spent > 0
)
SELECT
    ps.patient_id,
    ps."Full_name",
    ps.distinct_departments,
    ps.total_appointments,
    ps.total_diagnoses,
    ps.total_spent
FROM patient_summary AS ps
CROSS JOIN avg_spending AS av
WHERE ps.distinct_departments >= 3
  AND ps.total_spent > av.avg_total_spent
ORDER BY
    ps.total_spent DESC,
    ps.total_appointments DESC,
    ps."Full_name" ASC;
