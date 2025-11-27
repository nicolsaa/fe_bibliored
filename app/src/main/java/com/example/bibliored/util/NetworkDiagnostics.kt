package com.example.bibliored.util

import java.net.InetAddress
import java.net.UnknownHostException

/*Este código verifica si un host de internet es alcanzable/resoluble antes de hacer requests de red.*/

object NetworkDiagnostics {
    fun isHostResolvable(host: String): Boolean {
        return try {
            val address = InetAddress.getByName(host)
            address?.hostAddress?.isNotEmpty() == true
        } catch (e: UnknownHostException) {
            false
        } catch (e: Exception) {
            false
        }
    }
}
