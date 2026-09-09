[app]
title = Deepak Sir Nursing
package.name = deepaksir
package.domain = com.deepaksir
source.dir = .
source.include_exts = py,png,jpg,kv,atlas,ttf,json
version = 1.0.0
requirements = python3,kivy==2.3.1,kivymd==1.1.1,requests==2.31.0,httpx==0.25.0,plyer==2.1.0,pillow==10.0.0
orientation = portrait
fullscreen = 0
icon.filename = %(source.dir)s/app/assets/icon.png

[android]
permissions = INTERNET,ACCESS_NETWORK_STATE,CAMERA,READ_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE,READ_MEDIA_IMAGES,POST_NOTIFICATIONS,VIBRATE
android.api = 31
android.minapi = 21
android.ndk = 25b
android.arch = arm64-v8a,armeabi-v7a
android.entrypoint = org.kivy.android.PythonActivity
android.private_storage = True
android.presplash_color = #1A73E8

[python]
source.include_py = main.py

[buildozer]
log_level = 2
warn_on_root = 1