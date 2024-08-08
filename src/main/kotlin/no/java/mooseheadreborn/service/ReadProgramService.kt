package no.java.mooseheadreborn.service

import com.fasterxml.jackson.module.kotlin.*
import no.java.mooseheadreborn.*
import no.java.mooseheadreborn.dto.moresleep.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.*
import org.springframework.web.client.RestTemplate

@Service
class ReadProgramService(@Autowired private val restTemplate: RestTemplate) {

    fun fetchProgram():MoresleepProgram? {
        val response:String? = restTemplate.getForObject(ConfigVariable.MORESLEEP_PROGRAM_LOCATION.readValue(),String::class.java)
        val mapper = jacksonObjectMapper()
        return response?.let { mapper.readValue(it) }
    }
}