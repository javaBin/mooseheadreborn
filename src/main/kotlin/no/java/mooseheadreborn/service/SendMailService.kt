package no.java.mooseheadreborn.service

import com.sendgrid.*
import org.slf4j.*
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import no.java.mooseheadreborn.*


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
        val sendGrid = SendGrid(ConfigVariable.SENDGRID_KEY.readValue())
        val from = Email(ConfigVariable.MAIL_FROM.readValue())
        val content = Content("text/html",body)

        val personalization = Personalization()
        personalization.addTo(Email(to))

        val mail = Mail()

        mail.addPersonalization(personalization)
        mail.addContent(content)

        mail.from = from
        mail.subject = subject

        val request = Request()
        request.method = Method.POST
        request.endpoint = "mail/send"
        request.body = mail.build()

        sendGrid.api(request)




    }

    companion object {
        private val logger = LoggerFactory.getLogger(SendMailService::class.java)
    }

}