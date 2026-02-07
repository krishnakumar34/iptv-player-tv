#!/bin/bash

echo "======================================"
echo "IPTV Player - Build Script"
echo "======================================"
echo ""

# Check if we're in the right directory
if [ ! -f "settings.gradle" ]; then
    echo "Error: Not in project root directory"
    exit 1
fi

# Make gradlew executable
chmod +x gradlew

echo "Building APK..."
echo ""

# Build debug APK
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "======================================"
    echo "Build Successful!"
    echo "======================================"
    echo ""
    echo "APK Location:"
    echo "  Debug: app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "To install on device:"
    echo "  adb install app/build/outputs/apk/debug/app-debug.apk"
    echo ""
else
    echo ""
    echo "======================================"
    echo "Build Failed!"
    echo "======================================"
    exit 1
fi
