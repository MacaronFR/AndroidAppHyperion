package fr.macaron_dev.hyperion.data

import java.io.Serializable

data class Project(
    val id: Int,
    val logo: Int,
    val name: String,
    val description: String,
    val left: Int,
    val contribution: Int,
): Serializable
