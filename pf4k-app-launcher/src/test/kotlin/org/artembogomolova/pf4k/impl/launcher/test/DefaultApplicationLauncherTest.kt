package org.artembogomolova.pf4k.impl.launcher.test

import org.artembogomolova.pf4k.api.module.management.types.ApplicationDescriptor
import org.artembogomolova.pf4k.impl.app.DefaultModularizedApplication
import org.artembogomolova.pf4k.impl.launcher.DefaultApplicationLauncher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.MockitoSession
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

internal class DefaultApplicationLauncherTest {

    private lateinit var session: MockitoSession

    @BeforeEach
    fun init() {
        session = Mockito.mockitoSession().initMocks(this).startMocking()
    }

    @Test
    fun withNoArgsExec() {
        val application = mock<DefaultApplicationLauncher>(defaultAnswer = Mockito.CALLS_REAL_METHODS)
        doReturn(ApplicationDescriptor("test-application", DefaultModularizedApplication::class.java.name))
            .whenever(application)
            .getApplicationDescriptor()

        application.mainMethod(arrayOf())
    }

    @AfterEach
    fun release() {
        session.finishMocking()
    }
}