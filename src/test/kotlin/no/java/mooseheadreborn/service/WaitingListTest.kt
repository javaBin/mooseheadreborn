package no.java.mooseheadreborn.service

import io.mockk.*
import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.jooq.public_.tables.records.*
import no.java.mooseheadreborn.repository.*
import no.java.mooseheadreborn.util.*
import org.assertj.core.api.Assertions.*
import org.junit.Before
import org.junit.Test
import java.time.*
import java.time.temporal.*
import java.util.UUID

class WaitingListTest {
    private val workshopRepositoryMock = WorkshopRepositoryMock()
    private val participantRepositoryMock = ParticipantRepositoryMock()
    private val registrationRepositoryMock = RegistrationRepositoryMock()

    private val sendMailService:SendMailService = mockk(relaxed = true)

    private val timeServiceMock:MyTimeService = mockk(relaxed = true)

    private val registrationService = RegistrationService(
        workshopRepository = workshopRepositoryMock,
        participantRepository = participantRepositoryMock,
        registrationRepository = registrationRepositoryMock,
        timeService = timeServiceMock,
        sendMailService = sendMailService
    )

    private val participantOne = ParticiantRecord(
        UUID.randomUUID().toString(),
        "one@a.com",
        "partone",
        UUID.randomUUID().toString(),
        OffsetDateTime.now().minusDays(1)
    )
    private val participantTwo = ParticiantRecord(
        UUID.randomUUID().toString(),
        "two@a.com",
        "parttwo",
        UUID.randomUUID().toString(),
        OffsetDateTime.now().minusDays(1)
    )


    @Before
    fun setup() {
        participantRepositoryMock.addParticipant(participantOne)
        participantRepositoryMock.addParticipant(participantTwo)

        val now = Instant.now()
        every { timeServiceMock.currentTime() }.returns(now)
    }


    @Test
    fun shouldSendToCancel() {
        val workshopid = "myWorkshopId"
        workshopRepositoryMock.addWorkshop(WorkshopRecord(
            workshopid,
            "DummyWs",
            WorkshopType.KIDS.name,
            3,
            3,
            OffsetDateTime.now().minusDays(1),
            OffsetDateTime.now().plusDays(10),
            null,
            null,
        ))



        val resultOne: AddRegistrationResultDto = registrationService.addRegistration(participantOne.accessKey,workshopid,1).leftOrError()
        assertThat(resultOne.registrationStatus).isEqualTo(RegistrationStatus.REGISTERED)

        verify { sendMailService.sendEmail("one@a.com",EmailTemplate.REGISTER_CONFIRMATION,any()) }

        clearMocks(sendMailService)

        val resultTwo: AddRegistrationResultDto = registrationService.addRegistration(participantTwo.accessKey,workshopid,3).leftOrError()
        assertThat(resultTwo.registrationStatus).isEqualTo(RegistrationStatus.WAITING)

        verify { sendMailService.sendEmail("two@a.com",EmailTemplate.REGISTER_CONFIRMATION_WAITING,any()) }

        clearMocks(sendMailService)

        val cancelResult:CancelRegistrationResultDto = registrationService.cancelRegistration(resultOne.registrationId,null).leftOrError()
        assertThat(cancelResult.registrationStatus).isEqualTo(RegistrationStatus.NOT_LOGGED_IN)


        verify { sendMailService.sendEmail("two@a.com",EmailTemplate.REGISTER_CONFIRMATION,any()) }

        val allRegistrationList = registrationRepositoryMock.allRegistrations()

        assertThat(allRegistrationList.first { it.id == resultOne.registrationId }.status).isEqualTo(RegistrationStatus.CANCELLED.name)
        assertThat(allRegistrationList.first { it.id == resultTwo.registrationId }.status).isEqualTo(RegistrationStatus.REGISTERED.name)
    }

    @Test
    fun shouldNotUpdateWaitingListWhenClosed() {
        val now = timeServiceMock.currentTime()
        val workshopid = "myWorkshopId"
        workshopRepositoryMock.addWorkshop(WorkshopRecord(
            workshopid,
            "DummyWs",
            WorkshopType.JZ.name,
            1,
            1,
            now.atOffset(ZoneOffset.UTC).minusDays(1),
            now.atOffset(ZoneOffset.UTC).plusDays(1),
            null,
            null,
        ))



        val resultOne = registrationService.addRegistration(participantOne.accessKey,workshopid,1).leftOrError()
        val resultTwo = registrationService.addRegistration(participantTwo.accessKey,workshopid,1).leftOrError()

        clearMocks(sendMailService)

        every { timeServiceMock.currentTime() }.returns(now.plus(2,ChronoUnit.DAYS))

        val cancelResult:CancelRegistrationResultDto = registrationService.cancelRegistration(resultOne.registrationId,null).leftOrError()
        assertThat(cancelResult.registrationStatus).isEqualTo(RegistrationStatus.NOT_LOGGED_IN)

        verify(exactly = 0) { sendMailService.sendEmail(participantTwo.email,any(),any()) }

        assertThat(registrationRepositoryMock.store.first { it.id == resultOne.registrationId }.status).isEqualTo(RegistrationStatus.CANCELLED.name)
        assertThat(registrationRepositoryMock.store.first { it.id == resultTwo.registrationId }.status).isEqualTo(RegistrationStatus.WAITING.name)


    }
}