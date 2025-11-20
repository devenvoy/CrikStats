
package com.devansh.crikstats.di

import com.devansh.crikstats.data.repository.CricketRepository

//@EntryPoint
//@InstallIn(SingletonComponent::class)
interface FeaturePlayerDependencies {
    fun cricketRepository(): CricketRepository
}