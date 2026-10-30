package com.atharok.btremote.domain.entities.remoteInput.keyboard.virtualKeyboard

import com.atharok.btremote.common.utils.REMOTE_INPUT_NONE
import com.atharok.btremote.domain.entities.remoteInput.keyboard.KeyboardKey

// https://source.android.com/docs/core/interaction/input/keyboard-devices#hid-keyboard-and-keypad-page-0x07
abstract class VirtualKeyboardLayout {
    companion object {
        val KEYBOARD_KEY_ENTER = byteArrayOf(0x00, KeyboardKey.KEY_ENTER.byte)
        val KEYBOARD_KEY_TAB = byteArrayOf(0x00, KeyboardKey.KEY_TAB.byte)
        val KEYBOARD_KEY_BACKSPACE = byteArrayOf(0x00, KeyboardKey.KEY_BACKSPACE.byte)
        val KEYBOARD_KEY_DELETE = byteArrayOf(0x00, KeyboardKey.KEY_DELETE.byte)
        val KEYBOARD_KEY_ESCAPE = byteArrayOf(0x00, KeyboardKey.KEY_ESCAPE.byte)
    }

    private val controlInputs: Map<Char, ByteArray> = mapOf(
        '\n' to KEYBOARD_KEY_ENTER,
        '\r' to KEYBOARD_KEY_ENTER,
        '\t' to KEYBOARD_KEY_TAB
    )

    private val inputs: Map<Char, ByteArray> by lazy { keyboardInputs + extraInputs + controlInputs }

    protected abstract val keyboardInputs: Map<Char, ByteArray>
    protected abstract val extraInputs: Map<Char, ByteArray>

    fun getKeyboardKey(key: Char): ByteArray = inputs[key] ?: REMOTE_INPUT_NONE
}