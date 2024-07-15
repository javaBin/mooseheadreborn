package no.java.mooseheadreborn.service

import io.mockk.*
import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.jooq.public_.tables.records.*
import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.time.*
import java.util.UUID

class RegistrationServiceTest {
    private val workshopRepository:WorkshopRepository = mockk(relaxed = false)
    private val participantRepository:ParticipantRepository = mockk(relaxed = false)
    private val registrationRepository:RegistrationRepository = mockk(relaxed = true)


    private val registrationService: RegistrationService = RegistrationService(
        workshopRepository = workshopRepository,
        participantRepository = participantRepository,
        registrationRepository = registrationRepository,
        timeService = MyTimeServiceImpl()
    )

    @Test
    fun shouldRegisterOkWithEmpty() {
        val workshopId = UUID.randomUUID().toString()
        val accessKey = UUID.randomUUID().toString()
        val participantId = UUID.randomUUID().toString()


        every { workshopRepository.workshopFromId(workshopId) }.returns(WorkshopRecord(
            workshopId,
            "Dummy workshop",
            WorkshopType.JZ.name,
            30,
            1,
            OffsetDateTime.now().minusDays(1),
            null
        ))

        every { registrationRepository.registrationListForWorkshop(workshopId) }.returns(emptyList())

        every { participantRepository.participantByAccessKey(accessKey) }.returns(
            ParticiantRecord(
                participantId,
                "a@a.com",
                "Anders",
                accessKey,
                OffsetDateTime.now().minusDays(2)
            )
        )

        assertThat(registrationService.addRegistration(accessKey,workshopId,1).leftOrNull()).isEqualTo(RegistrationStatus.REGISTERED)

        val captor = slot<RegistrationRecord>()
        verify { registrationRepository.addRegistration(capture(captor)) }

        val registrationRecord = captor.captured

        assertThat(registrationRecord.workshop).isEqualTo(workshopId)
        assertThat(registrationRecord.participant).isEqualTo(participantId)
        assertThat(registrationRecord.participantCount).isEqualTo(1)
        assertThat(registrationRecord.registeredAt).isNotNull()
        assertThat(registrationRecord.cancelledAt).isNull()
    }

    @Test
    fun shouldNotAllowIfNotOpen() {
        val workshopId = UUID.randomUUID().toString()
        val accessKey = UUID.randomUUID().toString()
        val participantId = UUID.randomUUID().toString()


        every { workshopRepository.workshopFromId(workshopId) }.returns(WorkshopRecord(
            workshopId,
            "Dummy workshop",
            WorkshopType.JZ.name,
            30,
            1,
            OffsetDateTime.now().plusDays(1),
            null
        ))

        every { registrationRepository.registrationListForWorkshop(workshopId) }.returns(emptyList())

        every { participantRepository.participantByAccessKey(accessKey) }.returns(
            ParticiantRecord(
                participantId,
                "a@a.com",
                "Anders",
                accessKey,
                OffsetDateTime.now().minusDays(2)
            )
        )

        assertThat(registrationService.addRegistration(accessKey,workshopId,1).rightOrNull()).isEqualTo("Workshop is not open for registration")

        verify(exactly = 0) { registrationRepository.addRegistration(any()) }

    }
}