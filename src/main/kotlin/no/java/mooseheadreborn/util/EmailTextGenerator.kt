package no.java.mooseheadreborn.util

import no.java.mooseheadreborn.*

enum class EmailTemplate(val templatePath:String,val subject:String) {
    REGISTER_CONFIRMATION("templates/registrationConfirmation.html","Workshop confirmation"),
    REGISTER_CONFIRMATION_WAITING("templates/waitingConfirmation.html","Workshop confirmation"),
    PARTICIPANT_CONFIRMATION("templates/confirmEmail.html","Confirm email"),
    CANCEL_CONFIRMATION("templates/cancelConfirmation.html","Workshop registration cancelled"),
}

enum class EmailVariable {
    CANCEL_LINK,
    CONFIRM_EMAIL_LINK,
    WORKSHOP_NAME,
    WORKSHOP_TIME_TEXT,
}

object EmailTextGenerator {
    fun loadText(template: EmailTemplate,variableMap:Map<EmailVariable,String>): String {
        val inputStream = this::class.java.classLoader.getResourceAsStream(template.templatePath)
        val content = StringBuilder(inputStream?.bufferedReader().use { it?.readText() }?:"")
        variableMap.forEach { (variable, value) ->
            val search = "#${variable.name}#"
            while (true) {
                val pos = content.indexOf(search)
                if (pos == -1) break
                content.replace(pos,pos+search.length,value)
            }
        }
        return content.toString()
    }

    fun emailConfirnmAddress(registerKey:String):String = "${Config.getConfigValue(ConfigVariable.SERVER_ADDRESS)}/activate/$registerKey"
    fun cancelLinkAddress(registrationId:String):String = "${Config.getConfigValue(ConfigVariable.SERVER_ADDRESS)}/registration/$registrationId"
}