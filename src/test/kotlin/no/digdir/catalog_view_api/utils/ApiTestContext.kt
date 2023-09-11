package no.digdir.catalog_view_api.utils

import org.springframework.boot.test.web.server.LocalServerPort
import java.net.HttpURLConnection
import java.net.URL

abstract class ApiTestContext {

    @LocalServerPort
    var port = 0

    companion object {

        init {
            startMockServer()

            try {
                val con = URL("http://localhost:5050/ping").openConnection() as HttpURLConnection
                con.connect()
                if (con.responseCode != 200) {
                    stopMockServer()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stopMockServer()
            }
        }
    }

}
