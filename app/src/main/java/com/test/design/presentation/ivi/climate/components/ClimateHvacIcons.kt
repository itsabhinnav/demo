package com.test.design.presentation.ivi.climate.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Compact steering-wheel glyph for HVAC comfort controls. */
val SteeringWheelIcon: ImageVector
    get() = cachedIcon("SteeringWheel") {
        path(
            fill = SolidColor(Color.Black),
            pathFillType = PathFillType.EvenOdd,
        ) {
            moveTo(12f, 2f)
            curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
            reflectiveCurveTo(6.48f, 22f, 12f, 22f)
            reflectiveCurveTo(22f, 17.52f, 22f, 12f)
            reflectiveCurveTo(17.52f, 2f, 12f, 2f)
            close()
            moveTo(12f, 4f)
            curveTo(16.41f, 4f, 20f, 7.59f, 20f, 12f)
            curveTo(20f, 14.97f, 18.39f, 17.55f, 16f, 18.9f)
            lineTo(16f, 16.5f)
            curveTo(16f, 15.12f, 14.88f, 14f, 13.5f, 14f)
            lineTo(10.5f, 14f)
            curveTo(9.12f, 14f, 8f, 15.12f, 8f, 16.5f)
            lineTo(8f, 18.9f)
            curveTo(5.61f, 17.55f, 4f, 14.97f, 4f, 12f)
            curveTo(4f, 7.59f, 7.59f, 4f, 12f, 4f)
            close()
            moveTo(12f, 7f)
            curveTo(10.34f, 7f, 9f, 8.34f, 9f, 10f)
            reflectiveCurveTo(10.34f, 13f, 12f, 13f)
            reflectiveCurveTo(15f, 11.66f, 15f, 10f)
            reflectiveCurveTo(13.66f, 7f, 12f, 7f)
            close()
        }
    }

/** Seated figure with arrow toward face/torso. */
val AirflowFaceIcon: ImageVector
    get() = cachedIcon("AirflowFace") {
        // Person silhouette
        path(fill = SolidColor(Color.Black)) {
            moveTo(15.5f, 5.5f)
            curveTo(15.5f, 4.12f, 16.62f, 3f, 18f, 3f)
            reflectiveCurveTo(20.5f, 4.12f, 20.5f, 5.5f)
            reflectiveCurveTo(19.38f, 8f, 18f, 8f)
            reflectiveCurveTo(15.5f, 6.88f, 15.5f, 5.5f)
            close()
            moveTo(14.2f, 9.2f)
            curveTo(15.1f, 8.7f, 16.3f, 8.5f, 17.5f, 8.8f)
            curveTo(19.2f, 9.2f, 20.5f, 10.6f, 20.8f, 12.4f)
            lineTo(21.5f, 16.5f)
            lineTo(19.3f, 16.9f)
            lineTo(18.7f, 13.5f)
            lineTo(17.2f, 14.2f)
            lineTo(17.8f, 21f)
            lineTo(15.6f, 21f)
            lineTo(15f, 15.8f)
            lineTo(14.2f, 16.2f)
            lineTo(14.8f, 21f)
            lineTo(12.6f, 21f)
            lineTo(11.8f, 14.5f)
            curveTo(11.5f, 12.2f, 12.4f, 10.2f, 14.2f, 9.2f)
            close()
        }
        // Arrow to torso
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(2.5f, 10.5f)
            lineTo(11.5f, 10.5f)
            moveTo(9.2f, 7.8f)
            lineTo(12.2f, 10.5f)
            lineTo(9.2f, 13.2f)
        }
    }

/** Seated figure with face + feet arrows. */
val AirflowBiLevelIcon: ImageVector
    get() = cachedIcon("AirflowBiLevel") {
        path(fill = SolidColor(Color.Black)) {
            moveTo(15.5f, 5.5f)
            curveTo(15.5f, 4.12f, 16.62f, 3f, 18f, 3f)
            reflectiveCurveTo(20.5f, 4.12f, 20.5f, 5.5f)
            reflectiveCurveTo(19.38f, 8f, 18f, 8f)
            reflectiveCurveTo(15.5f, 6.88f, 15.5f, 5.5f)
            close()
            moveTo(14.2f, 9.2f)
            curveTo(15.1f, 8.7f, 16.3f, 8.5f, 17.5f, 8.8f)
            curveTo(19.2f, 9.2f, 20.5f, 10.6f, 20.8f, 12.4f)
            lineTo(21.5f, 16.5f)
            lineTo(19.3f, 16.9f)
            lineTo(18.7f, 13.5f)
            lineTo(17.2f, 14.2f)
            lineTo(17.8f, 21f)
            lineTo(15.6f, 21f)
            lineTo(15f, 15.8f)
            lineTo(14.2f, 16.2f)
            lineTo(14.8f, 21f)
            lineTo(12.6f, 21f)
            lineTo(11.8f, 14.5f)
            curveTo(11.5f, 12.2f, 12.4f, 10.2f, 14.2f, 9.2f)
            close()
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(2.5f, 10f)
            lineTo(11.2f, 10f)
            moveTo(9f, 7.4f)
            lineTo(11.8f, 10f)
            lineTo(9f, 12.6f)
            moveTo(8.5f, 13.5f)
            lineTo(8.5f, 20.5f)
            moveTo(6f, 18f)
            lineTo(8.5f, 20.8f)
            lineTo(11f, 18f)
        }
    }

