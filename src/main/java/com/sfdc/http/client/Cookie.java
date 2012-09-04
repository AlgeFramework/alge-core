package com.sfdc.http.client;

/**
 * @author psrinivasan
 *         Date: 8/30/12
 *         Time: 10:50 PM
 */
public interface Cookie {
    public java.lang.String getDomain();

    public java.lang.String getName();

    public java.lang.String getValue();

    public java.lang.String getPath();

    public int getMaxAge();

    public boolean isSecure();

    public int getVersion();

    public java.util.Set<java.lang.Integer> getPorts();

    public void setPorts(int... ports);

    public void setPorts(java.lang.Iterable<java.lang.Integer> ports);

    public java.lang.String toString();
}
