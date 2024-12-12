package tn.esprit.eventsproject.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.repositories.EventRepository;
import tn.esprit.eventsproject.repositories.LogisticsRepository;
import tn.esprit.eventsproject.repositories.ParticipantRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServicesImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private LogisticsRepository logisticsRepository;

    @InjectMocks
    private EventServicesImpl eventServices;

    private Participant participant;
    private Event event;
    private Logistics logistics;

    @BeforeEach
    void setUp() {
        // Initialize test data
        participant = new Participant();
        participant.setIdPart(1);
        participant.setNom("Tounsi");
        participant.setPrenom("Ahmed");
        participant.setTache(Tache.ORGANISATEUR);

        event = new Event();
        event.setDescription("Test Event");
        event.setDateDebut(LocalDate.now());
        event.setDateFin(LocalDate.now().plusDays(1));

        logistics = new Logistics();
        logistics.setDescription("Test Logistics");
        logistics.setReserve(true);
        logistics.setPrixUnit(10f);
        logistics.setQuantite(5);
    }

    @Test
    void testAddParticipant() {
        when(participantRepository.save(participant)).thenReturn(participant);

        Participant savedParticipant = eventServices.addParticipant(participant);

        assertNotNull(savedParticipant);
        assertEquals(participant, savedParticipant);
        verify(participantRepository).save(participant);
    }

    @Test
    void testAddAffectEvenParticipant() {
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(event)).thenReturn(event);

        event.setParticipants(new HashSet<>(Set.of(participant)));
        Event savedEvent = eventServices.addAffectEvenParticipant(event);

        assertNotNull(savedEvent);
        assertTrue(savedEvent.getParticipants().contains(participant));
        verify(eventRepository).save(event);
    }

    @Test
    void testAddAffectLog() {
        when(eventRepository.findByDescription("Test Event")).thenReturn(event);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        Logistics savedLogistics = eventServices.addAffectLog(logistics, "Test Event");

        assertNotNull(savedLogistics);
        assertTrue(event.getLogistics().contains(logistics));
        verify(logisticsRepository).save(logistics);
    }

    @Test
    void testGetLogisticsDates() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);

        event.setLogistics(new HashSet<>(Set.of(logistics)));
        when(eventRepository.findByDateDebutBetween(startDate, endDate)).thenReturn(List.of(event));

        List<Logistics> logisticsList = eventServices.getLogisticsDates(startDate, endDate);

        assertNotNull(logisticsList);
        assertFalse(logisticsList.isEmpty());
        assertTrue(logisticsList.contains(logistics));
    }

    @Test
    void testCalculCout() {
        event.setLogistics(new HashSet<>(Set.of(logistics)));
        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache("Tounsi", "Ahmed", Tache.ORGANISATEUR))
            .thenReturn(List.of(event));

        eventServices.calculCout();

        // Verify that the event's cost is calculated and saved
        assertEquals(50f, event.getCout());
        verify(eventRepository).save(event);
    }
}
