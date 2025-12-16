#include <jni.h>
#include <string>
#include <android/bitmap.h> // Indispensable pour lire les images
#include <android/log.h>

// Cette fonction est appelée directement depuis Kotlin
// Elle renvoie 'true' si l'image est claire, 'false' si elle est trop sombre
extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_tessilite_MainActivity_checkBrightnessNative(
        JNIEnv* env,
        jobject /* this */,
        jobject bitmap) { // On reçoit l'image (Bitmap) ici

    AndroidBitmapInfo info;
    void* pixels;
    int ret;

    // 1. On récupère les infos de l'image (largeur, hauteur...)
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        return false; // Erreur
    }

    // 2. On "verrouille" les pixels pour pouvoir les lire en mémoire
    // C'est ici que C++ accède à la mémoire brute de l'image
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        return false; // Erreur
    }

    // 3. On parcourt les pixels pour calculer la luminosité moyenne
    uint32_t* src = (uint32_t*) pixels;
    int totalPixels = info.width * info.height;
    long totalBrightness = 0;

    for (int i = 0; i < totalPixels; i++) {
        uint32_t pixel = src[i];

        // On extrait le Rouge, Vert, Bleu de chaque pixel
        // (Décalage de bits classique en traitement d'image)
        int r = (pixel & 0x00FF0000) >> 16;
        int g = (pixel & 0x0000FF00) >> 8;
        int b = (pixel & 0x000000FF);

        // Moyenne simple
        int brightness = (r + g + b) / 3;
        totalBrightness += brightness;
    }

    // 4. On déverrouille les pixels (Très important pour libérer la mémoire !)
    AndroidBitmap_unlockPixels(env, bitmap);

    // 5. Calcul final
    if (totalPixels == 0) return false;
    int averageBrightness = totalBrightness / totalPixels;

    // Si la luminosité est inférieure à 50 (sur 255), c'est trop sombre !
    return averageBrightness > 50;
}