package no.java.mooseheadreborn.util

enum class EmailTemplate {
    REGISTER_CONFIRMATION,
    PARTICIPANT_CONFIRMATION,
}

enum class EmailVariable {
    REGISTRATION_ID,
    ACCESS_KEY
}

object EmailTextGenerator {
    fun loadText(template: EmailTemplate,variableMap:Map<EmailVariable,String>): String {
        return "some dummy email text $template variables: $variableMap"
    }
}