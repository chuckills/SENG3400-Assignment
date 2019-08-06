/**
 * ConversionService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package currency.conversion;

public interface ConversionService extends javax.xml.rpc.Service {
    public java.lang.String getConversionAddress();

    public currency.conversion.Conversion_PortType getConversion() throws javax.xml.rpc.ServiceException;

    public currency.conversion.Conversion_PortType getConversion(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
