package com.project.pixenchant.di

import android.content.Context
import com.project.pixenchant.camera2.manager.CameraManager
import com.project.pixenchant.camera2.model.CameraRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CameraModule {


    @Provides
    @Singleton
    fun provideCameraRepository(cameraManager: CameraManager): CameraRepository {
        return CameraRepository(cameraManager)
    }
}

