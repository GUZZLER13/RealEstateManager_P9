package com.example.realestatemanager;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;

import com.example.realestatemanager.utils.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UtilsInstrumentedTest {

    Context appContext;
    private final UiDevice device = UiDevice.getInstance(getInstrumentation());

    @Before
    public void useAppContext() {
        // Context of the app under test.
        appContext = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void checkWithNetworkOff() throws IOException, InterruptedException {
        device.executeShellCommand("svc wifi disable");
        device.executeShellCommand("svc data disable");
        Thread.sleep(3000);
        assertFalse(Utils.checkInternetConnection(appContext));
    }

    @Test
    public void checkIsConnected() throws IOException, InterruptedException {
        device.executeShellCommand("svc wifi enable");
        device.executeShellCommand("svc data enable");
        Thread.sleep(2000);
        assertTrue(Utils.checkInternetConnection(appContext));
    }

    @After
    public void enableNetwork() throws IOException {
        device.executeShellCommand("svc wifi enable");
        device.executeShellCommand("svc data enable");
    }
}
