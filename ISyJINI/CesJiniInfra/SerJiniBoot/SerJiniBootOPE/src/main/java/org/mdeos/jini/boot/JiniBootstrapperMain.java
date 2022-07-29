package org.mdeos.jini.boot;

import com.sun.jini.start.ServiceStarter;
import org.mdeos.jini.helper.ConfigureJiniFramework;

public class JiniBootstrapperMain {

    public static void main(String[] args) {

        ConfigureJiniFramework.setupLoggingConfig();

        try {
            System.out.println("HOSTNAME: " + ConfigureJiniFramework.getIpv4NonLoopbackHostName());
            //EXTRACT EMBEDDED DIRETORY 'config'
            ConfigureJiniFramework.extractEmbeddedFilesToFileSystem(
                    ConfigureJiniFramework.JINI_CONFDIR,
                    ConfigureJiniFramework.JINI_RUNTIME_CONFDIR);

            //EXTRACT EMBEDDED DIRETORY 'www'
            ConfigureJiniFramework.extractEmbeddedFilesToFileSystem(
                    "www",
                    ConfigureJiniFramework.JINI_RUNTIME_CODEBASE_DIR);

            ConfigureJiniFramework.setSecurity(
                    ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/all.policy");

            //START
            String[] configOptions = {ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/StartJiniSysServices.config"};
            ConfigureJiniFramework.enableRemoteCodebase();
            initServices(configOptions);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void initServices(String[] options) {
        System.out.println("Before ServiceStarter.main..." + options);
        ServiceStarter.main(options);
        System.out.println("Services StartJiniSysServices, ready...");
    }
}
