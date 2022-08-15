package uz.fozilbekimomov.ml_card_scan.listeners

import android.content.Context
import android.view.View

interface ScanOptions {
    fun getCustomDecoration(context: Context): View
    fun getScreenTitle():String
    fun getDelayAfterScan():Long
}