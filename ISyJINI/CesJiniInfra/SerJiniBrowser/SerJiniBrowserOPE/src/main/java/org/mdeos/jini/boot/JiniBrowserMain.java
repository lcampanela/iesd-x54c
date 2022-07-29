package org.mdeos.jini.boot;

import org.apache.river.examples.browser.Browser;
import org.mdeos.jini.helper.ConfigureJiniFramework;

public class JiniBrowserMain {

    public static void main(String[] args) {

        ConfigureJiniFramework.setupLoggingConfig();

        try {
            ConfigureJiniFramework.setSecurity(
                    ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/JiniBrowser.policy");

            //START
            initServices(ConfigureJiniFramework.JINI_RUNTIME_CONFDIR);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void initServices(String configDir) {
        System.out.println("Starting JiniBrowser...");
        String[] args = null;

        // Start example Browser
        Browser.main(args);
    }
}
