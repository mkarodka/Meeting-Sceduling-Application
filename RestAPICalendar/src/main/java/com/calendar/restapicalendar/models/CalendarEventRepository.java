package com.calendar.restapicalendar.models;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long>{
    @Transactional
    public List<CalendarEvent> findByEventDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
