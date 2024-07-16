package no.java.mooseheadreborn

import no.java.mooseheadreborn.dto.*
import no.java.mooseheadreborn.dto.enduser.*
import no.java.mooseheadreborn.service.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = arrayOf("http://localhost:3000"))
@RestController
class RestController(
    private val workshopService: WorkshopService,
    private val participantService: ParticipantService,
    private val registrationService: RegistrationService,
) {
    @GetMapping("/api/config")
    fun hello(): ConfigDto {
        return ConfigDto()
    }

    @GetMapping("/api/workshopList")
    fun allWorkshops() = workshopService.allWorkshops()

    @GetMapping("/api/workshop/{workshopId}")
    fun oneWorkshop(@PathVariable workshopId: String?): ResponseEntity<WorkshopDto> {
        if (workshopId == null) {
            throw BadRequestException("WorkshopId cannot be null")
        }
        val workshopDto = workshopService.workshopById(workshopId)
            ?: throw BadRequestException("Unknown workshop $workshopId")
        return ResponseEntity.ok(workshopDto)
    }


    @PostMapping("/api/addWorkshop")
    fun addWorkshop(@RequestBody addWorkshopDto: AddWorkshopDto):ResponseEntity<ResultWithId> {
        return workshopService.addWorkshop(addWorkshopDto).fold(
            left = { resultWithId -> ResponseEntity.ok(resultWithId) },
            right = { errormessage -> throw BadRequestException(errormessage)}
        )
    }

    @PostMapping("/api/registerParticipant")
    fun addRegistration(@RequestBody registerParticipantDto: RegisterParticipantDto):ResponseEntity<NoDataDto> {
        return participantService.registerParticipant(registerParticipantDto.name,registerParticipantDto.email).fold(
            left = { noData -> ResponseEntity.ok(noData) },
            right = { errormessage -> throw BadRequestException(errormessage)}
        )
    }

    @PostMapping("/api/activateParticipant")
    fun activateParticipant(@RequestBody activateParticipantDto: ActivateParticipantDto):ResponseEntity<NoDataDto> {
        return participantService.activateParticipant(activateParticipantDto.accessKey).fold(
            left = { noData -> ResponseEntity.ok(noData) },
            right = { errormessage -> throw BadRequestException(errormessage)}
        )

    }

    @PostMapping("/api/addRegistration")
    fun addRegistration(@RequestBody addRegistrationDto: AddRegistrationDto):ResponseEntity<AddRegistrationResultDto> {
        return registrationService.addRegistration(
            accessToken = addRegistrationDto.accessToken,
            workshopId = addRegistrationDto.workshopId,
            numParticipants = addRegistrationDto.numParticipants?:1
        ).fold(
            left = { registrationStatus -> ResponseEntity.ok(AddRegistrationResultDto(registrationStatus.name)) },
            right = { errormessage -> throw BadRequestException(errormessage)}
        )
    }

    @PostMapping("/api/cancelRegistration")
    fun cancelRegistration(@RequestBody cancelRegistrationDto: CancelRegistrationDto):ResponseEntity<NoDataDto> {
        return registrationService.cancelRegistration(cancelRegistrationDto.registrationId).fold(
            left = { noData -> ResponseEntity.ok(noData) },
            right = { errormessage -> throw BadRequestException(errormessage)}
        )
    }

    @PostMapping("/api/readParticipantDto")
    fun readParticipantDto(@RequestBody accesssTokenDto: AccesssTokenDto):ResponseEntity<ParticipantDto> {
        return registrationService.readParticipantDto(accesssTokenDto.accessToken).fold(
            left = { participantDto -> ResponseEntity.ok(participantDto) },
            right = { errormessage -> throw BadRequestException(errormessage)}
        )
    }




    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<String> =
        ResponseEntity(ex.errormessage, HttpStatus.BAD_REQUEST)


}