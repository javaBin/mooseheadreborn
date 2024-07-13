package no.java.mooseheadreborn

import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.service.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController(
    private val workshopService: WorkshopService,
    private val participantService: ParticipantService,
) {
    @GetMapping("/config")
    fun hello(): ConfigDto {
        return ConfigDto()
    }

    @GetMapping("/api/workshopList")
    fun allWorkshops() = workshopService.allWorkshops()


    @PostMapping("/api/addWorkshop")
    fun addWorkshop(@RequestBody addWorkshopDto: AddWorkshopDto):ResponseEntity<Any> {
        return workshopService.addWorkshop(addWorkshopDto).fold(
            left = { resultWithId -> ResponseEntity.ok(resultWithId) },
            right = { errormessage -> ResponseEntity(errormessage, HttpStatus.BAD_REQUEST)}
        )
    }

    @PostMapping("/api/registerParticipant")
    fun registerParticipant(@RequestBody registerParticipantDto: RegisterParticipantDto):ResponseEntity<Any> {
        return participantService.registerParticipant(registerParticipantDto.name,registerParticipantDto.email).fold(
            left = { noData -> ResponseEntity.ok(noData) },
            right = { errormessage -> ResponseEntity(errormessage, HttpStatus.BAD_REQUEST)}
        )
    }

    @PostMapping("/api/activateParticipant")
    fun activateParticipant(@RequestBody activateParticipantDto: ActivateParticipantDto):ResponseEntity<Any> {
        return participantService.activateParticipant(activateParticipantDto.accessKey).fold(
            left = { noData -> ResponseEntity.ok(noData) },
            right = { errormessage -> ResponseEntity(errormessage, HttpStatus.BAD_REQUEST)}
        )

    }




}