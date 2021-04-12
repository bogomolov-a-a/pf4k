package org.artembogomolova.pf4k.impl.launcher.test

import org.artembogomolova.pf4k.api.module.management.types.ApplicationDescriptor
import org.artembogomolova.pf4k.impl.app.DefaultModularizedApplication
import org.artembogomolova.pf4k.impl.launcher.DefaultApplicationLauncher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.MockitoSession
import org.mockito.kotlin.any
import org.mockito.kotlin.anyArray
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class DefaultApplicationLauncherTest {

    companion object {
        const val TEST_APPLICATION_NAME: String = "test-application"
        val APPLICATION_IMPL_CLASS_NAME: String = DefaultModularizedApplication::class.java.name
    }

    private lateinit var session: MockitoSession
    private lateinit var applicationLauncher: DefaultApplicationLauncher

    @BeforeEach
    fun init() {
        session = Mockito.mockitoSession().initMocks(this).startMocking()
        applicationLauncher = mock(defaultAnswer = Mockito.CALLS_REAL_METHODS)
        doReturn(ApplicationDescriptor(TEST_APPLICATION_NAME, APPLICATION_IMPL_CLASS_NAME))
            .whenever(applicationLauncher)
            .getApplicationDescriptor()
        doNothing().whenever(applicationLauncher)
            .runApplicationWithArgs(any(), any(), anyArray())

    }

    @Test
    fun withNoArgsExec() {
        applicationLauncher.mainMethod(arrayOf())
    }

    @AfterEach
    fun release() {
        session.finishMocking()
    }
}