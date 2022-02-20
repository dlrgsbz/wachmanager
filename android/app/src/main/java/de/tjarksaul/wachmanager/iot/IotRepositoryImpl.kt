package de.tjarksaul.wachmanager.iot

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.google.gson.Gson
import de.tjarksaul.wachmanager.iotClient.*
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class IotRepositoryImpl(
    private val gson: Gson,
    private val iotClient: IotClient
) : IotRepository,
    // todo: move this somewhere
    CoroutineScope {

    private val parentJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.IO + parentJob

    private val connection: LiveData<ConnectionState> =
        Transformations.map(iotClient.getConnectionState()) { connectionState ->
            when (connectionState) {
                IotConnectionState.Connected -> ConnectionState.Connected
                else -> ConnectionState.Disconnected
            }
        }

    private var config: IotConfig? = null
    override fun observeConnection(): LiveData<ConnectionState> = connection

    override fun connect(config: IotConfig) {
        if (connection.value is ConnectionState.Connected) {
            return
        }
        this.config = config

        iotClient.connect(config)

        launch(coroutineContext) {
            delay(3 * 1_000)
            Timber.tag("IotRepository").i("updating shadow")
            iotClient.publish(
                "\$aws/things/${config.clientId}/shadow/update",
                ShadowData(connected = true).toJson(gson = gson)
            )
            iotClient.subscribe(
                "stations/${config.clientId}/irgendwas",
                { status -> Timber.tag("TestSubscription").d("Subscription: $status") },
                { data -> Timber.tag("TestSubscription").d("got message: $data") })
        }
    }

    override fun disconnect() {
        config?.let {
            iotClient.publish(
                "\$aws/things/${it.clientId}/shadow/update",
                gson.toJson(ShadowData(connected = false))
            )
        }

        iotClient.disconnect()
    }
}