package edu.moravian.csci215.misophoniaapp.headphones_data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * A pair of headphones the user has added. Everything except [name] comes
 * from the headphones themselves, so null means "not known yet".
 */
data class Headphones(
    val id: String,
    val name: String,
    val uuid: String? = null,
    val version: String? = null,
    val batteryPercent: Int? = null,
    val macAddress: String? = null,
    val lastServerConnection: String? = null,
    val lastPhoneConnection: String? = null,
)

/**
 * Holds the user's headphones for the headphone screens.
 *
 * todo: this only lives in memory for now. Fill the read-only fields from
 * the BLE connection and persist the list once headphone pairing exists.
 */
class HeadphonesRepository {
    private val _headphones: SnapshotStateList<Headphones> = mutableStateListOf()
    val headphones: List<Headphones> get() = _headphones

    fun get(id: String): Headphones? = _headphones.firstOrNull { it.id == id }

    @OptIn(ExperimentalUuidApi::class)
    fun add(): Headphones {
        val newHeadphones = Headphones(
            id = Uuid.random().toString(),
            name = "Headphones ${_headphones.size + 1}",
        )
        _headphones.add(newHeadphones)
        return newHeadphones
    }

    fun rename(id: String, newName: String) {
        val index = _headphones.indexOfFirst { it.id == id }
        if (index != -1) {
            _headphones[index] = _headphones[index].copy(name = newName)
        }
    }

    fun delete(id: String) {
        _headphones.removeAll { it.id == id }
    }
}
