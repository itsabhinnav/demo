package com.test.design.data.car

import android.car.Car
import android.car.VehicleAreaSeat
import android.car.VehiclePropertyIds
import android.car.VehicleUnit
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.test.design.presentation.ivi.climate.AirflowMode
import com.test.design.presentation.ivi.climate.ClimateCapabilities
import com.test.design.presentation.ivi.climate.ClimateZone
import com.test.design.presentation.ivi.climate.TemperatureUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Reads/writes HVAC vehicle properties through [CarPropertyManager].
 *
 * Discovers which properties/areas the VHAL exposes and surfaces that as
 * [ClimateCapabilities] so the UI can hide unsupported controls.
 */
class CarClimateRepository(
    context: Context,
) {
    private val appContext = context.applicationContext
    private val mainHandler = Handler(Looper.getMainLooper())
    private val started = AtomicBoolean(false)

    private var car: Car? = null
    private var propertyManager: CarPropertyManager? = null
    private var areas: HvacAreas = HvacAreas()

    private val _connection = MutableStateFlow(ClimateHvacConnection())
    val connection: StateFlow<ClimateHvacConnection> = _connection.asStateFlow()

    private val callback = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(value: CarPropertyValue<*>?) {
            if (value == null) return
            mainHandler.post { applyPropertyValue(value) }
        }

        override fun onErrorEvent(propId: Int, zone: Int) {
            Log.w(TAG, "HVAC property error propId=$propId area=$zone")
        }
    }

    fun start() {
        if (!started.compareAndSet(false, true)) return
        try {
            car = Car.createCar(
                appContext,
                mainHandler,
                Car.CAR_WAIT_TIMEOUT_WAIT_FOREVER,
            ) { created, ready ->
                if (ready) {
                    onCarReady(created)
                } else {
                    onCarDisconnected()
                }
            }
        } catch (t: Throwable) {
            Log.w(TAG, "Car.createCar failed", t)
            started.set(false)
            _connection.value = ClimateHvacConnection(isLive = false)
        }
    }

    fun stop() {
        if (!started.getAndSet(false)) return
        unregisterAll()
        propertyManager = null
        runCatching { car?.disconnect() }
        car = null
        areas = HvacAreas()
        _connection.value = ClimateHvacConnection(isLive = false)
    }

    fun setZoneTemperature(zone: ClimateZone, celsius: Float) {
        val mgr = propertyManager ?: return
        val area = when (zone) {
            ClimateZone.Driver -> areas.driverTemp
            ClimateZone.Passenger -> areas.passengerTemp
        } ?: return
        val snapped = snapToStep(celsius, areas.minTempC, areas.maxTempC, areas.tempStepC)
        setFloat(mgr, VehiclePropertyIds.HVAC_TEMPERATURE_SET, area, snapped)
        ensurePowerOn(mgr, area)
    }

    fun setFanSpeed(speed: Int) {
        val mgr = propertyManager ?: return
        val targets = areas.fanSpeedAreas.ifEmpty { return }
        targets.forEach { area ->
            setInt(mgr, VehiclePropertyIds.HVAC_FAN_SPEED, area, speed)
            ensurePowerOn(mgr, area)
        }
    }

    fun setAcEnabled(enabled: Boolean) {
        val mgr = propertyManager ?: return
        areas.acAreas.forEach { setBoolean(mgr, VehiclePropertyIds.HVAC_AC_ON, it, enabled) }
    }

    fun setSyncEnabled(syncEnabled: Boolean) {
        val mgr = propertyManager ?: return
        val area = areas.dual ?: return
        // HVAC_DUAL_ON true = independent zones; UI "sync" is the inverse.
        setBoolean(mgr, VehiclePropertyIds.HVAC_DUAL_ON, area, !syncEnabled)
        if (syncEnabled) {
            val driverTemp = _connection.value.driverTempCelsius
            areas.passengerTemp?.let {
                setFloat(mgr, VehiclePropertyIds.HVAC_TEMPERATURE_SET, it, driverTemp)
            }
        }
    }

    fun setRecirculation(on: Boolean) {
        val mgr = propertyManager ?: return
        areas.recircAreas.forEach { setBoolean(mgr, VehiclePropertyIds.HVAC_RECIRC_ON, it, on) }
    }

    fun setFrontDefrost(on: Boolean) {
        val mgr = propertyManager ?: return
        val area = areas.frontDefrost ?: return
        setBoolean(mgr, VehiclePropertyIds.HVAC_DEFROSTER, area, on)
    }

    fun setRearDefrost(on: Boolean) {
        val mgr = propertyManager ?: return
        val area = areas.rearDefrost ?: return
        setBoolean(mgr, VehiclePropertyIds.HVAC_DEFROSTER, area, on)
    }

    fun setAirflowMode(mode: AirflowMode) {
        val mgr = propertyManager ?: return
        when (mode) {
            AirflowMode.Auto -> {
                areas.autoAreas.forEach { setBoolean(mgr, VehiclePropertyIds.HVAC_AUTO_ON, it, true) }
            }
            AirflowMode.Face -> {
                areas.autoAreas.forEach { setBoolean(mgr, VehiclePropertyIds.HVAC_AUTO_ON, it, false) }
                areas.fanDirectionAreas.forEach {
                    setInt(mgr, VehiclePropertyIds.HVAC_FAN_DIRECTION, it, HvacFanDirection.FACE)
                }
            }
            AirflowMode.BiLevel -> {
                areas.autoAreas.forEach { setBoolean(mgr, VehiclePropertyIds.HVAC_AUTO_ON, it, false) }
                areas.fanDirectionAreas.forEach {
                    setInt(mgr, VehiclePropertyIds.HVAC_FAN_DIRECTION, it, HvacFanDirection.FACE_AND_FLOOR)
                }
            }
            AirflowMode.Feet -> {
                areas.autoAreas.forEach { setBoolean(mgr, VehiclePropertyIds.HVAC_AUTO_ON, it, false) }
                areas.fanDirectionAreas.forEach {
                    setInt(mgr, VehiclePropertyIds.HVAC_FAN_DIRECTION, it, HvacFanDirection.FLOOR)
                }
            }
        }
    }

    fun setSeatHeatLevel(level: Int) {
        val mgr = propertyManager ?: return
        val area = areas.seatHeat ?: return
        setInt(mgr, VehiclePropertyIds.HVAC_SEAT_TEMPERATURE, area, level)
    }

    fun setSeatVentLevel(level: Int) {
        val mgr = propertyManager ?: return
        val area = areas.seatVent ?: return
        setInt(mgr, VehiclePropertyIds.HVAC_SEAT_VENTILATION, area, level)
    }

    fun setSteeringHeatLevel(level: Int) {
        val mgr = propertyManager ?: return
        val area = areas.steeringHeat ?: return
        setInt(mgr, VehiclePropertyIds.HVAC_STEERING_WHEEL_HEAT, area, level)
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        val mgr = propertyManager ?: return
        if (areas.tempDisplayUnits == null) return
        val value = when (unit) {
            TemperatureUnit.Celsius -> VehicleUnit.CELSIUS
            TemperatureUnit.Fahrenheit -> VehicleUnit.FAHRENHEIT
        }
        setInt(mgr, VehiclePropertyIds.HVAC_TEMPERATURE_DISPLAY_UNITS, 0, value)
    }

    private fun onCarReady(created: Car) {
        car = created
        val mgr = try {
            created.getCarManager(Car.PROPERTY_SERVICE) as? CarPropertyManager
        } catch (t: Throwable) {
            Log.w(TAG, "PROPERTY_SERVICE unavailable", t)
            null
        }
        if (mgr == null) {
            _connection.value = ClimateHvacConnection(isLive = false)
            return
        }
        propertyManager = mgr
        areas = discoverAreas(mgr)
        val caps = areas.toCapabilities()
        if (!caps.hasAnyHvacControl) {
            Log.w(TAG, "No HVAC properties accessible (permission or VHAL)")
            _connection.value = ClimateHvacConnection(isLive = false, capabilities = caps)
            return
        }
        registerCallbacks(mgr)
        val snapshot = readAll(mgr).copy(isLive = true, capabilities = caps)
        _connection.value = snapshot
        Log.i(TAG, "HVAC live caps=$caps areas=$areas")
    }

    private fun onCarDisconnected() {
        unregisterAll()
        propertyManager = null
        areas = HvacAreas()
        _connection.update { ClimateHvacConnection(isLive = false) }
    }

    private fun discoverAreas(mgr: CarPropertyManager): HvacAreas {
        val tempAreas = areaIds(mgr, VehiclePropertyIds.HVAC_TEMPERATURE_SET)
        val driverTemp = preferredSeat(tempAreas, VehicleAreaSeat.SEAT_ROW_1_LEFT)
        val passengerTemp = preferredSeat(tempAreas, VehicleAreaSeat.SEAT_ROW_1_RIGHT)
            ?: tempAreas.firstOrNull { it != driverTemp }

        val fanSpeedAreas = cabinSeatAreas(areaIds(mgr, VehiclePropertyIds.HVAC_FAN_SPEED))
        val fanDirectionAreas = cabinSeatAreas(areaIds(mgr, VehiclePropertyIds.HVAC_FAN_DIRECTION))
        val acAreas = cabinSeatAreas(areaIds(mgr, VehiclePropertyIds.HVAC_AC_ON))
        val recircAreas = cabinSeatAreas(areaIds(mgr, VehiclePropertyIds.HVAC_RECIRC_ON))
        val autoAreas = cabinSeatAreas(areaIds(mgr, VehiclePropertyIds.HVAC_AUTO_ON))
        val powerAreas = cabinSeatAreas(areaIds(mgr, VehiclePropertyIds.HVAC_POWER_ON))

        val dualAreas = areaIds(mgr, VehiclePropertyIds.HVAC_DUAL_ON)
        val dual = dualAreas.firstOrNull()

        val defrostAreas = areaIds(mgr, VehiclePropertyIds.HVAC_DEFROSTER)
        val frontDefrost = defrostAreas.firstOrNull { it == HvacWindowArea.FRONT_WINDSHIELD }
            ?: defrostAreas.minOrNull()
        val rearDefrost = defrostAreas.firstOrNull { it == HvacWindowArea.REAR_WINDSHIELD }
            ?: defrostAreas.firstOrNull { it != frontDefrost }

        val seatHeatAreas = areaIds(mgr, VehiclePropertyIds.HVAC_SEAT_TEMPERATURE)
        val seatHeat = preferredSeat(seatHeatAreas, VehicleAreaSeat.SEAT_ROW_1_LEFT)
        val seatVentAreas = areaIds(mgr, VehiclePropertyIds.HVAC_SEAT_VENTILATION)
        val seatVent = preferredSeat(seatVentAreas, VehicleAreaSeat.SEAT_ROW_1_LEFT)

        val steeringAreas = areaIds(mgr, VehiclePropertyIds.HVAC_STEERING_WHEEL_HEAT)
        val steeringHeat = steeringAreas.firstOrNull()

        val tempUnitAreas = areaIds(mgr, VehiclePropertyIds.HVAC_TEMPERATURE_DISPLAY_UNITS)
        val tempDisplayUnits = tempUnitAreas.firstOrNull()

        val tempAreaForRange = driverTemp ?: tempAreas.firstOrNull()
        val (minTemp, maxTemp) = floatRange(
            mgr,
            VehiclePropertyIds.HVAC_TEMPERATURE_SET,
            tempAreaForRange,
            defaultMin = 16f,
            defaultMax = 30f,
        )
        val tempConfig = temperatureConfig(mgr, VehiclePropertyIds.HVAC_TEMPERATURE_SET)
        val tempStep = tempConfig.stepC
        val tempStepF = tempConfig.stepF
        val minTempF = tempConfig.minF
        // Prefer configArray Celsius range when present (matches CarSystemUI).
        val minTempC = tempConfig.minC ?: minTemp
        val maxTempC = tempConfig.maxC ?: maxTemp
        val (minFan, maxFan) = intRange(
            mgr,
            VehiclePropertyIds.HVAC_FAN_SPEED,
            fanSpeedAreas.firstOrNull(),
            defaultMin = 1,
            defaultMax = 5,
        )
        val (minSeatHeat, maxSeatHeat) = intRange(
            mgr,
            VehiclePropertyIds.HVAC_SEAT_TEMPERATURE,
            seatHeat,
            defaultMin = 0,
            defaultMax = 3,
        )
        val (minSeatVent, maxSeatVent) = intRange(
            mgr,
            VehiclePropertyIds.HVAC_SEAT_VENTILATION,
            seatVent,
            defaultMin = 0,
            defaultMax = 3,
        )
        val (minSteer, maxSteer) = intRange(
            mgr,
            VehiclePropertyIds.HVAC_STEERING_WHEEL_HEAT,
            steeringHeat,
            defaultMin = 0,
            defaultMax = 3,
        )

        return HvacAreas(
            driverTemp = driverTemp,
            passengerTemp = passengerTemp,
            fanSpeedAreas = fanSpeedAreas,
            fanDirectionAreas = fanDirectionAreas,
            acAreas = acAreas,
            recircAreas = recircAreas,
            autoAreas = autoAreas,
            powerAreas = powerAreas,
            dual = dual,
            frontDefrost = frontDefrost,
            rearDefrost = rearDefrost,
            seatHeat = seatHeat,
            seatVent = seatVent,
            steeringHeat = steeringHeat,
            tempDisplayUnits = tempDisplayUnits,
            minTempC = minTempC,
            maxTempC = maxTempC,
            tempStepC = tempStep,
            tempStepF = tempStepF,
            minTempF = minTempF,
            minFan = minFan.coerceAtLeast(0),
            maxFan = maxFan.coerceAtLeast(minFan),
            // Seat heat UI is 0..N heating levels (ignore negative "cool" range if present).
            maxSeatHeat = maxSeatHeat.coerceAtLeast(0),
            maxSeatVent = maxSeatVent.coerceAtLeast(0),
            maxSteeringHeat = maxSteer.coerceAtLeast(0),
            minSeatHeat = minSeatHeat.coerceAtLeast(0),
            minSeatVent = minSeatVent.coerceAtLeast(0),
            minSteeringHeat = minSteer.coerceAtLeast(0),
        )
    }

    private fun registerCallbacks(mgr: CarPropertyManager) {
        val props = buildList {
            if (areas.driverTemp != null || areas.passengerTemp != null) {
                add(VehiclePropertyIds.HVAC_TEMPERATURE_SET)
            }
            if (areas.fanSpeedAreas.isNotEmpty()) add(VehiclePropertyIds.HVAC_FAN_SPEED)
            if (areas.fanDirectionAreas.isNotEmpty()) add(VehiclePropertyIds.HVAC_FAN_DIRECTION)
            if (areas.acAreas.isNotEmpty()) add(VehiclePropertyIds.HVAC_AC_ON)
            if (areas.recircAreas.isNotEmpty()) add(VehiclePropertyIds.HVAC_RECIRC_ON)
            if (areas.autoAreas.isNotEmpty()) add(VehiclePropertyIds.HVAC_AUTO_ON)
            if (areas.dual != null) add(VehiclePropertyIds.HVAC_DUAL_ON)
            if (areas.frontDefrost != null || areas.rearDefrost != null) {
                add(VehiclePropertyIds.HVAC_DEFROSTER)
            }
            if (areas.seatHeat != null) add(VehiclePropertyIds.HVAC_SEAT_TEMPERATURE)
            if (areas.seatVent != null) add(VehiclePropertyIds.HVAC_SEAT_VENTILATION)
            if (areas.steeringHeat != null) add(VehiclePropertyIds.HVAC_STEERING_WHEEL_HEAT)
            if (areas.tempDisplayUnits != null) {
                add(VehiclePropertyIds.HVAC_TEMPERATURE_DISPLAY_UNITS)
            }
        }
        props.forEach { propId ->
            runCatching {
                mgr.registerCallback(callback, propId, CarPropertyManager.SENSOR_RATE_ONCHANGE)
            }.onFailure { Log.w(TAG, "registerCallback failed for $propId", it) }
        }
    }

    private fun unregisterAll() {
        val mgr = propertyManager ?: return
        runCatching { mgr.unregisterCallback(callback) }
    }

    private fun readAll(mgr: CarPropertyManager): ClimateHvacConnection {
        var driverTemp = 22f
        var passengerTemp = 22f
        var fanSpeed = 1
        var airflow = AirflowMode.Face
        var ac = false
        var sync = true
        var recirc = false
        var frontDefrost = false
        var rearDefrost = false
        var seatHeat = 0
        var seatVent = 0
        var steeringHeat = 0
        var unit = TemperatureUnit.Celsius
        var autoOn = false

        areas.driverTemp?.let {
            driverTemp = getFloat(mgr, VehiclePropertyIds.HVAC_TEMPERATURE_SET, it) ?: driverTemp
        }
        areas.passengerTemp?.let {
            passengerTemp = getFloat(mgr, VehiclePropertyIds.HVAC_TEMPERATURE_SET, it) ?: passengerTemp
        }
        areas.fanSpeedAreas.firstOrNull()?.let {
            fanSpeed = getInt(mgr, VehiclePropertyIds.HVAC_FAN_SPEED, it) ?: fanSpeed
        }
        areas.acAreas.firstOrNull()?.let {
            ac = getBoolean(mgr, VehiclePropertyIds.HVAC_AC_ON, it) ?: ac
        }
        areas.recircAreas.firstOrNull()?.let {
            recirc = getBoolean(mgr, VehiclePropertyIds.HVAC_RECIRC_ON, it) ?: recirc
        }
        areas.autoAreas.firstOrNull()?.let {
            autoOn = getBoolean(mgr, VehiclePropertyIds.HVAC_AUTO_ON, it) ?: autoOn
        }
        areas.dual?.let {
            val dualOn = getBoolean(mgr, VehiclePropertyIds.HVAC_DUAL_ON, it) ?: false
            sync = !dualOn
        }
        areas.frontDefrost?.let {
            frontDefrost = getBoolean(mgr, VehiclePropertyIds.HVAC_DEFROSTER, it) ?: frontDefrost
        }
        areas.rearDefrost?.let {
            rearDefrost = getBoolean(mgr, VehiclePropertyIds.HVAC_DEFROSTER, it) ?: rearDefrost
        }
        areas.seatHeat?.let {
            seatHeat = (getInt(mgr, VehiclePropertyIds.HVAC_SEAT_TEMPERATURE, it) ?: 0).coerceAtLeast(0)
        }
        areas.seatVent?.let {
            seatVent = (getInt(mgr, VehiclePropertyIds.HVAC_SEAT_VENTILATION, it) ?: 0).coerceAtLeast(0)
        }
        areas.steeringHeat?.let {
            steeringHeat = (getInt(mgr, VehiclePropertyIds.HVAC_STEERING_WHEEL_HEAT, it) ?: 0).coerceAtLeast(0)
        }
        areas.tempDisplayUnits?.let {
            unit = when (getInt(mgr, VehiclePropertyIds.HVAC_TEMPERATURE_DISPLAY_UNITS, it)) {
                VehicleUnit.FAHRENHEIT -> TemperatureUnit.Fahrenheit
                else -> TemperatureUnit.Celsius
            }
        }

        airflow = if (autoOn && areas.autoAreas.isNotEmpty()) {
            AirflowMode.Auto
        } else {
            val direction = areas.fanDirectionAreas.firstOrNull()?.let {
                getInt(mgr, VehiclePropertyIds.HVAC_FAN_DIRECTION, it)
            } ?: HvacFanDirection.FACE
            directionToAirflow(direction)
        }

        return ClimateHvacConnection(
            isLive = true,
            capabilities = areas.toCapabilities(),
            driverTempCelsius = driverTemp.coerceIn(areas.minTempC, areas.maxTempC),
            passengerTempCelsius = passengerTemp.coerceIn(areas.minTempC, areas.maxTempC),
            minTemperature = areas.minTempC,
            maxTemperature = areas.maxTempC,
            temperatureStepCelsius = areas.tempStepC,
            temperatureStepFahrenheit = areas.tempStepF,
            minTemperatureFahrenheit = areas.minTempF,
            fanSpeed = fanSpeed.coerceIn(areas.minFan.coerceAtLeast(1), areas.maxFan),
            maxFanSpeed = areas.maxFan.coerceAtLeast(1),
            airflowMode = airflow,
            isAcEnabled = ac,
            isSyncEnabled = sync,
            isRecirculationOn = recirc,
            isFrontDefrostOn = frontDefrost,
            isRearDefrostOn = rearDefrost,
            seatHeatLevel = seatHeat.coerceIn(0, areas.maxSeatHeat),
            maxSeatHeatLevel = areas.maxSeatHeat.coerceAtLeast(1),
            seatVentLevel = seatVent.coerceIn(0, areas.maxSeatVent),
            maxSeatVentLevel = areas.maxSeatVent.coerceAtLeast(1),
            steeringHeatLevel = steeringHeat.coerceIn(0, areas.maxSteeringHeat),
            maxSteeringHeatLevel = areas.maxSteeringHeat.coerceAtLeast(1),
            temperatureUnit = unit,
        )
    }

    private fun applyPropertyValue(value: CarPropertyValue<*>) {
        if (!_connection.value.isLive) return
        val area = value.areaId
        val prop = value.propertyId
        _connection.update { current ->
            when (prop) {
                VehiclePropertyIds.HVAC_TEMPERATURE_SET -> {
                    val temp = (value.value as? Number)?.toFloat() ?: return@update current
                    when (area) {
                        areas.driverTemp -> current.copy(driverTempCelsius = temp)
                        areas.passengerTemp -> current.copy(passengerTempCelsius = temp)
                        else -> current
                    }
                }
                VehiclePropertyIds.HVAC_FAN_SPEED -> {
                    if (area in areas.fanSpeedAreas || areas.fanSpeedAreas.isEmpty()) {
                        val speed = (value.value as? Number)?.toInt() ?: return@update current
                        current.copy(fanSpeed = speed.coerceIn(1, current.maxFanSpeed))
                    } else {
                        current
                    }
                }
                VehiclePropertyIds.HVAC_AC_ON -> {
                    if (area == areas.acAreas.firstOrNull()) {
                        current.copy(isAcEnabled = value.value as? Boolean ?: current.isAcEnabled)
                    } else {
                        current
                    }
                }
                VehiclePropertyIds.HVAC_RECIRC_ON -> {
                    if (area == areas.recircAreas.firstOrNull()) {
                        current.copy(isRecirculationOn = value.value as? Boolean ?: current.isRecirculationOn)
                    } else {
                        current
                    }
                }
                VehiclePropertyIds.HVAC_DUAL_ON -> {
                    if (area == areas.dual) {
                        val dualOn = value.value as? Boolean ?: false
                        current.copy(isSyncEnabled = !dualOn)
                    } else {
                        current
                    }
                }
                VehiclePropertyIds.HVAC_DEFROSTER -> {
                    when (area) {
                        areas.frontDefrost ->
                            current.copy(isFrontDefrostOn = value.value as? Boolean ?: current.isFrontDefrostOn)
                        areas.rearDefrost ->
                            current.copy(isRearDefrostOn = value.value as? Boolean ?: current.isRearDefrostOn)
                        else -> current
                    }
                }
                VehiclePropertyIds.HVAC_AUTO_ON -> {
                    if (area == areas.autoAreas.firstOrNull()) {
                        val auto = value.value as? Boolean ?: false
                        if (auto) current.copy(airflowMode = AirflowMode.Auto) else current
                    } else {
                        current
                    }
                }
                VehiclePropertyIds.HVAC_FAN_DIRECTION -> {
                    if (area == areas.fanDirectionAreas.firstOrNull()) {
                        if (current.airflowMode == AirflowMode.Auto && areas.autoAreas.isNotEmpty()) {
                            current
                        } else {
                            val direction = (value.value as? Number)?.toInt() ?: return@update current
                            current.copy(airflowMode = directionToAirflow(direction))
                        }
                    } else {
                        current
                    }
                }
                VehiclePropertyIds.HVAC_SEAT_TEMPERATURE -> {
                    if (area == areas.seatHeat) {
                        val level = ((value.value as? Number)?.toInt() ?: 0).coerceAtLeast(0)
                        current.copy(seatHeatLevel = level.coerceAtMost(current.maxSeatHeatLevel))
                    } else {
                        current
                    }
                }
                VehiclePropertyIds.HVAC_SEAT_VENTILATION -> {
                    if (area == areas.seatVent) {
                        val level = ((value.value as? Number)?.toInt() ?: 0).coerceAtLeast(0)
                        current.copy(seatVentLevel = level.coerceAtMost(current.maxSeatVentLevel))
                    } else {
                        current
                    }
                }
                VehiclePropertyIds.HVAC_STEERING_WHEEL_HEAT -> {
                    if (area == areas.steeringHeat) {
                        val level = ((value.value as? Number)?.toInt() ?: 0).coerceAtLeast(0)
                        current.copy(steeringHeatLevel = level.coerceAtMost(current.maxSteeringHeatLevel))
                    } else {
                        current
                    }
                }
                VehiclePropertyIds.HVAC_TEMPERATURE_DISPLAY_UNITS -> {
                    val unit = when ((value.value as? Number)?.toInt()) {
                        VehicleUnit.FAHRENHEIT -> TemperatureUnit.Fahrenheit
                        else -> TemperatureUnit.Celsius
                    }
                    current.copy(temperatureUnit = unit)
                }
                else -> current
            }
        }
    }

    private fun ensurePowerOn(mgr: CarPropertyManager, areaHint: Int) {
        val powerArea = when {
            areaHint in areas.powerAreas -> areaHint
            else -> areas.powerAreas.firstOrNull()
        } ?: return
        if (getBoolean(mgr, VehiclePropertyIds.HVAC_POWER_ON, powerArea) == false) {
            setBoolean(mgr, VehiclePropertyIds.HVAC_POWER_ON, powerArea, true)
        }
    }

    private fun areaIds(mgr: CarPropertyManager, propId: Int): IntArray {
        return try {
            val config = mgr.getCarPropertyConfig(propId) ?: return intArrayOf()
            config.areaIds ?: intArrayOf()
        } catch (t: Throwable) {
            Log.d(TAG, "areaIds unavailable for $propId: ${t.message}")
            intArrayOf()
        }
    }

    private fun cabinSeatAreas(all: IntArray): List<Int> {
        val row1 = listOf(
            VehicleAreaSeat.SEAT_ROW_1_LEFT,
            VehicleAreaSeat.SEAT_ROW_1_RIGHT,
            VehicleAreaSeat.SEAT_ROW_1_CENTER,
        )
        val preferred = all.filter { it in row1 }
        return preferred.ifEmpty { all.toList() }
    }

    private fun preferredSeat(areas: IntArray, preferred: Int): Int? =
        areas.firstOrNull { it == preferred } ?: areas.firstOrNull()

    private fun floatRange(
        mgr: CarPropertyManager,
        propId: Int,
        areaId: Int?,
        defaultMin: Float,
        defaultMax: Float,
    ): Pair<Float, Float> {
        if (areaId == null) return defaultMin to defaultMax
        return try {
            val config = mgr.getCarPropertyConfig(propId) ?: return defaultMin to defaultMax
            val min = (config.getMinValue(areaId) as? Number)?.toFloat() ?: defaultMin
            val max = (config.getMaxValue(areaId) as? Number)?.toFloat() ?: defaultMax
            min to max
        } catch (_: Throwable) {
            defaultMin to defaultMax
        }
    }

    private fun intRange(
        mgr: CarPropertyManager,
        propId: Int,
        areaId: Int?,
        defaultMin: Int,
        defaultMax: Int,
    ): Pair<Int, Int> {
        if (areaId == null) return defaultMin to defaultMax
        return try {
            val config = mgr.getCarPropertyConfig(propId) ?: return defaultMin to defaultMax
            val min = (config.getMinValue(areaId) as? Number)?.toInt() ?: defaultMin
            val max = (config.getMaxValue(areaId) as? Number)?.toInt() ?: defaultMax
            min to max
        } catch (_: Throwable) {
            defaultMin to defaultMax
        }
    }

    private fun getFloat(mgr: CarPropertyManager, propId: Int, areaId: Int): Float? =
        runCatching { mgr.getFloatProperty(propId, areaId) }.getOrNull()

    private fun getInt(mgr: CarPropertyManager, propId: Int, areaId: Int): Int? =
        runCatching { mgr.getIntProperty(propId, areaId) }.getOrNull()

    private fun getBoolean(mgr: CarPropertyManager, propId: Int, areaId: Int): Boolean? =
        runCatching { mgr.getBooleanProperty(propId, areaId) }.getOrNull()

    private fun setFloat(mgr: CarPropertyManager, propId: Int, areaId: Int, value: Float) {
        runCatching { mgr.setFloatProperty(propId, areaId, value) }
            .onFailure { Log.w(TAG, "setFloat $propId/$areaId=$value failed", it) }
    }

    private fun setInt(mgr: CarPropertyManager, propId: Int, areaId: Int, value: Int) {
        runCatching { mgr.setIntProperty(propId, areaId, value) }
            .onFailure { Log.w(TAG, "setInt $propId/$areaId=$value failed", it) }
    }

    private fun setBoolean(mgr: CarPropertyManager, propId: Int, areaId: Int, value: Boolean) {
        runCatching { mgr.setBooleanProperty(propId, areaId, value) }
            .onFailure { Log.w(TAG, "setBoolean $propId/$areaId=$value failed", it) }
    }

    private fun directionToAirflow(direction: Int): AirflowMode = when {
        direction and HvacFanDirection.FACE != 0 &&
            direction and HvacFanDirection.FLOOR != 0 -> AirflowMode.BiLevel
        direction and HvacFanDirection.FLOOR != 0 -> AirflowMode.Feet
        else -> AirflowMode.Face
    }

    private data class HvacAreas(
        val driverTemp: Int? = null,
        val passengerTemp: Int? = null,
        val fanSpeedAreas: List<Int> = emptyList(),
        val fanDirectionAreas: List<Int> = emptyList(),
        val acAreas: List<Int> = emptyList(),
        val recircAreas: List<Int> = emptyList(),
        val autoAreas: List<Int> = emptyList(),
        val powerAreas: List<Int> = emptyList(),
        val dual: Int? = null,
        val frontDefrost: Int? = null,
        val rearDefrost: Int? = null,
        val seatHeat: Int? = null,
        val seatVent: Int? = null,
        val steeringHeat: Int? = null,
        val tempDisplayUnits: Int? = null,
        val minTempC: Float = 16f,
        val maxTempC: Float = 30f,
        val tempStepC: Float = 0.5f,
        val tempStepF: Float = 1f,
        val minTempF: Float? = null,
        val minFan: Int = 1,
        val maxFan: Int = 5,
        val minSeatHeat: Int = 0,
        val maxSeatHeat: Int = 3,
        val minSeatVent: Int = 0,
        val maxSeatVent: Int = 3,
        val minSteeringHeat: Int = 0,
        val maxSteeringHeat: Int = 3,
    ) {
        fun toCapabilities(): ClimateCapabilities = ClimateCapabilities(
            hasDriverTemp = driverTemp != null,
            hasPassengerTemp = passengerTemp != null,
            hasFanSpeed = fanSpeedAreas.isNotEmpty(),
            hasFanDirection = fanDirectionAreas.isNotEmpty(),
            hasAuto = autoAreas.isNotEmpty(),
            hasAc = acAreas.isNotEmpty(),
            hasSync = dual != null,
            hasRecirculation = recircAreas.isNotEmpty(),
            hasFrontDefrost = frontDefrost != null,
            hasRearDefrost = rearDefrost != null,
            hasSeatHeat = seatHeat != null,
            hasSteeringHeat = steeringHeat != null,
            hasSeatVent = seatVent != null,
            hasTemperatureUnit = tempDisplayUnits != null,
        )
    }

    /**
     * HVAC_TEMPERATURE_SET configArray (AOSP):
     * `[minC×10, maxC×10, stepC×10, minF×10, maxF×10, stepF×10]`.
     * Older VHAls may only expose `[stepC×10]`.
     */
    private fun temperatureConfig(
        mgr: CarPropertyManager,
        propId: Int,
    ): TempConfig {
        return try {
            val config = mgr.getCarPropertyConfig(propId) ?: return TempConfig()
            val array = config.configArray
            when {
                array == null || array.isEmpty() -> TempConfig()
                array.size >= 6 -> TempConfig(
                    minC = array[0] / 10f,
                    maxC = array[1] / 10f,
                    stepC = (array[2].takeIf { it > 0 } ?: 5) / 10f,
                    minF = array[3] / 10f,
                    stepF = (array[5].takeIf { it > 0 } ?: 10) / 10f,
                )
                array.size >= 3 -> TempConfig(
                    minC = array[0] / 10f,
                    maxC = array[1] / 10f,
                    stepC = (array[2].takeIf { it > 0 } ?: 5) / 10f,
                )
                else -> {
                    // Legacy: single element = Celsius increment × 10.
                    TempConfig(stepC = (array[0].takeIf { it > 0 } ?: 5) / 10f)
                }
            }
        } catch (_: Throwable) {
            TempConfig()
        }
    }

    private data class TempConfig(
        val minC: Float? = null,
        val maxC: Float? = null,
        val stepC: Float = DEFAULT_TEMP_STEP_C,
        val minF: Float? = null,
        val stepF: Float = DEFAULT_TEMP_STEP_F,
    )

    private fun snapToStep(value: Float, min: Float, max: Float, step: Float): Float {
        if (step <= 0f) return value.coerceIn(min, max)
        val steps = kotlin.math.round((value - min) / step)
        return (min + steps * step).coerceIn(min, max)
    }

    companion object {
        private const val TAG = "CarClimateRepository"
        private const val DEFAULT_TEMP_STEP_C = 0.5f
        private const val DEFAULT_TEMP_STEP_F = 1f
    }
}

