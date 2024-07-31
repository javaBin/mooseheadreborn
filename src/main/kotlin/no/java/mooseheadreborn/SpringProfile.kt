package no.java.mooseheadreborn

enum class SpringProfile(val text:String) {
    PROD("prod"),
    DEV("dev"),
    ;
    companion object {
        fun current():SpringProfile = SpringProfile.valueOf(Config.getConfigValue(ConfigVariable.SPRING_PROFILE))
    }
}