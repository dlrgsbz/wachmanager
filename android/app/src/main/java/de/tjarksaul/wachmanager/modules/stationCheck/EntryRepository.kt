package de.tjarksaul.wachmanager.modules.stationCheck

import com.google.gson.Gson
import de.tjarksaul.wachmanager.dtos.PostEntry
import de.tjarksaul.wachmanager.dtos.StateKind
import de.tjarksaul.wachmanager.iotClient.IotClient
import de.tjarksaul.wachmanager.service.StationNameProvider

class EntryRepository(
    private val iotClient: IotClient,
    private val stationNameProvider: StationNameProvider,
    private val gson: Gson
) {
    fun updateEntry(
        fieldId: String,
        stationId: String,
        state: Boolean,
        stateKind: StateKind?,
        amount: Int?,
        note: String?,
        crew: String
    ) {
        val entry = PostEntry(state, stateKind, amount, note, crew)
        val stationName = stationNameProvider.currentStationName()
        iotClient.publish("${stationName}/field/${fieldId}/entry", gson.toJson(entry))
    }

}