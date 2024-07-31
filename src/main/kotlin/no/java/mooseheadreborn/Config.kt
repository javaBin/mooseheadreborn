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
    ;

    fun readValue():String = Config.getConfigValue(this)
}

object Config {
    private val configValueMap:MutableMap<ConfigVariable,String> = ConcurrentHashMap()

    fun loadConfig(args: Array<String>) {
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