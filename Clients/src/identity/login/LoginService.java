/**
 * LoginService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package identity.login;

public interface LoginService extends javax.xml.rpc.Service {
    public java.lang.String getLoginAddress();

    public identity.login.Login_PortType getLogin() throws javax.xml.rpc.ServiceException;

    public identity.login.Login_PortType getLogin(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
