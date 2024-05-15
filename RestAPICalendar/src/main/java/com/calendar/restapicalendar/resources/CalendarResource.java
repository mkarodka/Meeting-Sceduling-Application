package com.calendar.restapicalendar.resources;

import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.calendar.restapicalendar.errors.CalendarNotFoundException;
import com.calendar.restapicalendar.errors.InvalidRequestException;
import com.calendar.restapicalendar.models.Calendar;
import com.calendar.restapicalendar.models.CalendarEvent;
import com.calendar.restapicalendar.request.SearchRequest;
import com.calendar.restapicalendar.service.CalendarService;

@RestController
@RequestMapping("/v1")
public class CalendarResource {

	@Autowired
	private CalendarService calendarService;

	@GetMapping("/")
	public String sayHello() {
		return "Welcome to the Calendar REST API!";
	}

	@PostMapping("/calendars/add")
	public ResponseEntity<?> addCalendar(@RequestBody Calendar calendar) {
	    try {
	        // Validate calendar details
	        if (calendar.getUser() == null) {
	            throw new InvalidRequestException("User details must be provided to create a calendar.");
	        }

	        // Set the calendar for each event
	        if (calendar.getEvents() != null) {
	            for (CalendarEvent event : calendar.getEvents()) {
	                event.setCalendar(calendar);
	            }
	        }

	        // Additional validation logic if needed
	        Calendar createdCalendar = calendarService.addCalendar(calendar);
	        if (createdCalendar != null) {
	            return ResponseEntity.status(HttpStatus.CREATED).body(createdCalendar);
	        } else {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create calendar as all events are overlapping.");
	        }
	    } catch (InvalidRequestException e) {
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}
	@GetMapping("/calendars")
	public List<Calendar> retrieveAllCalendars() {
		return calendarService.retrieveAllCalendars();
	}

	@GetMapping("/calendars/{id}")
	public ResponseEntity<?> retrieveCalendar(@PathVariable long id) {
		try {
			Calendar calendar = calendarService.retrieveCalendar(id);
			if (calendar == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Calendar not found for id: " + id);
			} else {
				return ResponseEntity.ok(calendar);
			}
		} catch (CalendarNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@DeleteMapping("/calendars/delete/{id}")
	public ResponseEntity<String> deleteCalendar(@PathVariable Long id) {
		try {
			calendarService.deleteCalendar(id);
			return ResponseEntity.ok("Calendar with id " + id + " deleted successfully.");
		} catch (CalendarNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PutMapping(value = "/calendars/update")
	public ResponseEntity<?> updateCalendar(@RequestBody Calendar calendar) {
		try {
			Calendar updated = calendarService.updateCalendar(calendar);
			if (updated != null) {
				// Fetch the updated calendar with its events
				updated = calendarService.retrieveCalendar(updated.getCalendar_id());
				if (updated != null) {
					return ResponseEntity.ok(updated);
				} else {
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
							.body("Failed to retrieve updated calendar with events.");
				}
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Calendar not found for id: " + calendar.getCalendar_id());
			}
		} catch (InvalidRequestException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping("/calendars/search")
	public ResponseEntity<?> searchEvents(@RequestBody SearchRequest searchRequest) {
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			if (searchRequest != null) {
				String startDateTime = searchRequest.getStartDateTime();
				String endDateTime = searchRequest.getEndDateTime();

				if (startDateTime == null || endDateTime == null) {
					throw new InvalidRequestException("Both startDateTime and endDateTime must be provided.");
				}

				LocalDateTime start = LocalDateTime.parse(startDateTime, formatter);
				LocalDateTime end = LocalDateTime.parse(endDateTime, formatter);

				List<CalendarEvent> calendarEvents = calendarService.findByEventDateTimeBetween(start, end);
				if (calendarEvents.isEmpty()) {
					return ResponseEntity.status(HttpStatus.NOT_FOUND)
							.body("No events found between the specified date range.");
				} else {
					return ResponseEntity.ok(calendarEvents);
				}
			} else {
				throw new InvalidRequestException("Search request body must not be null.");
			}
		} catch (DateTimeParseException e) {
			return ResponseEntity.badRequest()
					.body("Invalid date format provided. Please provide dates in yyyy-MM-dd HH:mm:ss format.");
		} catch (InvalidRequestException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}