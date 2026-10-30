<p align="center">
  <img src="fastlane/metadata/android/en-US/images/icon.png" width="128" alt="OpenKeyMOD icon">
</p>

<h1 align="center">OpenKeyMOD</h1>

<p align="center">
  <strong>Turn your Android phone into a Bluetooth keyboard, mouse, trackpad, and macro pad.</strong>
</p>

<p align="center">
  <a href="https://github.com/sobanmujtaba/OpenKeyMOD/releases"><img src="https://img.shields.io/badge/Download-APK-2ea44f" alt="Download APK"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-GPLv3-blue.svg" alt="License: GPLv3"></a>
  <img src="https://img.shields.io/badge/Android-9.0%2B-3ddc84" alt="Android 9.0+">
</p>

> **OpenKeyMOD is not on the Google Play Store.** It is distributed as a direct APK download through [GitHub Releases](https://github.com/sobanmujtaba/OpenKeyMOD/releases).

OpenKeyMOD makes an Android device appear as a standard Bluetooth HID peripheral. Pair it once, and it acts as a keyboard, mouse, trackpad, media remote, and shortcut pad. No companion app or driver is required on the target device.

It is a cross-platform reinterpretation of the original [BT Remote](https://gitlab.com/Atharok/BtRemote) for Android TV, built on the Bluetooth HID work of [jqssun's fork](https://github.com/jqssun/android-bt-remote) and extended by [Soban Mujtaba](https://github.com/sobanmujtaba).

<video loop src="https://github.com/user-attachments/assets/9bddf132-a861-4693-b408-2539646b7e8b" alt="OpenKeyMOD demo" width="100%" style="display: block; margin: auto;"></video>

## What it does

- **Keyboard, mouse, and trackpad** over Bluetooth Classic HID.
- **Media and navigation controls**, including a TV-friendly D-pad layout.
- **Shortcut Hub** for reusable text snippets, runbooks, and macros.
- **Compose & Send** for entering longer text from your phone.
- **Numpad, function keys, and presentation controls**.
- **No target-side software.** The host sees a normal Bluetooth keyboard/mouse.
- **No internet permission, no ads, and no tracking.**
- **Open source** under GPLv3.

## Compatibility

OpenKeyMOD sends standard Bluetooth HID keyboard, mouse, and consumer-control reports. It has been tested with a **Linux** host.

Hosts that accept generic Bluetooth keyboards and mice may also work, including Windows, macOS, Android, ChromeOS, and some TV devices, but they have not all been verified yet. If you test one, please share the result in [Issues](https://github.com/sobanmujtaba/OpenKeyMOD/issues).

## Install

1. Download the latest APK from [GitHub Releases](https://github.com/sobanmujtaba/OpenKeyMOD/releases).
2. Allow installing apps from your browser or file manager when prompted.
3. Open OpenKeyMOD, grant the Bluetooth permission, and pair with your target device.
4. Start typing, pointing, and sending shortcuts.

There is currently **no Google Play release**. GitHub Releases and source builds are the supported distribution channels.

## Project status

OpenKeyMOD is under active development. Core keyboard, mouse, trackpad, media, and shortcut features work, but Bluetooth pairing behavior can vary between hosts. Bug reports and host compatibility notes are welcome in [Issues](https://github.com/sobanmujtaba/OpenKeyMOD/issues).

## Build from source

Open the project in Android Studio, or build from the command line:

```bash
git clone https://github.com/sobanmujtaba/OpenKeyMOD.git
cd OpenKeyMOD
./gradlew :app:assembleDefaultRelease
```

The release APK is written to `app/build/outputs/apk/default/release/app-default-release.apk`.

## Credits

- **[Atharok](https://gitlab.com/Atharok/BtRemote)** — creator of the original BT Remote, the Android TV Bluetooth HID remote this project is based on.
- **[jqssun](https://github.com/jqssun/android-bt-remote)** — cross-platform HID fork that OpenKeyMOD builds upon.
- **[Soban Mujtaba](https://github.com/sobanmujtaba)** — OpenKeyMOD fork, UI, shortcuts, and release packaging.

## License

OpenKeyMOD is licensed under the [GNU General Public License v3.0](LICENSE), the same license as the upstream project. If you distribute a modified build, you must also make the corresponding source available under GPLv3.
