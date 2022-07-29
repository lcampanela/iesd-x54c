package iesd.jini.helloworldimpl;

import iesd.jini.helloworld.api.HelloWorld;

/**
 * @author iesd
 */
public class HelloWorldImpl implements HelloWorld {

    public HelloWorldImpl() {
        System.out.println("Constructor of HelloWorldImpl()...");
    }

    @Override
    public String sayHello(String msg) {
        System.out.println("In sayHello(): " + msg);
        return msg.toUpperCase();
    }
}
