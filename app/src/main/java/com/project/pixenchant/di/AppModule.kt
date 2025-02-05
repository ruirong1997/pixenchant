package com.project.pixenchant.di

import android.content.Context
import com.project.pixenchant.camera2.manager.CameraManager
import com.project.pixenchant.camera2.model.CameraRepository
import com.project.pixenchant.camera2.model.MediaRepository
import com.project.pixenchant.camera2.renderer.manager.MediaPipeManager
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

    @Provides
    @Singleton
    fun provideMediaRepository(): MediaRepository {
        return MediaRepository()
    }

    @Provides
    @Singleton
    fun provideMediaPipeManager(): MediaPipeManager {
        return MediaPipeManager()
    }
}

