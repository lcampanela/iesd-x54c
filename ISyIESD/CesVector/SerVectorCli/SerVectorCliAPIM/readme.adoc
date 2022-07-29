= SerVectorCliAPIM

The main objective is to generate the proxy classes and interfaces to access the service SerVector and make them available to SerVectorCliOPE cliente example.


Important note: before the generation of this artifact (mvn install) start the service vector at SerVectorOPE, since wsimport in the jaxws-maven-plugin of the POM file is configured to run with a reference to the running service "<wsdlUrl>http://localhost:2058/Vector?wsdl</wsdlUrl>"

To generate the Asynchronous web service call the plugin configuration requires a configuration in the "<bindingDirectory>${basedir}/src/main/resources/jaxws</bindingDirectory>" directory. The async-bidings.xml sets enableAsyncMapping to true when the wsdl interface is accessed at the address: http://localhost:2058/Vector?wsdl

WIth the asynchronous access method the client delegates the answer based on the Future mechanism managed by the jax-ws runtime. The example at https://examples.javacodegeeks.com/wp-content/uploads/2018/02/jax-ws-callback.zip[link] can be used as a reference. Further details can be obtained from the jax-ws reference documentation https://jcp.org/aboutJava/communityprocess/mrel/jsr224/index5.html[link]. You can access further information and documentation at the following addresses:
* Apache CXF, Using the JAX-WS https://cxf.apache.org/docs/jax-ws.html[link]
* Apache Axis, JAX-WS Guide http://svn.apache.org/repos/asf/axis/axis2/java/core/tags/v1.4.1/modules/documentation/xdocs/@axis2_version_dir@/jaxws-guide.xml[link], and https://axis.apache.org/axis2/java/core/docs/jaxws-guide.html[link]
* Java Enterprise Edition (JEE) JAX-WS https://javaee.github.io/metro-jax-ws/doc/user-guide/release-documentation.html[link] and https://javaee.github.io/metro-jax-ws/doc/user-guide/release-documentation.html[link]
* Spring Web Services https://docs.spring.io/spring-ws/docs/current/reference/[link], with attention for differences from the standard.

Another important documentation is the jaxws-maven-plugin being developed by the org.codehaus.mojo project (Artifact Group) https://www.mojohaus.org/jaxws-maven-plugin/[link].


