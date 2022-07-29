package org.mdeos.jini.boot;

import com.sun.jini.start.ServiceStarter;
import org.mdeos.jini.helper.ConfigureJiniFramework;

public class JiniHttpServerMain {

    public static void main(String[] args) {

        ConfigureJiniFramework.setupLoggingConfig();

        try {
            //EXTRACT EMBEDDED DIRETORY 'config'
            ConfigureJiniFramework.extractEmbeddedFilesToFileSystem(
                    ConfigureJiniFramework.JINI_CONFDIR,
                    ConfigureJiniFramework.JINI_RUNTIME_CONFDIR);

            //EXTRACT EMBEDDED DIRETORY 'www'
            ConfigureJiniFramework.extractEmbeddedFilesToFileSystem(
                    "www",
                    ConfigureJiniFramework.JINI_RUNTIME_BASEPATH + "/www");

            ConfigureJiniFramework.setSecurity(
                    ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/all.policy");

            //START
            String[] configOptions = {ConfigureJiniFramework.JINI_RUNTIME_CONFDIR + "/StartJiniSysServicesHttp.config"};

            initServices(configOptions);

        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public static void initServices(String[] options) {
        System.out.println("Before ServiceHttpStarter.main..." + options);
        ServiceStarter.main(options);
        System.out.println("Service StartJiniSysServicesHttp, ready...");
    }
}
