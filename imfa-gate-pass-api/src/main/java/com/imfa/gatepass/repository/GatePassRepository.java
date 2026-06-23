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
          g.createdTime DESC
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

    @Query("SELECT COUNT(g) FROM GatePass g WHERE g.visitDate LIKE :yearPrefix%")
    long countByYearPrefix(@Param("yearPrefix") String yearPrefix);
}
