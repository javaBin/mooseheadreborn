package no.java.mooseheadreborn.service

import org.slf4j.*
import org.springframework.stereotype.Service


interface SendMailService {
    fun sendEmail(to: String, subject: String, body: String)
}

@Service
class SendEmailServiceDummy:SendMailService {
    override fun sendEmail(to: String, subject: String, body: String) {
        logger.info("Sending email to $to ($subject) -> $body")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SendMailService::class.java)
    }

}