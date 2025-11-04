package com.example.bibliored.util

import java.net.InetAddress
import java.net.UnknownHostException

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
