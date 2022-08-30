import org.json.JSONObject
import java.io.File

object AlarmConfig {
    var simulation = true

    init {
        `it.unibo`.radarSystem22.domain.utils.DomainSystemConfig.ledGui = true
        `it.unibo`.radarSystem22.domain.utils.DomainSystemConfig.simulation = true
        simulation = true
        ws.const.DLIMIT = 10
    }

    fun loadConf() {
        val config = File("alarmConfig").readText(Charsets.UTF_8)
        val jsonObject = JSONObject(config)
        `it.unibo`.radarSystem22.domain.utils.DomainSystemConfig.ledGui = jsonObject.getBoolean("ledGui")
        simulation = jsonObject.getBoolean("simulation")
        `it.unibo`.radarSystem22.domain.utils.DomainSystemConfig.simulation = simulation
        ws.const.DLIMIT = jsonObject.getInt("DLIMIT")
    }
}