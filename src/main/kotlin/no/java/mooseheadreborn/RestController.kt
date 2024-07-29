package no.java.mooseheadreborn

import no.java.mooseheadreborn.domain.*
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

    @GetMapping("/api/registration/{registrationId}")
    fun registrationInfo(@PathVariable registrationId:String?):ResponseEntity<UserWorkshopRegistrationDto> {
        if (registrationId == null) {
            throw BadRequestException("RegistrationId cannot be null")
        }
        return registrationService.participantInfoFromRegistrationId(registrationId).fold(
            left = { userWorkshopRegistrationDto: UserWorkshopRegistrationDto ->
                ResponseEntity.ok(
                    userWorkshopRegistrationDto
                )
            },
            right = { errormessage: String -> throw BadRequestException(errormessage) }
        )
    }


    @PostMapping("/api/readWorkshop")
    fun workshopWithUser(@RequestBody readWorkshopInfoInputDto:ReadWorkshopInfoInputDto):ResponseEntity<UserWorkshopRegistrationDto> {
        return registrationService.participantInfoForWorkshop(readWorkshopInfoInputDto.workshopId,readWorkshopInfoInputDto.accessToken).fold(
            left = { userWorkshopRegistrationDto -> ResponseEntity.ok(userWorkshopRegistrationDto) },
            right = { errormessage -> throw BadRequestException(errormessage)}
        )
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
    fun activateParticipant(@RequestBody activateParticipantDto: ActivateParticipantDto):ResponseEntity<UserDto> {
        return participantService.activateParticipant(activateParticipantDto.registerKey).fold(
            left = { participantActivationDto -> ResponseEntity.ok(participantActivationDto) },
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
            left = { addRegistrationResultDto -> ResponseEntity.ok(addRegistrationResultDto) },
            right = { errormessage -> throw BadRequestException(errormessage)}
        )
    }

    @PostMapping("/api/cancelRegistration")
    fun cancelRegistration(@RequestBody cancelRegistrationDto: CancelRegistrationDto):ResponseEntity<CancelRegistrationResultDto> {
        return registrationService.cancelRegistration(cancelRegistrationDto.registrationId,cancelRegistrationDto.accessToken).fold(
            left = { cancelRegistrationResultDto -> ResponseEntity.ok(cancelRegistrationResultDto) },
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

    @PostMapping("/api/user")
    fun readUser(@RequestBody accesssTokenDto: AccesssTokenDto):ResponseEntity<UserDto> {
        val userDto:UserDto = participantService.userFromAccessToken(accesssTokenDto.accessToken)
        return ResponseEntity.ok(userDto)
    }




    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<String> =
        ResponseEntity(ex.errormessage, HttpStatus.BAD_REQUEST)


}