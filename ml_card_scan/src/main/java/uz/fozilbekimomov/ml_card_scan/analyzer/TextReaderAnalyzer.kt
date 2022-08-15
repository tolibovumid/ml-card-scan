package uz.fozilbekimomov.ml_card_scan.analyzer

import android.annotation.SuppressLint
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import uz.fozilbekimomov.ml_card_scan.models.CardResult
import uz.fozilbekimomov.ml_card_scan.utils.validCreditCardNumber
import uz.fozilbekimomov.ml_card_scan.utils.validateCardExpiryDate
import java.io.IOException


class TextReaderAnalyzer(
    private val textDateListener: (String) -> Unit,
    private val textNumberListener: (String) -> Unit,
    private val onCardScanned: (CardResult) -> Unit,
) : ImageAnalysis.Analyzer {

    val TAG = "TextReaderAnalyzer"

    private var cardNumber: String? = null
    private var cardDate: String? = null


    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let { process(it, imageProxy) }
    }

    private fun process(image: Image, imageProxy: ImageProxy) {
        try {
            readTextFromImage(InputImage.fromMediaImage(image, 90), imageProxy)
        } catch (e: IOException) {
            Log.d(TAG, "Failed to load the image")
            e.printStackTrace()
        }
    }

    private fun readTextFromImage(image: InputImage, imageProxy: ImageProxy) {


        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            .process(image)
            .addOnSuccessListener { visionText ->
                processTextFromImage(visionText, imageProxy)
                imageProxy.close()
            }
            .addOnFailureListener { error ->
                Log.d(TAG, "Failed to process the image")
                error.printStackTrace()
                imageProxy.close()
            }
    }

    private fun processTextFromImage(visionText: Text, imageProxy: ImageProxy) {

        val words = visionText.text.split("\n")
        for (word in words) {
            val number = word.replace(" ", "")
            if (number.matches(Regex("(?<!\\d)\\d{16}(?!\\d)"))) {

                if (number.validCreditCardNumber()) {
                    cardNumber = number
                    textNumberListener(number)
                }
            }
            if (word.contains("/")) {
                for (date in word.split(" ")) {
                    if (date.validateCardExpiryDate())
                        cardDate = date
                    textDateListener(date)
                }
            }
        }

        if (cardDate != null && cardNumber != null) {
            onCardScanned(CardResult(cardNumber, cardDate))
        }

    }


}