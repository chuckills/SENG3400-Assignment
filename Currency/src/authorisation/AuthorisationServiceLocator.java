/**
 * AuthorisationServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package authorisation;

public class AuthorisationServiceLocator extends org.apache.axis.client.Service implements authorisation.AuthorisationService {

    public AuthorisationServiceLocator() {
    }


    public AuthorisationServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public AuthorisationServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Authorisation
    private java.lang.String Authorisation_address = "http://localhost:8080/identity/Authorisation.jws";

    public java.lang.String getAuthorisationAddress() {
        return Authorisation_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String AuthorisationWSDDServiceName = "Authorisation";

    public java.lang.String getAuthorisationWSDDServiceName() {
        return AuthorisationWSDDServiceName;
    }

    public void setAuthorisationWSDDServiceName(java.lang.String name) {
        AuthorisationWSDDServiceName = name;
    }

    public authorisation.Authorisation_PortType getAuthorisation() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Authorisation_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getAuthorisation(endpoint);
    }

    public authorisation.Authorisation_PortType getAuthorisation(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            authorisation.AuthorisationSoapBindingStub _stub = new authorisation.AuthorisationSoapBindingStub(portAddress, this);
            _stub.setPortName(getAuthorisationWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setAuthorisationEndpointAddress(java.lang.String address) {
        Authorisation_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (authorisation.Authorisation_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                authorisation.AuthorisationSoapBindingStub _stub = new authorisation.AuthorisationSoapBindingStub(new java.net.URL(Authorisation_address), this);
                _stub.setPortName(getAuthorisationWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("Authorisation".equals(inputPortName)) {
            return getAuthorisation();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://localhost:8080/identity/Authorisation.jws", "AuthorisationService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://localhost:8080/identity/Authorisation.jws", "Authorisation"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("Authorisation".equals(portName)) {
            setAuthorisationEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
