package com.test.design.data.car

/**
 * HVAC fan-direction bitmasks from the Vehicle HAL
 * (`android.hardware.automotive.vehicle.VehicleHvacFanDirection`).
 * Not exposed as a public `android.car` type on all SDK levels.
 */
internal object HvacFanDirection {
    const val FACE: Int = 0x1
    const val FLOOR: Int = 0x2
    const val DEFROST: Int = 0x4
    const val FACE_AND_FLOOR: Int = FACE or FLOOR
}

/**
 * Window area IDs used by [android.car.VehiclePropertyIds.HVAC_DEFROSTER].
 * Prefer discovering area IDs from [android.car.hardware.CarPropertyConfig];
 * these match the AOSP default VHAL.
 */
internal object HvacWindowArea {
    const val FRONT_WINDSHIELD: Int = 0x1
    const val REAR_WINDSHIELD: Int = 0x2
}
