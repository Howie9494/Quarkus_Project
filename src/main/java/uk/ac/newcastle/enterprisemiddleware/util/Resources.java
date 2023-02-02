package uk.ac.newcastle.enterprisemiddleware.util;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import java.util.logging.Logger;

/**
 * This class uses CDI to alias Java EE resources, such as the persistence context, to CDI beans
 *
 * <p>
 * Example injection on a managed bean field:
 * </p>
 *
 * <pre>
 * &#064;Inject
 * private EntityManager em;
 * </pre>
 */
public class Resources {
//
//    @Produces
//    @PersistenceContext(unitName = "contacts_pu")
//    private EntityManager em;

    @Produces
    @Named("logger")
    public Logger produceLog(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
//
//    @Produces
//    @Named("mapper")
//    public ObjectMapper produceMapper() {
//        return new ObjectMapper();
//    }

}

