package managers

import common.entities.MovieManager
import java.io.FileReader
import java.util.*

class DirManager(private val movieManager: MovieManager) {
    private val user: String
    private val password: String
    private val url: String
    init {
        val properties = Properties()
        FileReader("server/src/main/resources/db.cfg").use { reader ->
            properties.load(reader)
        }

        user = properties.getProperty("user")
        password = properties.getProperty("password")
        url = properties.getProperty("URL")
    }
}