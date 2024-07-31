package no.java.mooseheadreborn.service

import no.java.mooseheadreborn.*
import org.slf4j.*
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service


interface SendMailService {
    fun sendEmail(to: String, subject: String, body: String)
}


@Profile("dev")
@Service
class SendEmailServiceDummy:SendMailService {
    override fun sendEmail(to: String, subject: String, body: String) {
        logger.info("Sending email to $to ($subject) -> $body")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SendMailService::class.java)
    }

}

@Profile("prod")
@Service
class SendEmailServiceLIve:SendMailService {
    override fun sendEmail(to: String, subject: String, body: String) {
        logger.info("Sending prod email to $to ($subject) -> $body")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SendMailService::class.java)
    }

}