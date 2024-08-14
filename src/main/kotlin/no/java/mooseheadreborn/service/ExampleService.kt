package no.java.mooseheadreborn.service

import org.springframework.stereotype.*
import org.springframework.transaction.annotation.*

@Service
class ExampleService(private val doDbService: DoDbService) {

    @Transactional
    fun doStuff() {
        doDbService.doDbStuff()
    }

}

@Service
class DoDbService() {
    @Transactional
    fun doDbStuff() {
        // Do something
    }
}