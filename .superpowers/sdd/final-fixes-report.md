# Final Fixes Report — IMFA Gate Pass Control

## Fix 1 — Pass number race condition (DONE)
- `alter-gate-pass.sql`: Added `CREATE SEQUENCE IF NOT EXISTS gate_pass_seq` and `UNIQUE` constraint on `pass_no`.
- `GatePassRepository.java`: Added `nextPassSeq()` native query using `nextval('gate_pass_seq')`.
- `GatePassService.java`: `generatePassNo()` now uses `repo.nextPassSeq()` for atomic sequence-based numbering with IST year.

## Fix 2 — DetailDrawer swallows errors (DONE)
- `DetailDrawer.tsx`: Added `actionError` state, updated `doAction` to catch and surface errors, added error banner in sticky footer before action buttons.

## Fix 3 — Timezone LocalDate.now() without ZoneId (DONE)
- `GatePassService.java`: Added `private static final ZoneId IST = ZoneId.of("Asia/Kolkata")`. All `LocalDate.now()` and `LocalTime.now()` calls now pass `IST`.
- `DashboardService.java`: Added same `IST` constant. Both `getStats()` and `getChartData()` use `LocalDate.now(IST)`.

## Fix 4 — findByVisitdate() loads all photos into heap (DONE)
- `GatePassRepository.java`: Added `countByVisitDate()` and `countByLocationForDate()` query methods.
- `DashboardService.java`: `getStats()` now uses `countByVisitDate` and `countByVisitDateAndStatus` (no entity loading). `getChartData()` uses `countByLocationForDate` aggregation query — no photo bytes loaded.

## Fix 5 — findByVisitdate case mismatch (DONE)
- `GatePassRepository.java`: Removed derived methods `findByVisitdate` and `countByVisitdateAndStatus`. Replaced with explicit `@Query` JPQL methods: `findByVisitDate`, `countByVisitDate`, `countByVisitDateAndStatus`.
- `DashboardService.java` and `GatePassService.java`: Updated all call sites to new method names.

## Fix 6 — JPQL boolean literal in findAllByFilters (DONE)
- `GatePassRepository.java`: Changed `@Param("showAllDates") boolean showAllDates` to `String showAllDates`; JPQL condition changed from `:showAllDates = true` to `:showAllDates = 'true'`.
- `GatePassService.java`: `list()` now passes `String.valueOf(showAllDates)`.

## Fix 7 — .gitignore for Spring Boot API (DONE)
- Created `imfa-gate-pass-api/.gitignore` excluding `target/`, `*.class`, `*.jar`, `*.war`, `.DS_Store`, `.idea/`, `*.iml`, `application-local.properties`.

## Fix 8 — GlobalExceptionHandler: DataIntegrityViolationException (DONE)
- `GlobalExceptionHandler.java`: Added `handleDataIntegrity` handler for `DataIntegrityViolationException` returning HTTP 409 CONFLICT, placed before the generic `RuntimeException` handler.

## Status
All 8 fixes applied. No regressions introduced to existing method signatures or response contracts.
