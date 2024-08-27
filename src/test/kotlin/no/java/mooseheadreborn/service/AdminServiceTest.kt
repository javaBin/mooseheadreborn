package no.java.mooseheadreborn.service

import io.mockk.*
import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.dto.admin.*
import no.java.mooseheadreborn.jooq.public_.tables.records.*
import no.java.mooseheadreborn.repository.*
import no.java.mooseheadreborn.util.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.*
import java.util.UUID

class AdminServiceTest {
    private val adminRepository:AdminRepository = mockk(relaxed = false)
    private val workshopRepositoryMock = WorkshopRepositoryMock()
    private val registrationRepositoryMock = RegistrationRepositoryMock()
    private val participantRepositoryMock = ParticipantRepositoryMock()
    private val sendMailService: SendMailService = mockk(relaxed = true)

    private val registrationService = RegistrationService(
        workshopRepository = workshopRepositoryMock,
        participantRepository = participantRepositoryMock,
        registrationRepository = registrationRepositoryMock,
        timeService = MyTimeServiceImpl(),
        sendMailService = sendMailService,
    )

    private val adminService:AdminService = AdminService(
        adminRepository=adminRepository,
        workshopRepository = workshopRepositoryMock,
        registrationRepository = registrationRepositoryMock,
        participantRepository = participantRepositoryMock,
        registrationService = registrationService,
        reportRepository = mockk(relaxed = false)
    )


    private val partOne = ParticiantRecord(
        UUID.randomUUID().toString(),
        "one@a.com",
        "Part One",
        UUID.randomUUID().toString(),
        OffsetDateTime.now().minusDays(1)
    )
    private val partTwo = ParticiantRecord(
        UUID.randomUUID().toString(),
        "two@a.com",
        "Part Two",
        UUID.randomUUID().toString(),
        OffsetDateTime.now().minusDays(1)
    )

    private val partThree = ParticiantRecord(
        UUID.randomUUID().toString(),
        "three@a.com",
        "Part Three",
        UUID.randomUUID().toString(),
        OffsetDateTime.now().minusDays(1)
    )
    private val workshopRecord = WorkshopRecord(
        UUID.randomUUID().toString(),
        "Workshop one",
        WorkshopType.JZ.name,
        2,
        1,
        OffsetDateTime.now().minusDays(1),
        null,
        null,
        null
    )
    private val adminAccessToken = UUID.randomUUID().toString()

    @BeforeEach
    fun setup() {
        participantRepositoryMock.addParticipant(partOne)
        participantRepositoryMock.addParticipant(partTwo)
        participantRepositoryMock.addParticipant(partThree)

        workshopRepositoryMock.addWorkshop(workshopRecord)

        registrationService.addRegistration(partOne.accessKey,workshopRecord.id,1)
        registrationService.addRegistration(partTwo.accessKey,workshopRecord.id,1)

        every { adminRepository.readKey(adminAccessToken) }.returns(
            AdminKeysRecord(
                adminAccessToken, OffsetDateTime.now().minusSeconds(10)
            )
        )
    }

    @Test
    fun shouldGiveWaitingSpaceToParticipant() {
        val addToWaiting: AddRegistrationResultDto = registrationService.addRegistration(partThree.accessKey,workshopRecord.id,1).leftOrError()
        assertThat(addToWaiting.registrationStatus).isEqualTo(RegistrationStatus.WAITING)

        clearMocks(sendMailService)

        val adminWorkshopDto:AdminWorkshopDto = adminService.changeCapacity(ChangeCapacityDto(
            accessToken = adminAccessToken,
            workshopId = workshopRecord.id,
            capacity = 3
        )).leftOrError()

        assertThat(adminWorkshopDto.capacity).isEqualTo(3)
        val participants = adminWorkshopDto.registrationList
        assertThat(participants).hasSize(3)
        assertThat(participants.map { it.status }.toSet()).isEqualTo(setOf(RegistrationStatus.REGISTERED))

        verify { sendMailService.sendEmail(partThree.email,EmailTemplate.REGISTER_CONFIRMATION,any()) }
    }

    @Test
    fun shouldNotBeAllowedToReduce() {
        val errormessage:String? = adminService.changeCapacity(ChangeCapacityDto(
            accessToken = adminAccessToken,
            workshopId = workshopRecord.id,
            capacity = 1
        )).rightOrNull()

        assertThat(errormessage).isEqualTo("There are 2 participants. Can not reduce capacity to 1")
    }

}