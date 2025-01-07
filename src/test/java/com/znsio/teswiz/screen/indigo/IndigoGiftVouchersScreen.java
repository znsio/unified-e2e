package com.znsio.teswiz.screen.indigo;

import com.znsio.teswiz.entities.Platform;
import com.znsio.teswiz.runner.Driver;
import com.znsio.teswiz.runner.Drivers;
import com.znsio.teswiz.runner.Runner;
import com.znsio.teswiz.runner.Visual;
import com.znsio.teswiz.screen.android.indigo.IndigoGiftVouchersScreenAndroid;
import com.znsio.teswiz.screen.web.indigo.IndigoGiftVouchersScreenWeb;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class IndigoGiftVouchersScreen {
    private static final String SCREEN_NAME = IndigoGiftVouchersScreen.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(SCREEN_NAME);

    public static IndigoGiftVouchersScreen get() {
        Driver driver = Drivers.getDriverForCurrentUser(Thread.currentThread().getId());
        Platform platform = Runner.fetchPlatform(Thread.currentThread().getId());
        LOGGER.info(SCREEN_NAME + ": Driver type: " + driver.getType() + ": Platform: " + platform);
        Visual visually = Drivers.getVisualDriverForCurrentUser(Thread.currentThread().getId());

        switch (platform) {
            case android:
                return new IndigoGiftVouchersScreenAndroid(driver, visually);
            case web:
                return new IndigoGiftVouchersScreenWeb(driver, visually);
        }
        throw new NotImplementedException(
                SCREEN_NAME + " is not implemented in " + Runner.getPlatform());
    }

    public abstract IndigoGiftVouchersScreen select(String numberOfGiftVouchersToPurchase,
            String denomination);

    public abstract int getTotalPrice();

    public abstract IndigoGiftVouchersScreen select(String numberOfGiftVouchersToPurchase,
            String denomination, String forWhom,
            String customMessage);

    public abstract IndigoGiftVouchersScreen preview();
}
