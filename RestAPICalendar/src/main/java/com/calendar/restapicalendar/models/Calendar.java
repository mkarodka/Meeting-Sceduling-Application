package com.calendar.restapicalendar.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

import org.springframework.stereotype.Component;

@Entity
@Table(name = "calendar")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Component
public class Calendar {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long calendar_id;
	 
	@Column(name = "calendar_name")
	private String name;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private User user;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "calendar")
	@JsonIgnoreProperties("calendar")
	private List<CalendarEvent> events;

	public Calendar() {
		super();
	}

	public Calendar(Long calendar_id, String name, User user, List<CalendarEvent> events) {
		super();
		this.calendar_id = calendar_id;
		this.name = name;
		this.user = user;
		this.events = events;
	}

	public Long getCalendar_id() {
		return calendar_id;
	}

	public void setCalendar_id(Long calendar_id) {
		this.calendar_id = calendar_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<CalendarEvent> getEvents() {
		return events;
	}

	public void setEvents(List<CalendarEvent> events) {
		this.events = events;
	}

	@Override
	public String toString() {
		return "Calendar [calendar_id=" + calendar_id + ", name=" + name + ", user=" + user + ", events=" + events
				+ "]";
	}

}
