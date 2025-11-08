package com.elab.amortizaplus

import android.app.Application
import com.elab.amortizaplus.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AmortizaPlusApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Inicializa o Koin (injeção de dependência)
        startKoin {
            androidContext(this@AmortizaPlusApp)
            modules(appModule)
        }
    }
}