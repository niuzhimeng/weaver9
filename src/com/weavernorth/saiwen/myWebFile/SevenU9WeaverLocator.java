/**
 * SevenU9WeaverLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.weavernorth.saiwen.myWebFile;

public class SevenU9WeaverLocator extends org.apache.axis.client.Service implements SevenU9Weaver {

    public SevenU9WeaverLocator() {
    }


    public SevenU9WeaverLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SevenU9WeaverLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SevenU9WeaverSoap12
    private String SevenU9WeaverSoap12_address = "http://111.160.2.51/SVUW/SevenU9Weaver.asmx";

    public String getSevenU9WeaverSoap12Address() {
        return SevenU9WeaverSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private String SevenU9WeaverSoap12WSDDServiceName = "SevenU9WeaverSoap12";

    public String getSevenU9WeaverSoap12WSDDServiceName() {
        return SevenU9WeaverSoap12WSDDServiceName;
    }

    public void setSevenU9WeaverSoap12WSDDServiceName(String name) {
        SevenU9WeaverSoap12WSDDServiceName = name;
    }

    public SevenU9WeaverSoap_PortType getSevenU9WeaverSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SevenU9WeaverSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSevenU9WeaverSoap12(endpoint);
    }

    public SevenU9WeaverSoap_PortType getSevenU9WeaverSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            SevenU9WeaverSoap12Stub _stub = new SevenU9WeaverSoap12Stub(portAddress, this);
            _stub.setPortName(getSevenU9WeaverSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSevenU9WeaverSoap12EndpointAddress(String address) {
        SevenU9WeaverSoap12_address = address;
    }


    // Use to get a proxy class for SevenU9WeaverSoap
    private String SevenU9WeaverSoap_address = "http://111.160.2.51/SVUW/SevenU9Weaver.asmx";

    public String getSevenU9WeaverSoapAddress() {
        return SevenU9WeaverSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private String SevenU9WeaverSoapWSDDServiceName = "SevenU9WeaverSoap";

    public String getSevenU9WeaverSoapWSDDServiceName() {
        return SevenU9WeaverSoapWSDDServiceName;
    }

    public void setSevenU9WeaverSoapWSDDServiceName(String name) {
        SevenU9WeaverSoapWSDDServiceName = name;
    }

    public SevenU9WeaverSoap_PortType getSevenU9WeaverSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SevenU9WeaverSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSevenU9WeaverSoap(endpoint);
    }

    public SevenU9WeaverSoap_PortType getSevenU9WeaverSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            SevenU9WeaverSoap_BindingStub _stub = new SevenU9WeaverSoap_BindingStub(portAddress, this);
            _stub.setPortName(getSevenU9WeaverSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSevenU9WeaverSoapEndpointAddress(String address) {
        SevenU9WeaverSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (SevenU9WeaverSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                SevenU9WeaverSoap12Stub _stub = new SevenU9WeaverSoap12Stub(new java.net.URL(SevenU9WeaverSoap12_address), this);
                _stub.setPortName(getSevenU9WeaverSoap12WSDDServiceName());
                return _stub;
            }
            if (SevenU9WeaverSoap_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                SevenU9WeaverSoap_BindingStub _stub = new SevenU9WeaverSoap_BindingStub(new java.net.URL(SevenU9WeaverSoap_address), this);
                _stub.setPortName(getSevenU9WeaverSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
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
        String inputPortName = portName.getLocalPart();
        if ("SevenU9WeaverSoap12".equals(inputPortName)) {
            return getSevenU9WeaverSoap12();
        }
        else if ("SevenU9WeaverSoap".equals(inputPortName)) {
            return getSevenU9WeaverSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "SevenU9Weaver");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "SevenU9WeaverSoap12"));
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "SevenU9WeaverSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("SevenU9WeaverSoap12".equals(portName)) {
            setSevenU9WeaverSoap12EndpointAddress(address);
        }
        else 
if ("SevenU9WeaverSoap".equals(portName)) {
            setSevenU9WeaverSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
