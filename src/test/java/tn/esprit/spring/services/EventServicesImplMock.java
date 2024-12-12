package tn.esprit.spring.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;
import tn.esprit.eventsproject.services.EventServicesImpl;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServicesImplMock {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    @InjectMocks
    private EventServicesImpl eventServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddParticipant() {
        // Arrange
        Participant participant = new Participant();
        participant.setNom("Tounsi");
        participant.setPrenom("Ahmed");

        when(participantRepository.save(participant)).thenReturn(participant);

        // Act
        Participant savedParticipant = eventServices.addParticipant(participant);

        // Assert
        assertNotNull(savedParticipant);
        assertEquals("Tounsi", savedParticipant.getNom());
        verify(participantRepository, times(1)).save(participant);
    }

    @Test
    void testAddAffectEvenParticipant() {
        // Arrange
        Event event = new Event();
        event.setDescription("Test Event");

        Participant participant = new Participant();
        participant.setIdPart(1);
        participant.setNom("Tounsi");
        participant.setPrenom("Ahmed");

        Set<Participant> participants = new HashSet<>();
        participants.add(participant);
        event.setParticipants(participants);

        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        // Act
        Event savedEvent = eventServices.addAffectEvenParticipant(event);

        // Assert
        assertNotNull(savedEvent);
        assertTrue(participant.getEvents().contains(event));
        verify(participantRepository, times(1)).findById(1);
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testAddAffectLog() {
        // Arrange
        Event event = new Event();
        event.setDescription("Test Event");

        Logistics logistics = new Logistics();
        logistics.setDescription("Test Logistics");
        logistics.setReserve(true);

        when(eventRepository.findByDescription("Test Event")).thenReturn(event);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        // Act
        Logistics savedLogistics = eventServices.addAffectLog(logistics, "Test Event");

        // Assert
        assertNotNull(savedLogistics);
        assertTrue(event.getLogistics().contains(logistics));
        verify(eventRepository, times(1)).findByDescription("Test Event");
        verify(logisticsRepository, times(1)).save(logistics);
    }

    @Test
    void testGetLogisticsDates() {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        Event event = new Event();
        event.setDateDebut(startDate);
        event.setDateFin(endDate);

        Logistics logistics = new Logistics();
        logistics.setReserve(true);

        Set<Logistics> logisticsSet = new HashSet<>();
        logisticsSet.add(logistics);
        event.setLogistics(logisticsSet);

        when(eventRepository.findByDateDebutBetween(startDate, endDate))
                .thenReturn(List.of(event));

        // Act
        List<Logistics> result = eventServices.getLogisticsDates(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains(logistics));
        verify(eventRepository, times(1)).findByDateDebutBetween(startDate, endDate);
    }
}
