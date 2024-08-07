package no.java.mooseheadreborn

import java.io.File
import java.util.concurrent.*

enum class ConfigVariable(val defaultValue: String) {
    PORT("8080"),
    ADMIN_PASSWORD("bingo"),
    SPRING_PROFILE(SpringProfile.DEV.name),
    SERVER_ADDRESS("http://localhost:3000"),
    SENDGRID_KEY(""),
    MAIL_FROM("program@java.no"),
    POSTGRES_URL("jdbc:postgresql://localhost:5432/mooserblocal"),
    POSTGRES_USER("localdevuser"),
    POSTGRES_PASSWORD("localdevuser"),
    ADMIN_LOGIN_DURATION_MINUTES("480"),
    ;

    fun readValue():String = Config.getConfigValue(this)
    fun longValue():Long = readValue().toLong()
}

object Config {
    private val configValueMap:MutableMap<ConfigVariable,String> = ConcurrentHashMap()

    fun validateAndloadConfig(args: Array<String>) {
        loadConfig(args)
        if (SpringProfile.PROD.name == ConfigVariable.SPRING_PROFILE.readValue() && ConfigVariable.SENDGRID_KEY.readValue().isEmpty()) {
            throw RuntimeException("Missing sendgrid config variable")
        }
    }

    private fun loadConfig(args: Array<String>) {


        if (args.isEmpty()) {
            return
        }
        for (line in File(args[0]).readLines()) {
            if (line.isEmpty() || line.startsWith("#")) {
                continue
            }
            val pos:Int = line.indexOf("=")
            if (pos == -1) {
                continue
            }
            val configVariable = ConfigVariable.valueOf(line.substring(0, pos))
            val configValue = line.substring(pos + 1)
            configValueMap[configVariable] = configValue
        }
    }

    fun getConfigValue(variable: ConfigVariable): String = configValueMap[variable]?:variable.defaultValue

    fun getIntValue(variable: ConfigVariable): Int = getConfigValue(variable).toInt()

}