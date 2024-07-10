package no.java.mooseheadreborn

import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.service.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController(
    private val workshopService: WorkshopService
) {
    @GetMapping("/config")
    fun hello(): ConfigDto {
        return ConfigDto()
    }

    @GetMapping("/api/workshopList")
    fun allWorkshops() = workshopService.allWorkshops()


    @PostMapping("/api/addWorkshop")
    fun addWorkshop(@RequestBody addWorkshopDto: AddWorkshopDto):ResponseEntity<String> {
        return workshopService.addWorkshop(addWorkshopDto).fold(
            left = { id -> ResponseEntity.ok(id) },
            right = { errormessage -> ResponseEntity(errormessage, HttpStatus.BAD_REQUEST)}
        )
    }

}