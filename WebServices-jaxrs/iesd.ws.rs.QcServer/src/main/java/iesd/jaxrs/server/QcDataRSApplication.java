package iesd.jaxrs.server;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import iesd.jaxrs.server.resources.ReadWriteQcDataRS;

@ApplicationPath(QcDataRSApplication.BASE_PATH)
public class QcDataRSApplication extends Application {
	
    public static final String BASE_PATH = "iesd";
    private static final Set<Class<?>> RESOURCES_CLASSES = new java.util.HashSet<Class<?>>();   

    static {
        RESOURCES_CLASSES.add(ReadWriteQcDataRS.class);
//        RESOURCES_CLASSES.add(AuthResource.class);
//        RESOURCES_CLASSES.add(VehicleResource.class);
    }
    @Override
    public Set<Class<?>> getClasses() {
        return RESOURCES_CLASSES;
    }
}
