package no.java.mooseheadreborn.service

import io.mockk.mockk
import io.mockk.every
import no.java.mooseheadreborn.domain.*
import no.java.mooseheadreborn.jooq.public_.tables.records.WorkshopRecord
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.Test
import java.time.*
import java.util.UUID

class WorkshopServiceTest {
    private val workshopRepository:WorkshopRepository = mockk(relaxed = false)
    private val timeService:MyTimeService = mockk(relaxed = false)

    private val workshopService: WorkshopService = WorkshopService(
        workshopRepository = workshopRepository,
        timeService = timeService,
        adminService = mockk(relaxed = false)
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
            null
        )
        every { workshopRepository.allWorkshops()}.returns(listOf(wr))
        every { timeService.currentTime() }.returns(Instant.now())

        val result = workshopService.allWorkshops()

        assertThat(result).hasSize(1)
        assertThat(result[0].workshopstatus).isEqualTo(WorkshopStatus.OPEN)

    }
}