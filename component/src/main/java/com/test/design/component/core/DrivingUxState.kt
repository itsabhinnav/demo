package com.test.design.component.core

/**
 * Mirrors AAOS [CarUxRestrictions] driving states for design-system previews.
 *
 * - [Parked]: full interaction — keyboard, dialogs, fine controls allowed.
 * - [Driving]: glanceable UI per Google Design for Driving — enlarged targets, no keyboard.
 * - [Restricted]: strict UXR — keyboard blocked, secondary actions hidden, lists truncated.
 */
enum class DrivingUxState {
    Parked,
    Driving,
    Restricted,
}
