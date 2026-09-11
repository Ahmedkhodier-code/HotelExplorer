package com.khodier.hotelexplorer.core.common.dispatcher

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DefaultCoroutineDispatchers @Inject constructor(): CoroutineDispatchers {
    override val io = Dispatchers.IO
    override val main = Dispatchers.Main
    override val default = Dispatchers.Default
}