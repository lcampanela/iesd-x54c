package isos.tutorial.isyiesd.cesvector.servector;
/*
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService(name = "IVector", targetNamespace = "http://iesd21.isos.isyiesd.cesvector.servectorserver")
@SOAPBinding(style = Style.DOCUMENT)
*/
public interface IVector {
/*
    @WebMethod
    int read(int pos);

    @WebMethod
    void write(int pos, int n);

    @WebMethod
    String invariantCheck();
*/
	public int read();
	public void write();

	public void prepare();
	public void commit();
	public void rollback();
	
}
