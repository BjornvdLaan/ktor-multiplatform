package nl.bjornvanderlaan

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

fun startServer(platform: String) {
    embeddedServer(CIO, port = 8080) {
        install(ContentNegotiation) {
            json(Json { prettyPrint = true })
        }

        val catRepository = CatRepository()

        routing {
            get("/") {
                call.respondText { "Hello from the world of $platform, KotlinConf!" }
            }

            get("/cat/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respondText("Id is missing from request", status = HttpStatusCode.BadRequest)

                val cat = catRepository.findById(id)
                    ?: return@get call.respondText("No cat with id $id", status = HttpStatusCode.NotFound)

                return@get call.respond(cat)
            }
        }
    }.start(wait = true)
}

@Serializable
data class Cat(val id: Long, val name: String, val breed: String)

class CatRepository {
    val cats = listOf(
        Cat(0, "Kitty", "Siamese Cat"),
        Cat(1, "Mr Chief", "Maine Coon"),
        Cat(2, "Bob", "Burmese Cat"),
        Cat(3, "Kevin", "Sphynx"),
        Cat(4, "Iris", "Ragdoll")
    )
        .associateBy { it.id }

    suspend fun findById(id: Long): Cat? {
        delay(200)
        return cats[id % cats.size]
    }
}