package no.java.mooseheadreborn.service

import io.mockk.*
import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.dto.admin.*
import no.java.mooseheadreborn.dto.moresleep.*
import no.java.mooseheadreborn.jooq.public_.tables.records.WorkshopRecord
import no.java.mooseheadreborn.repository.*
import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.time.*
import java.util.UUID

class WorkshopServiceTest {
    private val workshopRepository: WorkshopRepository = mockk(relaxed = false)
    private val timeService:MyTimeService = mockk(relaxed = false)
    private val readProgramService:ReadProgramService = mockk(relaxed = false)
    private val adminService:AdminService = mockk(relaxed = false)

    private val workshopService: WorkshopService = WorkshopService(
        workshopRepository = workshopRepository,
        timeService = timeService,
        adminService = adminService,
        readProgramService = readProgramService,
    )

    @Test
    fun shouldGiveOpenWorkshop() {
        val wr = WorkshopRecord(
            UUID.randomUUID().toString(),
            "Mockname",
            WorkshopType.KIDS.name,
            10,
            3,
            OffsetDateTime.now().minusDays(1),
            null,
            null,
            null,
        )
        every { workshopRepository.allWorkshops()}.returns(listOf(wr))
        every { timeService.currentTime() }.returns(Instant.now())

        val result = workshopService.allWorkshops()

        assertThat(result).hasSize(1)
        assertThat(result[0].workshopstatus).isEqualTo(WorkshopStatus.OPEN)

    }

    @Test
    fun shouldCreateWorkshopFromMoresleep() {
        val moresleepProgram = MoresleepProgram(
            sessions = listOf(
                MoresleepSession(
                    title = "Moresleep workshop",
                    format = "workshop",
                    startTime = "2024-09-03T09:00",
                    endTime = "2024-09-03T13:00"
                )
            )
        )
        val expectedTitle = "moresleepworksh"
        every { readProgramService.fetchProgram() }.returns(moresleepProgram)
        every { workshopRepository.addWorkshop(any()) } just Runs
        every { workshopRepository.workshopFromId(expectedTitle) }.returns(null)

        every { adminService.keyIsValid("myAccessToken") }.returns(true)

        workshopService.createWorkshopsFromMoosehead(MoresleepCreateWorkshopsDto(
            accessToken = "myAccessToken",
            opensAt = "202408181200",
            capacity = 30
        ))

        val captureRecord = slot<WorkshopRecord>()
        verify { workshopRepository.addWorkshop(capture(captureRecord)) }

        val workshopRecord:WorkshopRecord = captureRecord.captured

        assertThat(workshopRecord.id).isEqualTo(expectedTitle)
        assertThat(workshopRecord.name).isEqualTo("Moresleep workshop")
        assertThat(workshopRecord.workshopType).isEqualTo(WorkshopType.JZ.name)
        assertThat(workshopRecord.capacity).isEqualTo(30)
        assertThat(workshopRecord.registerLimit).isEqualTo(1)
        assertThat(workshopRecord.registrationOpen).isEqualTo(LocalDateTime.of(2024,8,18,12,0).atZone(ZoneId.of("Europe/Oslo")).toOffsetDateTime())
        assertThat(workshopRecord.starttime).isEqualTo(LocalDateTime.of(2024,9,3,9,0).atZone(ZoneId.of("Europe/Oslo")).toOffsetDateTime())

    }
}