/** Seated figure with feet arrow. */
val AirflowFeetIcon: ImageVector
    get() = cachedIcon("AirflowFeet") {
        path(fill = SolidColor(Color.Black)) {
            moveTo(15.5f, 5.5f)
            curveTo(15.5f, 4.12f, 16.62f, 3f, 18f, 3f)
            reflectiveCurveTo(20.5f, 4.12f, 20.5f, 5.5f)
            reflectiveCurveTo(19.38f, 8f, 18f, 8f)
            reflectiveCurveTo(15.5f, 6.88f, 15.5f, 5.5f)
            close()
            moveTo(14.2f, 9.2f)
            curveTo(15.1f, 8.7f, 16.3f, 8.5f, 17.5f, 8.8f)
            curveTo(19.2f, 9.2f, 20.5f, 10.6f, 20.8f, 12.4f)
            lineTo(21.5f, 16.5f)
            lineTo(19.3f, 16.9f)
            lineTo(18.7f, 13.5f)
            lineTo(17.2f, 14.2f)
            lineTo(17.8f, 21f)
            lineTo(15.6f, 21f)
            lineTo(15f, 15.8f)
            lineTo(14.2f, 16.2f)
            lineTo(14.8f, 21f)
            lineTo(12.6f, 21f)
            lineTo(11.8f, 14.5f)
            curveTo(11.5f, 12.2f, 12.4f, 10.2f, 14.2f, 9.2f)
            close()
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(8.5f, 11f)
            lineTo(8.5f, 20.5f)
            moveTo(5.8f, 17.8f)
            lineTo(8.5f, 20.8f)
            lineTo(11.2f, 17.8f)
        }
    }

/** Front windshield defrost. */
val FrontDefrostIcon: ImageVector
    get() = cachedIcon("FrontDefrost") {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(4f, 18f)
            curveTo(6f, 10f, 10f, 6f, 12f, 5.5f)
            curveTo(14f, 6f, 18f, 10f, 20f, 18f)
            close()
            moveTo(9f, 10.5f)
            curveTo(9f, 12.5f, 8.6f, 14.8f, 8f, 16.8f)
            moveTo(12f, 9.2f)
            curveTo(12f, 11.5f, 11.7f, 14.2f, 11.2f, 16.8f)
            moveTo(15f, 10.5f)
            curveTo(15f, 12.5f, 15.4f, 14.8f, 16f, 16.8f)
        }
    }

/** Rear window defrost. */
val RearDefrostIcon: ImageVector
    get() = cachedIcon("RearDefrost") {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.8f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(5f, 6f)
            lineTo(19f, 6f)
            lineTo(19f, 18f)
            lineTo(5f, 18f)
            close()
            moveTo(9f, 8.5f)
            curveTo(9f, 11f, 8.6f, 13.8f, 8.2f, 15.8f)
            moveTo(12f, 8.5f)
            curveTo(12f, 11f, 12f, 13.8f, 12f, 15.8f)
            moveTo(15f, 8.5f)
            curveTo(15f, 11f, 15.4f, 13.8f, 15.8f, 15.8f)
        }
    }

/** Fresh outdoor air into cabin. */
val FreshAirIcon: ImageVector
    get() = cachedIcon("FreshAir") {
        // Car side profile
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(4f, 15f)
            lineTo(5.5f, 11.5f)
            lineTo(9f, 9.5f)
            lineTo(14.5f, 9.5f)
            lineTo(18f, 12f)
            lineTo(21f, 12.5f)
            lineTo(21f, 15.5f)
            lineTo(19f, 15.5f)
            curveTo(19f, 16.6f, 18.1f, 17.5f, 17f, 17.5f)
            reflectiveCurveTo(15f, 16.6f, 15f, 15.5f)
            lineTo(10f, 15.5f)
            curveTo(10f, 16.6f, 9.1f, 17.5f, 8f, 17.5f)
            reflectiveCurveTo(6f, 16.6f, 6f, 15.5f)
            lineTo(4f, 15.5f)
            close()
            moveTo(8f, 15.5f)
            curveTo(8.55f, 15.5f, 9f, 15.95f, 9f, 16.5f)
            reflectiveCurveTo(8.55f, 17.5f, 8f, 17.5f)
            reflectiveCurveTo(7f, 17.05f, 7f, 16.5f)
            reflectiveCurveTo(7.45f, 15.5f, 8f, 15.5f)
            close()
        }
        // Arrow into cabin
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(1.5f, 7.5f)
            lineTo(10f, 11f)
            moveTo(7.5f, 9.2f)
            lineTo(10.2f, 11.1f)
            lineTo(8.2f, 13.2f)
        }
    }

