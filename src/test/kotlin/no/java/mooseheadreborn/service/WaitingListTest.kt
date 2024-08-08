package no.java.mooseheadreborn.service

import io.mockk.*
import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.jooq.public_.tables.records.*
import no.java.mooseheadreborn.repository.*
import no.java.mooseheadreborn.util.*
import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.time.*
import java.util.UUID

class WaitingListTest {
    private val workshopRepositoryMock = WorkshopRepositoryMock()
    private val participantRepositoryMock = ParticipantRepositoryMock()
    private val registrationRepositoryMock = RegistrationRepositoryMock()

    private val sendMailService:SendMailService = mockk(relaxed = true)

    private val registrationService = RegistrationService(
        workshopRepository = workshopRepositoryMock,
        participantRepository = participantRepositoryMock,
        registrationRepository = registrationRepositoryMock,
        timeService = MyTimeServiceImpl(),
        sendMailService = sendMailService

    )

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

        val participantOne = ParticiantRecord(
            UUID.randomUUID().toString(),
            "one@a.com",
            "partone",
            UUID.randomUUID().toString(),
            OffsetDateTime.now().minusDays(1)
        )
        val participantTwo = ParticiantRecord(
            UUID.randomUUID().toString(),
            "two@a.com",
            "parttwo",
            UUID.randomUUID().toString(),
            OffsetDateTime.now().minusDays(1)
        )

        participantRepositoryMock.addParticipant(participantOne)
        participantRepositoryMock.addParticipant(participantTwo)

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
}