package com.calendar.restapicalendar.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalendarEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long calendar_event_id;
	private String title;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime eventDateTime;

	private String location;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "calendar_event_attendees", joinColumns = @JoinColumn(name = "calendar_event_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<User> attendees;

	private LocalDateTime reminderDateTime;
	private boolean reminderSent;

	@ManyToOne
	@JoinColumn(name = "calendar_id")
	@JsonIgnoreProperties("events")
	private Calendar calendar;

	public CalendarEvent() {
		super();
	}

	public CalendarEvent(Long calendar_event_id, String title, LocalDateTime eventDateTime, String location,
			List<User> attendees, LocalDateTime reminderDateTime, boolean reminderSent, Calendar calendar) {
		super();
		this.calendar_event_id = calendar_event_id;
		this.title = title;
		this.eventDateTime = eventDateTime;
		this.location = location;
		this.attendees = attendees;
		this.reminderDateTime = reminderDateTime;
		this.reminderSent = reminderSent;
		this.calendar = calendar;
	}

	public Long getCalendar_event_id() {
		return calendar_event_id;
	}

	public void setCalendar_event_id(Long calendar_event_id) {
		this.calendar_event_id = calendar_event_id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDateTime getEventDateTime() {
		return eventDateTime;
	}

	public void setEventDateTime(LocalDateTime eventDateTime) {
		this.eventDateTime = eventDateTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<User> getAttendees() {
		return attendees;
	}

	public void setAttendees(List<User> attendees) {
		this.attendees = attendees;
	}

	public LocalDateTime getReminderDateTime() {
		return reminderDateTime;
	}

	public void setReminderDateTime(LocalDateTime reminderDateTime) {
		this.reminderDateTime = reminderDateTime;
	}

	public boolean isReminderSent() {
		return reminderSent;
	}

	public void setReminderSent(boolean reminderSent) {
		this.reminderSent = reminderSent;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	@Override
	public String toString() {
		return "CalendarEvent [calendar_event_id=" + calendar_event_id + ", title=" + title + ", eventDateTime="
				+ eventDateTime + ", location=" + location + ", attendees=" + attendees + ", reminderDateTime="
				+ reminderDateTime + ", reminderSent=" + reminderSent + ", calendar=" + calendar + "]";
	}

}
