package uks.seminar.models

import kotlinx.serialization.Serializable

@Serializable
class Coordinate(
    val x: Float,
    val y: Float
) {
    override fun toString(): String {
        return "($x, $y)"
    }
}

@Serializable
data class SineDTO(
    val x: Float
)
