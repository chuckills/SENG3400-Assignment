/**
 * AuthorisationService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package authorisation;

public interface AuthorisationService extends javax.xml.rpc.Service {
    public java.lang.String getAuthorisationAddress();

    public authorisation.Authorisation_PortType getAuthorisation() throws javax.xml.rpc.ServiceException;

    public authorisation.Authorisation_PortType getAuthorisation(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
