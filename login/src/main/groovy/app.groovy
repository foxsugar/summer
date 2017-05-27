import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GroovyTest {

    @RequestMapping("/app")
    String home() {
        return "Hello World!"
    }

}