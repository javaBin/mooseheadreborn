package no.java.mooseheadreborn

import jakarta.servlet.http.*
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.io.IOException


@Controller
class SPAController {

    @RequestMapping(value = ["{_:^(?!api).*\$}/**"])
    @ResponseBody
    @Throws(IOException::class)
    fun redirect(request: HttpServletRequest): Resource {
        val servletPath = request.servletPath
        val path = if (servletPath.indexOf(".") == -1) "/static/index.html" else "/static" + request.servletPath
        return ClassPathResource(path)
    }
}