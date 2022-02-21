package de.tjarksaul.wachmanager.modules.stationCheck

import com.google.gson.Gson
import org.koin.dsl.module.module

val stationCheckModule = module {
    single { EntryService(get(), get(), Gson()) }
}
