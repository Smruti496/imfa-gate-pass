package com.imfa.gatepass.repository;

import com.imfa.gatepass.model.GatePass;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface GatePassRepository extends JpaRepository<GatePass, UUID> {

    List<GatePass> findByVisitdate(String visitdate);

    long countByVisitdateAndStatus(String visitdate, String status);

    @Query("""
        SELECT g FROM GatePass g WHERE
          (:showAllDates = true OR g.visitDate = :date)
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
        @Param("showAllDates") boolean showAllDates,
        Pageable pageable
    );

    @Query("SELECT COUNT(g) FROM GatePass g WHERE g.visitDate LIKE :yearPrefix%")
    long countByYearPrefix(@Param("yearPrefix") String yearPrefix);
}
