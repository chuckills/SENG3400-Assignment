/**
 * AdminService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package currency.admin;

public interface AdminService extends javax.xml.rpc.Service {
    public java.lang.String getAdminAddress();

    public currency.admin.Admin_PortType getAdmin() throws javax.xml.rpc.ServiceException;

    public currency.admin.Admin_PortType getAdmin(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
