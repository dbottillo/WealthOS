package com.wealthos.common

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(baseUrl: String = "", appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(commonModule(baseUrl))
    }

fun commonModule(baseUrl: String) = module {
    single { WealthOsClient(baseUrl) }
    single { PeriodRepository(get()) }
    factory { SpendingPeriodViewModel(get(), get()) }
}
