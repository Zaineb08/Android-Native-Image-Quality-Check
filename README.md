# Mobile Document Quality Check (POC)

A native Android proof of concept designed to perform **on-device pre-validation of identity documents (KYC)** using edge computing principles.

The application captures an image via the device camera, converts it into a safe and mutable format, and processes it through a **C++ native library** to perform real-time pixel luminosity analysis. This simulates an initial document quality and validity check prior to any server-side processing.

## Key Features

- Edge-based document validation executed entirely on-device
- Hybrid architecture combining Kotlin (UI, camera, system logic) and C++ (native image processing)
- High-performance pixel analysis via NDK/JNI with direct memory access
- Safe handling of hardware bitmaps using a defensive `Bitmap.copy()` strategy
- Fully compliant with Android runtime permission policies (Android 14 ready)

## Tech Stack

- **Languages:** Kotlin, C++ (C++17)
- **Native Interface:** JNI (Java Native Interface)
- **Camera API:** Android Activity Result API (Intent-based)
- **Build System:** CMake
- **IDE:** Android Studio Ladybug / Koala

## Project Structure

- `cpp/native-lib.cpp`  
  Core native image processing and luminosity analysis logic

- `MainActivity.kt`  
  Camera lifecycle management, runtime permissions, and JNI bridge

- `CMakeLists.txt`  
  Native build and NDK configuration

## Screenshots

**Low-light detection (invalid capture):**

<img width="1080" height="2400" alt="Low-light image rejected" src="https://github.com/user-attachments/assets/6e12eda5-ebfa-40a9-9d9b-a25220f7b42e" />

**Valid image detection (accepted capture):**

<img width="1080" height="2400" alt="Valid image accepted" src="https://github.com/user-attachments/assets/7cb9d5ee-ad80-499f-86c5-f30c75073790" />

## Notes

This project focuses on **low-level image handling and native performance** rather than machine learning. It is intended as a foundational component for future document validation, OCR, or Edge AI pipelines.

---

Developed as a technical Proof of Concept for research and development in **document processing automation and on-device KYC systems**.
