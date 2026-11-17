package service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reusable input checks so the GUI does not repeat them in every button listener.
 * All methods are static and never throw.
 */
public class Validator {

    // Values the GUI combo boxes should use. They match what the database stores
    // (schema.sql default status is 'suspected').
    public static final String[] LIVESTOCK_TYPES = {"Cattle", "Goats", "Sheep", "Poultry", "Other"};
    public static final String[] REPORT_STATUSES = {"suspected", "under review", "verified"};

    // Acceptable dates: not before this, and not in the future.
    public static final LocalDate EARLIEST_DATE = LocalDate.of(2000, 1, 1);

    private Validator() { }

    /** True if the value is null, empty or only whitespace. */
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /** True only for a whole number greater than zero (no letters, no decimals). */
    public static boolean isPositiveInteger(String value) {
        if (isBlank(value)) return false;
        try {
            return Integer.parseInt(value.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /** True for a real calendar date written yyyy-MM-dd, within the acceptable range. */
    public static boolean isValidDate(String value) {
        if (isBlank(value)) return false;
        try {
            return isDateInRange(LocalDate.parse(value.trim()));
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isDateInRange(LocalDate date) {
        return date != null
                && !date.isBefore(EARLIEST_DATE)
                && !date.isAfter(LocalDate.now());
    }

    public static boolean isAllowedLivestockType(String value) {
        return value != null && Arrays.asList(LIVESTOCK_TYPES).contains(value.trim());
    }

    public static boolean isAllowedStatus(String value) {
        return value != null && Arrays.asList(REPORT_STATUSES).contains(value.trim());
    }

    /**
     * Checks the whole report form and returns readable messages.
     * An empty list means everything is valid.
     */
    public static List<String> validateReportInput(boolean diseaseChosen, boolean regionChosen,
                                                   String livestockType, String date,
                                                   String affectedCount, String status) {
        List<String> errors = new ArrayList<>();
        if (!diseaseChosen) errors.add("Please select a disease.");
        if (!regionChosen)  errors.add("Please select a region.");
        if (!isAllowedLivestockType(livestockType))
            errors.add("Please choose a livestock type from the list.");
        if (!isValidDate(date))
            errors.add("Enter a valid date as yyyy-MM-dd (not before "
                    + EARLIEST_DATE + " and not in the future).");
        if (!isPositiveInteger(affectedCount))
            errors.add("Affected animals must be a whole number greater than zero.");
        if (!isAllowedStatus(status))
            errors.add("Please choose a status: " + String.join(", ", REPORT_STATUSES) + ".");
        return errors;
    }
}
