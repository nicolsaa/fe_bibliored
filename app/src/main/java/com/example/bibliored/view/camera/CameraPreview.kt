package com.example.bibliored.view.camera

import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@Composable
fun CameraPreview(
    onQrScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isScanning by remember { mutableStateOf(true) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val provider = cameraProviderFuture.get()
                cameraProvider = provider

                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val selector = CameraSelector.DEFAULT_BACK_CAMERA
                val scanner = BarcodeScanning.getClient()

                val analysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { imageAnalysis ->
                        imageAnalysis.setAnalyzer(
                            ContextCompat.getMainExecutor(ctx)
                        ) { imageProxy ->

                            if (!isScanning) {
                                imageProxy.close()
                                return@setAnalyzer
                            }

                            processImageProxy(
                                imageProxy = imageProxy,
                                scanner = scanner
                            ) { valorBarcode ->
                                // Primer código de barras encontrado
                                isScanning = false
                                cameraProvider?.unbindAll()   // apaga la cámara
                                onQrScanned(valorBarcode)
                            }
                        }
                    }

                try {
                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        analysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }
}



@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    scanner: BarcodeScanner,
    onQrFound: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue
                    if (!rawValue.isNullOrBlank()) {
                        onQrFound(rawValue)
                        break // Procesar solo el primer código de barras válido encontrado
                    }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
