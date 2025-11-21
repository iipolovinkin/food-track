package com.foodtracker.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByEventType(String eventType);

    List<Event> findByUserId(String userId);

    @Query("SELECT COUNT(DISTINCT e.userId) " +
            "FROM Event e " +
            "WHERE e.eventType = :eventType AND e.timestamp >= :fromDate")
    long countDistinctUsersByEventTypeAndTimestampAfter(@Param("eventType") String eventType,
                                                        @Param("fromDate") Instant fromDate);

    List<Event> findByEventTypeAndTimestampBetween(String eventType, Instant start, Instant end);

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
            @Param("start") Instant start,
            @Param("end") Instant end
    );

    // Methods for dashboard metrics
    @Query("SELECT COUNT(DISTINCT e.userId) FROM Event e WHERE e.timestamp >= :since")
    long countDistinctUsersSince(@Param("since") Instant since);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.eventType = :eventType AND e.timestamp >= :since")
    long countByEventTypeSince(@Param("eventType") String eventType, @Param("since") Instant since);

    @Query(value = "SELECT * FROM EVENTS E WHERE E.EVENT_TYPE = :eventType AND E.TIMESTAMP >= :since", nativeQuery = true)
    List<Event> findByEventTypeSince(@Param("eventType") String eventType, @Param("since") Instant since);

    @Query(value = """
            SELECT COUNT(*)
            FROM EVENTS E
            WHERE E.EVENT_TYPE = :eventType
            AND E.TIMESTAMP >= :since
            AND E.PROPERTIES ->> 'category' = :category
            """, nativeQuery = true)
    long countByEventTypeAndCategory(@Param("eventType") String eventType,
                                   @Param("category") String category,
                                   @Param("since") Instant since);

    @Query(value = """
            SELECT *
            FROM EVENTS E
            WHERE E.EVENT_TYPE = :eventType
            AND E.TIMESTAMP >= :since
            AND E.PROPERTIES ->> 'category' = :category
            """, nativeQuery = true)
    List<Event> findByEventTypeAndCategory(@Param("eventType") String eventType,
                                         @Param("category") String category,
                                         @Param("since") Instant since);

    @Query(value = """
            SELECT COUNT(DISTINCT E.USER_ID)
            FROM EVENTS E
            WHERE E.TIMESTAMP >= :since
            AND E.PROPERTIES ->> 'category' = :category
            """, nativeQuery = true)
    long countDistinctUsersByCategorySince(@Param("category") String category,
                                         @Param("since") Instant since);

    @Query(value = """
            SELECT COUNT(*)
            FROM EVENTS E
            WHERE E.EVENT_TYPE = 'order_placed'
            AND E.TIMESTAMP >= :since
            AND E.PROPERTIES ->> 'category' = :category
            """, nativeQuery = true)
    long countOrdersByCategorySince(@Param("category") String category,
                                  @Param("since") Instant since);
}