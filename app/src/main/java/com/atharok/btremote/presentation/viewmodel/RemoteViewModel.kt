package com.atharok.btremote.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atharok.btremote.common.utils.DELAY_BETWEEN_KEY_PRESSES_IN_MILLIS
import com.atharok.btremote.common.utils.KEYBOARD_REPORT_ID
import com.atharok.btremote.common.utils.MOUSE_REPORT_ID
import com.atharok.btremote.common.utils.REMOTE_REPORT_ID
import com.atharok.btremote.domain.entities.remoteInput.MouseAction
import com.atharok.btremote.domain.entities.remoteInput.keyboard.KeyboardKey
import com.atharok.btremote.domain.entities.remoteInput.keyboard.SysadminMacro
import com.atharok.btremote.domain.entities.remoteInput.keyboard.virtualKeyboard.VirtualKeyboardLayout
import com.atharok.btremote.domain.entities.settings.RemoteSettings
import com.atharok.btremote.domain.usecases.RemoteUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class RemoteViewModel(
    private val useCase: RemoteUseCase
): ViewModel() {

    // ---- Settings ----

    val remoteSettingsFlow: Flow<RemoteSettings> get() = useCase.remoteSettingsFlow

    // ---- Connection ----

    fun disconnectDevice(): Boolean = useCase.disconnectDevice()

    // ---- Send ----

    fun sendReport(id: Int, bytes: ByteArray): Boolean {
        return useCase.sendReport(id, bytes)
    }

    // Remote
    val sendRemoteReport: (ByteArray) -> Unit = { bytes -> sendReport(REMOTE_REPORT_ID, bytes) }

    // Mouse
    val sendMouseReport: (MouseAction, Float, Float, Float) -> Unit = { input, x, y, wheel ->
        val xInt = x.roundToInt().coerceIn(-127, 127)
        val yInt = y.roundToInt().coerceIn(-127, 127)
        val bytes: ByteArray = byteArrayOf(input.byte, xInt.toByte(), yInt.toByte(), wheel.roundToInt().toByte())
        sendReport(MOUSE_REPORT_ID, bytes)
    }

    // Keyboard
    val sendKeyboardReport: (ByteArray) -> Unit = { bytes -> sendReport(KEYBOARD_REPORT_ID, bytes) }

    fun sendKey(key: KeyboardKey) {
        sendKeyboardReport(byteArrayOf(0x00, key.byte))
        sendKeyboardReport(byteArrayOf(0x00, 0x00))
    }

    // Macro
    fun sendMacro(macro: SysadminMacro) {
        sendKeyboardReport(macro.pressedBytes)
        viewModelScope.launch(Dispatchers.Default) {
            delay(DELAY_BETWEEN_KEY_PRESSES_IN_MILLIS)
            sendKeyboardReport(macro.releasedBytes)
        }
    }

    // Text (Keyboard)
    private var sendTextJob: Job? = null

    val sendTextReport: (String, VirtualKeyboardLayout, Boolean) -> Unit = { text, virtualKeyboardLayout, shouldSendEnter ->
        sendText(text, virtualKeyboardLayout, shouldSendEnter, DELAY_BETWEEN_KEY_PRESSES_IN_MILLIS)
    }

    fun sendText(
        text: String,
        virtualKeyboardLayout: VirtualKeyboardLayout,
        shouldSendEnter: Boolean = false,
        delayMillis: Long = DELAY_BETWEEN_KEY_PRESSES_IN_MILLIS
    ) {
        if(sendTextJob?.isActive != true) {
            sendTextJob = viewModelScope.launch(Dispatchers.Default) {
                useCase.sendTextReport(text, virtualKeyboardLayout, shouldSendEnter, delayMillis)
            }
        }
    }
}