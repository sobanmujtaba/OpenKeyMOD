package com.atharok.btremote.domain.entities.remoteInput.keyboard

/**
 * Common system administration and shortcut macros for OpenKeyMOD (Openterface KeyMOD compatibility).
 */
enum class SysadminMacro(
    val title: String,
    val description: String,
    val modifierByte: Byte,
    val keyByte: Byte
) {
    CTRL_ALT_DEL(
        title = "Ctrl+Alt+Del",
        description = "Security options / Login",
        modifierByte = (KeyboardKey.KEY_CTRL_LEFT.byte.toInt() or KeyboardKey.KEY_ALT.byte.toInt()).toByte(),
        keyByte = KeyboardKey.KEY_DELETE.byte
    ),
    CTRL_SHIFT_ESC(
        title = "Task Manager",
        description = "Open Task Manager directly",
        modifierByte = (KeyboardKey.KEY_CTRL_LEFT.byte.toInt() or KeyboardKey.KEY_SHIFT_LEFT.byte.toInt()).toByte(),
        keyByte = KeyboardKey.KEY_ESCAPE.byte
    ),
    ALT_TAB(
        title = "Alt+Tab",
        description = "Switch active window",
        modifierByte = KeyboardKey.KEY_ALT.byte,
        keyByte = KeyboardKey.KEY_TAB.byte
    ),
    ALT_F4(
        title = "Alt+F4",
        description = "Close current window",
        modifierByte = KeyboardKey.KEY_ALT.byte,
        keyByte = KeyboardKey.KEY_F4.byte
    ),
    WIN_R(
        title = "Win+R",
        description = "Open Run dialog",
        modifierByte = KeyboardKey.KEY_META_LEFT.byte,
        keyByte = KeyboardKey.ROW_2_KEY_03.byte // 'r'
    ),
    CTRL_C(
        title = "Ctrl+C",
        description = "Copy / Terminal Interruption (SIGINT)",
        modifierByte = KeyboardKey.KEY_CTRL_LEFT.byte,
        keyByte = KeyboardKey.ROW_4_KEY_03.byte // 'c'
    ),
    CTRL_V(
        title = "Ctrl+V",
        description = "Paste",
        modifierByte = KeyboardKey.KEY_CTRL_LEFT.byte,
        keyByte = KeyboardKey.ROW_4_KEY_04.byte // 'v'
    ),
    CTRL_Z(
        title = "Ctrl+Z",
        description = "Undo / Terminal Suspend (SIGTSTP)",
        modifierByte = KeyboardKey.KEY_CTRL_LEFT.byte,
        keyByte = KeyboardKey.ROW_4_KEY_01.byte // 'z'
    );

    val pressedBytes: ByteArray = byteArrayOf(modifierByte, keyByte)
    val releasedBytes: ByteArray = byteArrayOf(0x00, 0x00)
}
