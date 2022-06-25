package de.tjarksaul.wachmanager.modules.crew.database

import androidx.room.Entity

@Entity(tableName = "crew_info", primaryKeys = ["date"])
data class CrewInfo(
    val crew: String,
    val date: String,
)