/** Cabin air recirculation. */
val RecirculationIcon: ImageVector
    get() = cachedIcon("Recirculation") {
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(3.5f, 15f)
            lineTo(5f, 11.5f)
            lineTo(8.5f, 9.5f)
            lineTo(14f, 9.5f)
            lineTo(17.5f, 12f)
            lineTo(20.5f, 12.5f)
            lineTo(20.5f, 15.5f)
            lineTo(18.5f, 15.5f)
            curveTo(18.5f, 16.6f, 17.6f, 17.5f, 16.5f, 17.5f)
            reflectiveCurveTo(14.5f, 16.6f, 14.5f, 15.5f)
            lineTo(9.5f, 15.5f)
            curveTo(9.5f, 16.6f, 8.6f, 17.5f, 7.5f, 17.5f)
            reflectiveCurveTo(5.5f, 16.6f, 5.5f, 15.5f)
            lineTo(3.5f, 15.5f)
            close()
        }
        // Loop arrow inside cabin
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.7f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(15.5f, 11.2f)
            curveTo(16.8f, 11.8f, 17.2f, 13.2f, 16.2f, 14.2f)
            curveTo(15.2f, 15.2f, 11.5f, 15f, 10f, 13.8f)
            moveTo(10.2f, 12.2f)
            lineTo(9.5f, 13.9f)
            lineTo(11.4f, 14.2f)
        }
    }

/** Seat with heat waves. */
val SeatHeatIcon: ImageVector
    get() = cachedIcon("SeatHeat") {
        path(fill = SolidColor(Color.Black)) {
            moveTo(7f, 6f)
            curveTo(7f, 4.9f, 7.9f, 4f, 9f, 4f)
            lineTo(12f, 4f)
            curveTo(13.1f, 4f, 14f, 4.9f, 14f, 6f)
            lineTo(14f, 12f)
            lineTo(17f, 12f)
            curveTo(18.1f, 12f, 19f, 12.9f, 19f, 14f)
            lineTo(19f, 19f)
            lineTo(17f, 19f)
            lineTo(17f, 14.5f)
            lineTo(7f, 14.5f)
            lineTo(7f, 19f)
            lineTo(5f, 19f)
            lineTo(5f, 13f)
            curveTo(5f, 11.9f, 5.9f, 11f, 7f, 11f)
            close()
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
        ) {
            moveTo(15.5f, 5f)
            curveTo(16f, 6f, 15.5f, 7f, 16f, 8f)
            moveTo(17.5f, 4.5f)
            curveTo(18.2f, 5.7f, 17.5f, 6.8f, 18.2f, 8f)
            moveTo(19.5f, 5f)
            curveTo(20f, 6f, 19.5f, 7f, 20f, 8f)
        }
    }

/** Seat with cool air. */
val SeatVentIcon: ImageVector
    get() = cachedIcon("SeatVent") {
        path(fill = SolidColor(Color.Black)) {
            moveTo(7f, 6f)
            curveTo(7f, 4.9f, 7.9f, 4f, 9f, 4f)
            lineTo(12f, 4f)
            curveTo(13.1f, 4f, 14f, 4.9f, 14f, 6f)
            lineTo(14f, 12f)
            lineTo(17f, 12f)
            curveTo(18.1f, 12f, 19f, 12.9f, 19f, 14f)
            lineTo(19f, 19f)
            lineTo(17f, 19f)
            lineTo(17f, 14.5f)
            lineTo(7f, 14.5f)
            lineTo(7f, 19f)
            lineTo(5f, 19f)
            lineTo(5f, 13f)
            curveTo(5f, 11.9f, 5.9f, 11f, 7f, 11f)
            close()
        }
        path(
            fill = SolidColor(Color.Transparent),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.5f,
            strokeLineCap = StrokeCap.Round,
        ) {
            moveTo(16f, 5f)
            lineTo(20f, 5f)
            moveTo(16.5f, 7.2f)
            lineTo(20.5f, 7.2f)
            moveTo(17f, 9.4f)
            lineTo(20.5f, 9.4f)
        }
    }

private val iconCache = mutableMapOf<String, ImageVector>()

private fun cachedIcon(
    name: String,
    builder: ImageVector.Builder.() -> Unit,
): ImageVector = iconCache.getOrPut(name) {
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply(builder).build()
}
