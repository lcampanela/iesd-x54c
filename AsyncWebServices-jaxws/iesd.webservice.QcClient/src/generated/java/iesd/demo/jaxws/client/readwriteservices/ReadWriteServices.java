
package iesd.demo.jaxws.client.readwriteservices;

import java.util.concurrent.Future;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.Response;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "ReadWriteServices", targetNamespace = "http://iesd.demo.async.ws")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ReadWriteServices {


    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns javax.xml.ws.Response<iesd.demo.jaxws.client.readwriteservices.WriteResponse>
     */
    @WebMethod(operationName = "write")
    @RequestWrapper(localName = "write", targetNamespace = "http://iesd.demo.async.ws", className = "iesd.demo.jaxws.client.readwriteservices.Write")
    @ResponseWrapper(localName = "writeResponse", targetNamespace = "http://iesd.demo.async.ws", className = "iesd.demo.jaxws.client.readwriteservices.WriteResponse")
    public Response<WriteResponse> writeAsync(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        QcData arg1);

    /**
     * 
     * @param arg1
     * @param arg0
     * @param asyncHandler
     * @return
     *     returns java.util.concurrent.Future<? extends java.lang.Object>
     */
    @WebMethod(operationName = "write")
    @RequestWrapper(localName = "write", targetNamespace = "http://iesd.demo.async.ws", className = "iesd.demo.jaxws.client.readwriteservices.Write")
    @ResponseWrapper(localName = "writeResponse", targetNamespace = "http://iesd.demo.async.ws", className = "iesd.demo.jaxws.client.readwriteservices.WriteResponse")
    public Future<?> writeAsync(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        QcData arg1,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<WriteResponse> asyncHandler);

    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns int
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "write", targetNamespace = "http://iesd.demo.async.ws", className = "iesd.demo.jaxws.client.readwriteservices.Write")
    @ResponseWrapper(localName = "writeResponse", targetNamespace = "http://iesd.demo.async.ws", className = "iesd.demo.jaxws.client.readwriteservices.WriteResponse")
    public int write(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        QcData arg1);

    /**
     * 
     * @param arg0
     * @return
     *     returns javax.xml.ws.Response<iesd.demo.jaxws.client.readwriteservices.ReadResponse>
     */
    @WebMethod(operationName = "read")
    @RequestWrapper(localName = "read", targetNamespace = "http://iesd.demo.async.ws", className = "iesd.demo.jaxws.client.readwriteservices.Read")
    @ResponseWrapper(localName = "readResponse", targetNamespace = "http://iesd.demo.async.ws", className = "iesd.demo.jaxws.client.readwriteservices.ReadResponse")
    public Response<ReadResponse> readAsync(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

    /**
     * 
     * @param arg0
     * @param asyncHandler
     * @return
     *     returns java.util.concurrent.Future<? extends java.lang.Object>
     */
    @WebMethod(operationName = "read")
    @RequestWrapper(localName = "read", targetNamespace = "http://iesd.demo.async.ws", className = "iesd.demo.jaxws.client.readwriteservices.Read")
    @ResponseWrapper(localName = "readResponse", targetNamespace = "http://iesd.demo.async.ws", className = "iesd.demo.jaxws.client.readwriteservices.ReadResponse")
    public Future<?> readAsync(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<ReadResponse> asyncHandler);

    /**
     * 
     * @param arg0
     * @return
     *     returns iesd.demo.jaxws.client.readwriteservices.QcData
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "read", targetNamespace = "http://iesd.demo.async.ws", className = "iesd.demo.jaxws.client.readwriteservices.Read")
    @ResponseWrapper(localName = "readResponse", targetNamespace = "http://iesd.demo.async.ws", className = "iesd.demo.jaxws.client.readwriteservices.ReadResponse")
    public QcData read(
        @WebParam(name = "arg0", targetNamespace = "")
        String arg0);

}
