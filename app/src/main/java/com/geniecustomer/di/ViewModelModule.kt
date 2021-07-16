package com.geniecustomer.di

import com.geniecustomer.viewmodels.*
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewmodelModule: Module = module {
    viewModel { SignupViewModel(get()) }
    viewModel { OtpViewModel(get()) }
    viewModel { UpdateprofileViewModel(get()) }
    viewModel { SigninViewModel(get()) }
    viewModel { GetDashboardDataViewModel(get()) }
    viewModel { FliterDataViewModel(get()) }
    viewModel { BookingViewModel(get()) }
    viewModel { BookingHistoryDetailViewModel(get()) }
    viewModel { GenerateInvoiceViewModel(get()) }
    viewModel { InitializeGatewayViewModel(get()) }
    viewModel { BankListViewModel(get()) }
}

