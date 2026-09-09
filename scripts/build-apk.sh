#!/bin/bash
echo "Building Deepak Sir Android APK..."
cd ../frontend
buildozer android clean
buildozer android debug
echo "APK built at: frontend/bin/deepaksir-1.0.0-debug.apk"