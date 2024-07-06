package no.java.mooseheadreborn

import no.java.mooseheadreborn.dto.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController {
    @GetMapping("/config")
    fun hello(): ConfigDto {
        return ConfigDto()
    }
}