/** Live HVAC snapshot from VHAL (empty/non-live when Car/permission unavailable). */
data class ClimateHvacConnection(
    val isLive: Boolean = false,
    val capabilities: ClimateCapabilities = ClimateCapabilities(),
    val driverTempCelsius: Float = 22f,
    val passengerTempCelsius: Float = 21f,
    val minTemperature: Float = 16f,
    val maxTemperature: Float = 30f,
    val temperatureStepCelsius: Float = 0.5f,
    val temperatureStepFahrenheit: Float = 1f,
    val minTemperatureFahrenheit: Float? = null,
    val fanSpeed: Int = 3,
    val maxFanSpeed: Int = 5,
    val airflowMode: AirflowMode = AirflowMode.Auto,
    val isAcEnabled: Boolean = true,
    val isSyncEnabled: Boolean = true,
    val isRecirculationOn: Boolean = false,
    val isFrontDefrostOn: Boolean = false,
    val isRearDefrostOn: Boolean = false,
    val seatHeatLevel: Int = 0,
    val maxSeatHeatLevel: Int = 3,
    val seatVentLevel: Int = 0,
    val maxSeatVentLevel: Int = 3,
    val steeringHeatLevel: Int = 0,
    val maxSteeringHeatLevel: Int = 3,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.Celsius,
)
