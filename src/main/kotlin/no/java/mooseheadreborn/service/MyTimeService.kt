package no.java.mooseheadreborn.service

import org.springframework.stereotype.Service
import java.time.Instant

interface MyTimeService {
    fun currentTime():Instant
}

@Service
class MyTimeServiceImpl:MyTimeService {
    override fun currentTime(): Instant {
        return Instant.now()
    }

}