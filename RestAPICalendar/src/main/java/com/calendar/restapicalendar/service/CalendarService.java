package com.calendar.restapicalendar.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.calendar.restapicalendar.errors.CalendarNotFoundException;
import com.calendar.restapicalendar.models.Calendar;
import com.calendar.restapicalendar.models.CalendarEvent;
import com.calendar.restapicalendar.models.CalendarEventRepository;
import com.calendar.restapicalendar.models.CalendarRepository;


@Service
public class CalendarService {

	@Autowired
	private CalendarRepository calendarRepository;
	@Autowired
	private CalendarEventRepository calendarEventRepository;
	

	

	public Calendar retrieveCalendar(long id) {
	    Optional<Calendar> calendarOptional = calendarRepository.findById(id);
	    if (calendarOptional.isPresent()) {
	        return calendarOptional.get();
	    } else {
	       return null;
	    }
	}


	public List<Calendar> retrieveAllCalendars() {
		return calendarRepository.findAll();
	}

	
	public Calendar addCalendar(Calendar calendar) {
	    List<CalendarEvent> existingEvents = new ArrayList<>();

	    // Retrieve all existing events from the database
	    if (calendar.getEvents() != null && !calendar.getEvents().isEmpty()) {
	        for (CalendarEvent event : calendar.getEvents()) {
	            List<CalendarEvent> overlappingEvents = calendarEventRepository.findByEventDateTimeBetween(
	                    event.getEventDateTime(), event.getEventDateTime().plusMinutes(1)); // Assuming minimum event duration is 1 minute
	            boolean isOverlap = overlappingEvents.stream()
	                    .anyMatch(existingEvent -> {
	                        LocalDateTime existingStart = existingEvent.getEventDateTime();
	                        LocalDateTime existingEnd = existingStart.plusMinutes(1); // Assuming minimum event duration is 1 minute
	                        LocalDateTime newStart = event.getEventDateTime();
	                        LocalDateTime newEnd = newStart.plusMinutes(1);

	                        // Check if new event overlaps with existing event
	                        return (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) ||
	                                (existingStart.isBefore(newEnd) && existingEnd.isAfter(newStart));
	                    });

	            // Skip adding the new event if there is an overlap
	            if (!isOverlap) {
	                existingEvents.add(event);
	            }
	        }
	    }

	    // If all events are overlapping, return null
	    if (existingEvents.isEmpty()) {
	        return null;
	    }

	    // Set the non-overlapping events to the provided calendar
	    calendar.setEvents(existingEvents);

	    // Save the updated calendar with new events or the newly created calendar
	    return calendarRepository.save(calendar);
	}



	

	public Calendar updateCalendar(Calendar calendar) {
		Calendar searchedCalendar = retrieveCalendar(calendar.getCalendar_id());
		if (searchedCalendar != null) {
			// Update calendar attributes
			searchedCalendar.setName(calendar.getName());
			searchedCalendar.setUser(calendar.getUser());

			// Update existing events or add new ones if needed
			if (calendar.getEvents() != null) {
				for (CalendarEvent updatedEvent : calendar.getEvents()) {
					boolean found = false;
					for (CalendarEvent existingEvent : searchedCalendar.getEvents()) {
						if (existingEvent.getCalendar_event_id().equals(updatedEvent.getCalendar_event_id())) {
							// Update existing event
							existingEvent.setTitle(updatedEvent.getTitle());
							existingEvent.setEventDateTime(updatedEvent.getEventDateTime());
							existingEvent.setLocation(updatedEvent.getLocation());
							existingEvent.setAttendees(updatedEvent.getAttendees());
							existingEvent.setReminderDateTime(updatedEvent.getReminderDateTime());
							existingEvent.setReminderSent(updatedEvent.isReminderSent());
							found = true;
							break;
						}
					}
					// If the event is not found, add it to the calendar
					if (!found) {
						updatedEvent.setCalendar(searchedCalendar);
						searchedCalendar.getEvents().add(updatedEvent);
					}
				}
			}

			// Save the updated calendar
			return calendarRepository.save(searchedCalendar);
		} else {
			return null;
		}
	}

	public void deleteCalendar(Long calendarId) throws CalendarNotFoundException {
		Optional<Calendar> calendarOptional = calendarRepository.findById(calendarId);
		if (calendarOptional.isPresent()) {
			calendarRepository.delete(calendarOptional.get());
		} else {
			throw new CalendarNotFoundException("Calendar with id " + calendarId + " not found.");
		}
	}

	public List<CalendarEvent> findByEventDateTimeBetween(LocalDateTime start, LocalDateTime end) {
		if (start == null || end == null) {
			// Handle case where start or end is null
			throw new IllegalArgumentException("Both start and end parameters are required.");
		}
		if (start.isAfter(end)) {
			// Handle case where start is after end
			throw new IllegalArgumentException("Start date cannot be after end date.");
		}
		return calendarEventRepository.findByEventDateTimeBetween(start, end);
	}

}
