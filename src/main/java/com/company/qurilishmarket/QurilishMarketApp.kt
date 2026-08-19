package com.company.qurilishmarket

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Hilt uchun kirish nuqtasi — shu klassdan butun ilova bo'ylab DI graph generatsiya qilinadi.
 * Boshqa hech narsa yozish shart emas, faqat annotatsiya kifoya.
 */
@HiltAndroidApp
class QurilishMarketApp : Application()
