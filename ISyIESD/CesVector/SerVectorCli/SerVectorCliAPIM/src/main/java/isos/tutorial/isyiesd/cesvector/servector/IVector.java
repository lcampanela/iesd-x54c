
package isos.tutorial.isyiesd.cesvector.servector;

import java.util.concurrent.Future;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.Response;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.3.3-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "IVector", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface IVector {


    /**
     * 
     * @param arg1
     * @param arg0
     * @return
     *     returns javax.xml.ws.Response&lt;isos.tutorial.isyiesd.cesvector.servector.WriteResponse&gt;
     */
    @WebMethod(operationName = "write")
    @RequestWrapper(localName = "write", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.Write")
    @ResponseWrapper(localName = "writeResponse", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.WriteResponse")
    public Response<WriteResponse> writeAsync(
        @WebParam(name = "arg0", targetNamespace = "")
        int arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        int arg1);

    /**
     * 
     * @param arg1
     * @param arg0
     * @param asyncHandler
     * @return
     *     returns java.util.concurrent.Future&lt;? extends java.lang.Object&gt;
     */
    @WebMethod(operationName = "write")
    @RequestWrapper(localName = "write", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.Write")
    @ResponseWrapper(localName = "writeResponse", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.WriteResponse")
    public Future<?> writeAsync(
        @WebParam(name = "arg0", targetNamespace = "")
        int arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        int arg1,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<WriteResponse> asyncHandler);

    /**
     * 
     * @param arg1
     * @param arg0
     */
    @WebMethod
    @RequestWrapper(localName = "write", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.Write")
    @ResponseWrapper(localName = "writeResponse", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.WriteResponse")
    @Action(input = "http://iesd21.isos.isyiesd.cesvector.servectorserver/IVector/writeRequest", output = "http://iesd21.isos.isyiesd.cesvector.servectorserver/IVector/writeResponse")
    public void write(
        @WebParam(name = "arg0", targetNamespace = "")
        int arg0,
        @WebParam(name = "arg1", targetNamespace = "")
        int arg1);

    /**
     * 
     * @param arg0
     * @return
     *     returns javax.xml.ws.Response&lt;isos.tutorial.isyiesd.cesvector.servector.ReadResponse&gt;
     */
    @WebMethod(operationName = "read")
    @RequestWrapper(localName = "read", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.Read")
    @ResponseWrapper(localName = "readResponse", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.ReadResponse")
    public Response<ReadResponse> readAsync(
        @WebParam(name = "arg0", targetNamespace = "")
        int arg0);

    /**
     * 
     * @param arg0
     * @param asyncHandler
     * @return
     *     returns java.util.concurrent.Future&lt;? extends java.lang.Object&gt;
     */
    @WebMethod(operationName = "read")
    @RequestWrapper(localName = "read", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.Read")
    @ResponseWrapper(localName = "readResponse", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.ReadResponse")
    public Future<?> readAsync(
        @WebParam(name = "arg0", targetNamespace = "")
        int arg0,
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<ReadResponse> asyncHandler);

    /**
     * 
     * @param arg0
     * @return
     *     returns int
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "read", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.Read")
    @ResponseWrapper(localName = "readResponse", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.ReadResponse")
    @Action(input = "http://iesd21.isos.isyiesd.cesvector.servectorserver/IVector/readRequest", output = "http://iesd21.isos.isyiesd.cesvector.servectorserver/IVector/readResponse")
    public int read(
        @WebParam(name = "arg0", targetNamespace = "")
        int arg0);

    /**
     * 
     * @return
     *     returns javax.xml.ws.Response&lt;isos.tutorial.isyiesd.cesvector.servector.InvariantCheckResponse&gt;
     */
    @WebMethod(operationName = "invariantCheck")
    @RequestWrapper(localName = "invariantCheck", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.InvariantCheck")
    @ResponseWrapper(localName = "invariantCheckResponse", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.InvariantCheckResponse")
    public Response<InvariantCheckResponse> invariantCheckAsync();

    /**
     * 
     * @param asyncHandler
     * @return
     *     returns java.util.concurrent.Future&lt;? extends java.lang.Object&gt;
     */
    @WebMethod(operationName = "invariantCheck")
    @RequestWrapper(localName = "invariantCheck", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.InvariantCheck")
    @ResponseWrapper(localName = "invariantCheckResponse", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.InvariantCheckResponse")
    public Future<?> invariantCheckAsync(
        @WebParam(name = "asyncHandler", targetNamespace = "")
        AsyncHandler<InvariantCheckResponse> asyncHandler);

    /**
     * 
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "invariantCheck", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.InvariantCheck")
    @ResponseWrapper(localName = "invariantCheckResponse", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver", className = "isos.tutorial.isyiesd.cesvector.servector.InvariantCheckResponse")
    @Action(input = "http://iesd21.isos.isyiesd.cesvector.servectorserver/IVector/invariantCheckRequest", output = "http://iesd21.isos.isyiesd.cesvector.servectorserver/IVector/invariantCheckResponse")
    public String invariantCheck();

}
