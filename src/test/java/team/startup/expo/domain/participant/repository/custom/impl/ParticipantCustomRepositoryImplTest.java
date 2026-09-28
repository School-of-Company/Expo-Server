package team.startup.expo.domain.participant.repository.custom.impl;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import team.startup.expo.domain.expo.entity.Expo;
import team.startup.expo.domain.participant.entity.StandardParticipant;
import team.startup.expo.domain.participant.entity.StandardParticipantParticipation;
import team.startup.expo.domain.participant.presentation.dto.response.GetParticipantInfoResponseDto;
import team.startup.expo.domain.participant.presentation.dto.response.ParticipantResponseDto;
import team.startup.expo.domain.trainee.entity.ApplicationType;
import team.startup.expo.global.querydsl.QueryDslConfig;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.sql.init.mode=never"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QueryDslConfig.class, ParticipantCustomRepositoryImpl.class})
@Testcontainers(disabledWithoutDocker = true)
class ParticipantCustomRepositoryImplTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    private static final LocalDate DAY1 = LocalDate.of(2026, 9, 10);
    private static final LocalDate DAY2 = LocalDate.of(2026, 9, 11);

    @Autowired
    private EntityManager em;

    @Autowired
    private ParticipantCustomRepositoryImpl repository;

    private Expo expoA;
    private Expo expoB;
    private final List<Long> expoADay1Ids = new ArrayList<>();
    private final List<Long> expoADay2Ids = new ArrayList<>();

    @BeforeEach
    void setUp() {
        expoA = persistExpo("expo-a");
        expoB = persistExpo("expo-b");

        List<StandardParticipant> day1Participants = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            day1Participants.add(persistParticipant(expoA, "a" + i));
        }
        StandardParticipant bothDays = persistParticipant(expoA, "both");
        StandardParticipant day2Only = persistParticipant(expoA, "day2");
        StandardParticipant otherExpo = persistParticipant(expoB, "b");

        persistParticipation(bothDays, expoA, DAY2);
        persistParticipation(day2Only, expoA, DAY2);
        persistParticipation(otherExpo, expoB, DAY1);
        persistParticipation(bothDays, expoA, DAY1);
        for (int i = day1Participants.size() - 1; i >= 0; i--) {
            persistParticipation(day1Participants.get(i), expoA, DAY1);
        }

        day1Participants.forEach(p -> expoADay1Ids.add(p.getId()));
        expoADay1Ids.add(bothDays.getId());
        expoADay2Ids.add(bothDays.getId());
        expoADay2Ids.add(day2Only.getId());

        em.flush();
        em.createNativeQuery("update tb_standard_participant set sms_try_time = 1 where id in (:ids)")
                .setParameter("ids", expoADay1Ids.subList(0, 2))
                .executeUpdate();
        em.clear();
    }

    @Test
    void pagesAreOrderedByIdWithoutOverlapAndMatchCount() {
        ParticipantResponseDto first = repository.searchParticipants(expoA.getId(), PageRequest.of(0, 4), DAY1);
        ParticipantResponseDto second = repository.searchParticipants(expoA.getId(), PageRequest.of(1, 4), DAY1);

        assertThat(first.getInfo().getTotalElement()).isEqualTo(6);
        assertThat(first.getInfo().getTotalPage()).isEqualTo(2);
        assertThat(second.getInfo().getTotalElement()).isEqualTo(6);
        assertThat(first.getParticipants()).hasSize(4);
        assertThat(second.getParticipants()).hasSize(2);

        List<Long> all = new ArrayList<>(ids(first));
        all.addAll(ids(second));
        assertThat(all).containsExactlyElementsOf(expoADay1Ids.stream().sorted().toList());
    }

    @Test
    void sameRequestReturnsSameRows() {
        List<Long> firstCall = ids(repository.searchParticipants(expoA.getId(), PageRequest.of(1, 2), DAY1));
        List<Long> secondCall = ids(repository.searchParticipants(expoA.getId(), PageRequest.of(1, 2), DAY1));

        assertThat(firstCall).hasSize(2).isEqualTo(secondCall);
    }

    @Test
    void pageBeyondLastReturnsEmptyWithTotalCount() {
        ParticipantResponseDto response = repository.searchParticipants(expoA.getId(), PageRequest.of(5, 4), DAY1);

        assertThat(response.getParticipants()).isEmpty();
        assertThat(response.getInfo().getTotalElement()).isEqualTo(6);
        assertThat(response.getInfo().getTotalPage()).isEqualTo(2);
    }

    @Test
    void filtersByAttendanceDate() {
        ParticipantResponseDto response = repository.searchParticipants(expoA.getId(), PageRequest.of(0, 80), DAY2);

        assertThat(response.getInfo().getTotalElement()).isEqualTo(2);
        assertThat(ids(response)).containsExactlyElementsOf(expoADay2Ids.stream().sorted().toList());
    }

    @Test
    void filtersByExpo() {
        ParticipantResponseDto response = repository.searchParticipants(expoB.getId(), PageRequest.of(0, 80), DAY1);

        assertThat(response.getInfo().getTotalElement()).isEqualTo(1);
        assertThat(response.getParticipants()).extracting(GetParticipantInfoResponseDto::getName).containsExactly("b");
    }

    @Test
    void mapsProjectionFields() {
        GetParticipantInfoResponseDto dto = repository.searchParticipants(expoA.getId(), PageRequest.of(0, 1), DAY1)
                .getParticipants().get(0);

        assertThat(dto.getId()).isEqualTo(expoADay1Ids.get(0));
        assertThat(dto.getName()).isEqualTo("a0");
        assertThat(dto.getPhoneNumber()).isEqualTo("010a0");
        assertThat(dto.getInformationStatus()).isTrue();
    }

    @Test
    void pagingIndexesAreDeclared() {
        List<?> indexNames = em.createNativeQuery(
                        "select indexname from pg_indexes where indexname in " +
                                "('idx_standard_participant_expo_id_id', 'idx_sp_participation_date_participant_id')")
                .getResultList();

        assertThat(indexNames).hasSize(2);
    }

    private List<Long> ids(ParticipantResponseDto response) {
        return response.getParticipants().stream().map(GetParticipantInfoResponseDto::getId).toList();
    }

    private Expo persistExpo(String id) {
        Expo expo = Expo.builder()
                .id(id)
                .title(id)
                .description(id)
                .startedDay("2026-09-01")
                .finishedDay("2026-10-31")
                .location("Seoul")
                .x("127.0")
                .y("37.5")
                .applicationPerson(0L)
                .yesterdayApplicationPerson(0L)
                .build();
        em.persist(expo);
        return expo;
    }

    private StandardParticipant persistParticipant(Expo expo, String name) {
        StandardParticipant participant = StandardParticipant.builder()
                .name(name)
                .phoneNumber("010" + name)
                .informationJson("{}")
                .personalInformationStatus(true)
                .applicationType(ApplicationType.PRE)
                .expo(expo)
                .smsTryTime(0)
                .applicationDate(LocalDateTime.of(2026, 9, 1, 0, 0))
                .build();
        em.persist(participant);
        return participant;
    }

    private void persistParticipation(StandardParticipant participant, Expo expo, LocalDate date) {
        em.persist(StandardParticipantParticipation.builder()
                .entryTime(date.atTime(9, 0))
                .attendanceDate(date)
                .standardParticipant(participant)
                .expo(expo)
                .build());
    }
}
