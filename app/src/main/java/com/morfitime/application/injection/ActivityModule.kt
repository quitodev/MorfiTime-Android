package com.morfitime.application.injection

import com.morfitime.domain.place.account.AccountImpl
import com.morfitime.domain.place.account.IAccountRepo
import com.morfitime.domain.place.booking.BookingImpl
import com.morfitime.domain.place.booking.IBookingRepo
import com.morfitime.domain.login.ILoginRepo
import com.morfitime.domain.login.LoginImpl
import com.morfitime.domain.maps.account.IMapsAccountRepo
import com.morfitime.domain.maps.account.MapsAccountImpl
import com.morfitime.domain.place.home.HomeImpl
import com.morfitime.domain.place.home.IHomeRepo
import com.morfitime.domain.maps.maps.IMapsRepo
import com.morfitime.domain.maps.maps.MapsImpl
import com.morfitime.domain.place.ubication.IUbicationRepo
import com.morfitime.domain.place.ubication.UbicationImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@InstallIn(ActivityRetainedComponent::class)
@ExperimentalCoroutinesApi
abstract class ActivityModule {
    @ActivityRetainedScoped
    @Binds
    abstract fun bindLoginDataSource(loginImpl: LoginImpl) : ILoginRepo
    @ActivityRetainedScoped
    @Binds
    abstract fun bindMapsDataSource(mapsImpl: MapsImpl) : IMapsRepo
    @ActivityRetainedScoped
    @Binds
    abstract fun bindMapsAccountDataSource(mapsAccountImpl: MapsAccountImpl) : IMapsAccountRepo
    @ActivityRetainedScoped
    @Binds
    abstract fun bindHomeDataSource(homeImpl: HomeImpl) : IHomeRepo
    @ActivityRetainedScoped
    @Binds
    abstract fun bindBookingDataSource(bookingImpl: BookingImpl) : IBookingRepo
    @ActivityRetainedScoped
    @Binds
    abstract fun bindUbicationDataSource(ubicationImpl: UbicationImpl) : IUbicationRepo
    @ActivityRetainedScoped
    @Binds
    abstract fun bindAccountDataSource(accountImpl: AccountImpl) : IAccountRepo
}
