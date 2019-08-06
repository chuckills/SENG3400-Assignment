/**
 * Admin_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package currency.admin;

public interface Admin_PortType extends java.rmi.Remote {
    public boolean removeRate(java.lang.String sessionKey, java.lang.String fromCurrencyCode, java.lang.String toCurrencyCode) throws java.rmi.RemoteException;
    public boolean removeCurrency(java.lang.String sessionKey, java.lang.String currencyCode) throws java.rmi.RemoteException;
    public java.lang.String[] listCurrencies(java.lang.String sessionKey) throws java.rmi.RemoteException;
    public java.lang.String[] conversionsFor(java.lang.String sessionKey, java.lang.String currencyCode) throws java.rmi.RemoteException;
    public boolean updateRate(java.lang.String sessionKey, java.lang.String fromCurrencyCode, java.lang.String toCurrencyCode, double rate) throws java.rmi.RemoteException;
    public boolean addCurrency(java.lang.String sessionKey, java.lang.String currencyCode) throws java.rmi.RemoteException;
    public boolean addRate(java.lang.String sessionKey, java.lang.String fromCurrencyCode, java.lang.String toCurrencyCode, double conversionRate) throws java.rmi.RemoteException;
    public java.lang.String[] listRates(java.lang.String sessionKey) throws java.rmi.RemoteException;
}
