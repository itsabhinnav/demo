package com.test.design.presentation.ivi.navigation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

private val MapLand = Color(0xFFE8E4D9)
private val MapPark = Color(0xFFC8E6C9)
private val MapWater = Color(0xFFA8D8F0)
private val MapBlock = Color(0xFFD5D0C4)
private val MapRoad = Color(0xFFFFFFFF)
private val MapRoadMajor = Color(0xFFFFF59D)
private val MapRoute = Color(0xFF1A73E8)
private val MapRouteHalo = Color(0xFFFFFFFF)

/**
 * Offline dummy map rendered with Canvas — full-bleed background, no network required.
 */
@Composable
fun DummyMapBackground(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(MapLand)

        drawRoundRect(
            color = MapWater,
            topLeft = Offset(size.width * 0.68f, size.height * 0.05f),
            size = Size(size.width * 0.3f, size.height * 0.42f),
            cornerRadius = CornerRadius(24.dp.toPx()),
        )
        drawRoundRect(
            color = MapPark,
            topLeft = Offset(size.width * 0.04f, size.height * 0.08f),
            size = Size(size.width * 0.22f, size.height * 0.28f),
            cornerRadius = CornerRadius(20.dp.toPx()),
        )

        val blocks = listOf(
            Offset(0.30f, 0.10f) to Size(0.12f, 0.18f),
            Offset(0.44f, 0.12f) to Size(0.10f, 0.14f),
            Offset(0.52f, 0.48f) to Size(0.14f, 0.16f),
            Offset(0.18f, 0.52f) to Size(0.16f, 0.12f),
            Offset(0.70f, 0.55f) to Size(0.12f, 0.20f),
            Offset(0.36f, 0.68f) to Size(0.18f, 0.14f),
        )
        blocks.forEach { (origin, blockSize) ->
            drawRoundRect(
                color = MapBlock,
                topLeft = Offset(size.width * origin.x, size.height * origin.y),
                size = Size(size.width * blockSize.width, size.height * blockSize.height),
                cornerRadius = CornerRadius(6.dp.toPx()),
            )
        }

        fun drawRoad(from: Offset, to: Offset, width: Float, color: Color = MapRoad) {
            val path = Path().apply {
                moveTo(size.width * from.x, size.height * from.y)
                lineTo(size.width * to.x, size.height * to.y)
            }
            drawPath(path, color = color, style = Stroke(width = width, cap = StrokeCap.Round))
        }

        drawRoad(Offset(0f, 0.42f), Offset(1f, 0.42f), 22.dp.toPx(), MapRoadMajor)
        drawRoad(Offset(0.48f, 0f), Offset(0.48f, 1f), 18.dp.toPx())
        drawRoad(Offset(0f, 0.72f), Offset(0.72f, 0.72f), 16.dp.toPx())
        drawRoad(Offset(0.22f, 0.2f), Offset(0.62f, 0.58f), 12.dp.toPx())

        val route = Path().apply {
            moveTo(size.width * 0.14f, size.height * 0.76f)
            cubicTo(
                size.width * 0.30f, size.height * 0.62f,
                size.width * 0.40f, size.height * 0.50f,
                size.width * 0.50f, size.height * 0.44f,
            )
            cubicTo(
                size.width * 0.62f, size.height * 0.36f,
                size.width * 0.56f, size.height * 0.22f,
                size.width * 0.48f, size.height * 0.18f,
            )
        }
        drawPath(route, color = MapRouteHalo, style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round))
        drawPath(route, color = MapRoute, style = Stroke(width = 9.dp.toPx(), cap = StrokeCap.Round))

        val destination = Offset(size.width * 0.48f, size.height * 0.18f)
        drawCircle(MapRoute, radius = 14.dp.toPx(), center = destination)
        drawCircle(Color.White, radius = 6.dp.toPx(), center = destination)

        val origin = Offset(size.width * 0.14f, size.height * 0.76f)
        drawCircle(Color(0xFF34A853), radius = 10.dp.toPx(), center = origin)
        drawCircle(Color.White, radius = 4.dp.toPx(), center = origin)
    }
}
