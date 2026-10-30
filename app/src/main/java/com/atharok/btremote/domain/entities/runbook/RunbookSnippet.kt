package com.atharok.btremote.domain.entities.runbook

import kotlinx.serialization.Serializable

/**
 * Model representing a saved text snippet or command runbook (KM Pro feature in Openterface KeyMOD).
 */
@Serializable
data class RunbookSnippet(
    val id: String,
    val title: String,
    val command: String,
    val category: String = "General",
    val autoEnter: Boolean = true,
    val delayMs: Long = 25L
) {
    companion object {
        fun defaultSnippets(): List<RunbookSnippet> = listOf(
            RunbookSnippet("1", "List IP Addresses", "ip a || ifconfig", "Network"),
            RunbookSnippet("2", "Ping Gateway / DNS", "ping -c 4 8.8.8.8", "Network"),
            RunbookSnippet("3", "System Service Status", "systemctl status", "Sysadmin"),
            RunbookSnippet("4", "Disk Usage Summary", "df -h", "Sysadmin"),
            RunbookSnippet("5", "Reboot to BIOS / UEFI", "systemctl reboot --firmware-setup", "Sysadmin"),
            RunbookSnippet("6", "Top Processes", "top -b -n 1 | head -n 25", "Sysadmin"),
            RunbookSnippet("7", "Windows Network Info", "ipconfig /all", "Windows"),
            RunbookSnippet("8", "Windows Running Tasks", "tasklist", "Windows")
        )
    }
}
