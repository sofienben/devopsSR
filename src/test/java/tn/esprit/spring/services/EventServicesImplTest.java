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
import java.util.Optional;
import java.util.Set;

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
        participant = new Participant();
        participant.setIdPart(1);
        participant.setNom("Tounsi");
        participant.setPrenom("Ahmed");
        participant.setTache(Tache.ORGANISATEUR);

        event = new Event();
        event.setDescription("Test Event");
        event.setDateDebut(LocalDate.now());
        event.setDateFin(LocalDate.now().plusDays(1));
        event.setParticipants(new HashSet<>());

        logistics = new Logistics();
        logistics.setDescription("Test Logistics");
        logistics.setReserve(true);
        logistics.setPrixUnit(10f);
        logistics.setQuantite(5);
    }

    @Test
    void testAddParticipant() {
        // Simuler l'ajout d'un participant
        when(participantRepository.save(participant)).thenReturn(participant);

        // Appeler la méthode
        eventServices.addParticipant(participant);

        // Vérifier que la méthode `save` du repository a été appelée
        verify(participantRepository).save(participant);
    }

    @Test
    void testAddAffectEvenParticipant() {
        // Simuler la recherche d'un participant par ID
        when(participantRepository.findById(1)).thenReturn(Optional.of(participant));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        // Appeler la méthode
        eventServices.addAffectEvenParticipant(event, 1);

        // Vérifier les interactions
        verify(participantRepository).findById(1);
        verify(eventRepository).save(event);
    }

    @Test
    void testAddAffectLog() {
        // Simuler la recherche d'un événement et l'enregistrement de la logistique
        when(eventRepository.findByDescription("Test Event")).thenReturn(event);
        when(logisticsRepository.save(logistics)).thenReturn(logistics);

        // Appeler la méthode
        eventServices.addAffectLog(logistics, "Test Event");

        // Vérifier les interactions
        verify(eventRepository).findByDescription("Test Event");
        verify(logisticsRepository).save(logistics);
    }

    @Test
    void testGetLogisticsDates() {
        // Définir une plage de dates
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(1);

        // Ajouter de la logistique à l'événement
        event.setLogistics(new HashSet<>(Set.of(logistics)));

        // Simuler la recherche d'événements dans une plage de dates
        when(eventRepository.findByDateDebutBetween(startDate, endDate)).thenReturn(Set.of(event));

        // Appeler la méthode
        eventServices.getLogisticsDates(startDate, endDate);

        // Vérifier les interactions
        verify(eventRepository).findByDateDebutBetween(startDate, endDate);
    }

    @Test
    void testCalculCout() {
        // Ajouter de la logistique à l'événement
        event.setLogistics(new HashSet<>(Set.of(logistics)));

        // Simuler la recherche d'événements avec un participant spécifique
        when(eventRepository.findByParticipants_NomAndParticipants_PrenomAndParticipants_Tache(
                "Tounsi", "Ahmed", Tache.ORGANISATEUR))
            .thenReturn(Set.of(event));

        // Appeler la méthode
        eventServices.calculCout();

        // Vérifier que l'événement est enregistré avec le coût mis à jour
        verify(eventRepository).save(argThat(savedEvent -> 
            savedEvent.getCout() == 50f  // 10 (prixUnit) * 5 (quantite)
        ));
    }
}
