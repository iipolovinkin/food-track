package com.foodtracker.shared.repository;

import com.foodtracker.shared.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByEventType(String eventType);

    List<Event> findByUserId(String userId);

    @Query("SELECT COUNT(DISTINCT e.userId) " +
            "FROM Event e " +
            "WHERE e.eventType = :eventType AND e.timestamp >= :fromDate")
    long countDistinctUsersByEventTypeAndTimestampAfter(@Param("eventType") String eventType,
                                                        @Param("fromDate") LocalDateTime fromDate);

    List<Event> findByEventTypeAndTimestampBetween(String eventType, LocalDateTime start, LocalDateTime end);

    @Query(value = """
            SELECT COUNT(*)
            FROM EVENTS E
            WHERE E.EVENT_TYPE = :eventType
            AND E.TIMESTAMP
            BETWEEN :start AND :end
            AND E.PROPERTIES ->> 'category' = :category
            """,
            nativeQuery = true)
    long countByEventTypeAndCategoryAndTimestampBetween(
            @Param("eventType") String eventType,
            @Param("category") String category,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}