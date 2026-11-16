# Shared Contract: Livestock Disease Tracker (CNS 1207)

Status: DRAFT. Nobody writes code against this until all three members have approved the pull request that adds it.
Package: `com.livestocktracker` (sub-packages `model`, `dao`, `service`)

## 1. Ownership

| Person | Owns |
|---|---|
| A | SQL script, sample data, `DBConnection`, `DiseaseReportDAO`, `DiseaseDAO`, `RegionDAO` |
| B | Model classes, `Validator`, `OutbreakChecker`, `DashboardService` |
| C | `Main`, `MainGUI`, all panels, charts, `lib/` folder |

Branches: `feature/database` (A), `feature/logic` (B), `feature/gui` (C). Nobody pushes straight to `main`. Everything goes through a pull request.

## 2. Database

Database name: `livestock_disease_tracker`

| Table | Columns |
|---|---|
| `regions` | `region_id` (PK, AUTO_INCREMENT), `region_name`, `county_name` |
| `diseases` | `disease_id` (PK, AUTO_INCREMENT), `disease_name` (UNIQUE), `description`, `guidance_text` |
| `disease_reports` | `report_id` (PK, AUTO_INCREMENT), `disease_id` (FK), `region_id` (FK), `livestock_type`, `report_date` (DATE), `affected_count` (INT), `report_status` (default 'Reported'), `notes` |

SQL file location in the repo: `database/livestock_disease_tracker.sql`

Credentials: the password is never committed. `DBConnection` reads `db.properties` (git-ignored). A file `db.properties.example` with a fake password is committed so everyone knows the format.

## 3. Agreed values

- Livestock types: `Cattle`, `Goats`, `Sheep`, `Poultry`, `Other`
- Report statuses: `Reported`, `Under Review`, `Verified`
- Date format typed in the GUI: `yyyy-MM-dd`
- Alert rule: count report records (not affected animals) for the same disease and region, flag when count is 5 or more in the last 7 days. The two numbers are constants in `OutbreakChecker`.
- Alert wording: "Alert threshold reached, seek verification". Never "outbreak confirmed".

## 4. Model classes (Person B), package `com.livestocktracker.model`

`DiseaseReport`: `int reportId`, `int diseaseId`, `int regionId`, `String livestockType`, `LocalDate reportDate`, `int affectedCount`, `String reportStatus`, `String notes`, plus display-only `String diseaseName` and `String regionName`.
The two display fields are filled by the JOIN in `getAllReports()` and ignored by add/update. A new report that has not been saved yet uses `reportId = 0`.

`Disease`: `int diseaseId`, `String diseaseName`, `String description`, `String guidanceText`

`Region`: `int regionId`, `String regionName`, `String countyName`

Every class: private fields, constructors, getters and setters.

## 5. Method signatures

All DAO methods throw `SQLException`. The GUI catches it and shows a `JOptionPane` message. DAOs never show dialogs.

```java
// DBConnection (Person A)
public Connection getConnection() throws SQLException;

// DiseaseReportDAO (Person A)
public int addReport(DiseaseReport r) throws SQLException;          // returns generated report_id
public List<DiseaseReport> getAllReports() throws SQLException;     // JOIN diseases + regions
public boolean updateReport(DiseaseReport r) throws SQLException;   // WHERE report_id = ?
public boolean deleteReport(int reportId) throws SQLException;
public Map<String,Integer> getReportCountsByRegion() throws SQLException;
public Map<String,Integer> getReportCountsByMonth() throws SQLException;        // keys like "2026-11"
public Map<String,Integer> getReportCountsByLivestockType() throws SQLException;
public Map<String,Integer> getReportCountsByDisease() throws SQLException;
public int getTotalAffectedAnimals() throws SQLException;

// DiseaseDAO and RegionDAO (Person A), needed for the GUI drop-downs
public List<Disease> getAllDiseases() throws SQLException;
public List<Region> getAllRegions() throws SQLException;

// Validator (Person B), all static
public static boolean isBlank(String value);
public static boolean isPositiveInteger(String value);
public static boolean isValidDate(String value);                    // yyyy-MM-dd
public static boolean isAllowedLivestockType(String value);
public static boolean isAllowedStatus(String value);
public static final String[] LIVESTOCK_TYPES;                       // used by the GUI combo box
public static final String[] REPORT_STATUSES;                       // used by the GUI combo box

// OutbreakChecker (Person B)
public List<String> checkAll() throws SQLException;                 // one message per flagged disease+region

// DashboardService (Person B), the only class the dashboard panel calls
public int getTotalReports() throws SQLException;
public int getTotalAffectedAnimals() throws SQLException;
public int getRegionsRepresented() throws SQLException;
public Map<String,Integer> getReportsByRegion() throws SQLException;
public Map<String,Integer> getReportsByMonth() throws SQLException;
public Map<String,Integer> getReportsByLivestockType() throws SQLException;
public Map<String,Integer> getReportsByDisease() throws SQLException;
```

Every `Map` is a `LinkedHashMap` so the chart order stays stable. `DashboardService` calls the DAO's count methods.

## 6. Decisions the guide left open (tick when all three agree)

- [ ] DAO methods throw `SQLException` and the GUI shows the dialogs
- [ ] Person A also writes `DiseaseDAO` and `RegionDAO` (the guide marks them optional, but the drop-downs need them)
- [ ] `DiseaseReport` carries `diseaseName` and `regionName` for display
- [ ] `Disease` uses `guidanceText` (the guide's model table says `recommendedAction`, but its SQL column is `guidance_text`)
- [ ] Livestock type and status lists as in section 3
- [ ] Alert rule: 5 reports in 7 days, counting records
- [ ] Credentials via git-ignored `db.properties`
- [ ] Lecturer has approved a group of three

## 7. Changing this contract

If a name or signature has to change, edit this file in a pull request and tell the group chat. Do not change a signature silently in your own code.
