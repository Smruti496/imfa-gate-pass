package com.imfa.gatepass.repository;

import com.imfa.gatepass.model.GatePass;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface GatePassRepository extends JpaRepository<GatePass, UUID> {

    @Query("SELECT g FROM GatePass g WHERE g.visitDate = :date")
    List<GatePass> findByVisitDate(@Param("date") String date);

    @Query("SELECT COUNT(g) FROM GatePass g WHERE g.visitDate = :date")
    long countByVisitDate(@Param("date") String date);

    @Query("SELECT COUNT(g) FROM GatePass g WHERE g.visitDate = :date AND g.status = :status")
    long countByVisitDateAndStatus(@Param("date") String date, @Param("status") String status);

    @Query("SELECT COUNT(g) FROM GatePass g WHERE g.status = :status")
    long countByStatus(@Param("status") String status);

    @Query("SELECT g.location, COUNT(g) FROM GatePass g WHERE g.visitDate = :date GROUP BY g.location")
    List<Object[]> countByLocationForDate(@Param("date") String date);

    @Query("""
        SELECT g FROM GatePass g WHERE
          (:showAllDates = 'true' OR g.visitDate = :date)
          AND (:location = 'all' OR g.location = :location)
          AND (:status = 'all' OR g.status = :status)
          AND (:query = '' OR
               LOWER(g.visitorName) LIKE :qlike OR
               LOWER(g.companyName) LIKE :qlike OR
               LOWER(g.photoId)     LIKE :qlike OR
               LOWER(g.passNo)      LIKE :qlike OR
               LOWER(g.whomToVisit) LIKE :qlike)
        ORDER BY
          CASE g.status WHEN 'onsite' THEN 0 WHEN 'pending' THEN 1 ELSE 2 END,
          g.createdTime DESC NULLS LAST
        """)
    Page<GatePass> findAllByFilters(
        @Param("date")         String date,
        @Param("location")     String location,
        @Param("status")       String status,
        @Param("query")        String query,
        @Param("qlike")        String qlike,
        @Param("showAllDates") String showAllDates,
        Pageable pageable
    );

    @Query(value = "SELECT nextval('gate_pass_seq')", nativeQuery = true)
    Long nextPassSeq();

    @Query(value = "SELECT COALESCE(MAX(CAST(SPLIT_PART(pass_no, '-', 3) AS BIGINT)), 0) FROM gate_pass WHERE pass_no LIKE CONCAT('GP-', CAST(:year AS TEXT), '-%')", nativeQuery = true)
    Long maxPassNoForYear(@Param("year") int year);

    @Query("SELECT COUNT(g) FROM GatePass g WHERE g.visitDate LIKE :yearPrefix%")
    long countByYearPrefix(@Param("yearPrefix") String yearPrefix);

    @Query(value = "SELECT status, COUNT(*) FROM gate_pass GROUP BY status", nativeQuery = true)
    List<Object[]> countGroupByStatus();

    @Query(value = "SELECT location, status, COUNT(*) FROM gate_pass GROUP BY location, status", nativeQuery = true)
    List<Object[]> countGroupByLocationAndStatus();

    @Query(value = "SELECT COALESCE(gender, 'Unknown'), COUNT(*) FROM gate_pass GROUP BY gender", nativeQuery = true)
    List<Object[]> countGroupByGender();

    @Query(value = "SELECT LPAD(EXTRACT(HOUR FROM createdtime AT TIME ZONE 'Asia/Kolkata')::text, 2, '0'), COUNT(*) FROM gate_pass WHERE createdtime IS NOT NULL GROUP BY 1 ORDER BY 1", nativeQuery = true)
    List<Object[]> countGroupByHour();

    @Query(value = "SELECT TO_CHAR(createdtime AT TIME ZONE 'Asia/Kolkata', 'Mon YYYY'), DATE_TRUNC('month', createdtime AT TIME ZONE 'Asia/Kolkata'), COUNT(*) FROM gate_pass WHERE createdtime IS NOT NULL GROUP BY 1, 2 ORDER BY 2", nativeQuery = true)
    List<Object[]> countGroupByMonth();

    @Query(value = "SELECT 'Q' || EXTRACT(QUARTER FROM createdtime AT TIME ZONE 'Asia/Kolkata')::text || ' ' || EXTRACT(YEAR FROM createdtime AT TIME ZONE 'Asia/Kolkata')::text, DATE_TRUNC('quarter', createdtime AT TIME ZONE 'Asia/Kolkata'), COUNT(*) FROM gate_pass WHERE createdtime IS NOT NULL GROUP BY 1, 2 ORDER BY 2", nativeQuery = true)
    List<Object[]> countGroupByQuarter();

    @Query(value = "SELECT EXTRACT(YEAR FROM createdtime AT TIME ZONE 'Asia/Kolkata')::text, COUNT(*) FROM gate_pass WHERE createdtime IS NOT NULL GROUP BY 1 ORDER BY 1", nativeQuery = true)
    List<Object[]> countGroupByYear();
}
