import api.Controller
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.event.Level

val controller = Controller()

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging) {
        level = Level.INFO
    }
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // Pretty Prints the JSON
        }
    }

    controller.initRouting(this)
}

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080, watchPaths = listOf("blockchain"), module = Application::module).start()
}