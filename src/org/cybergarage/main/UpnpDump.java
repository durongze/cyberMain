package org.cybergarage.main;

/******************************************************************
 *
 *	CyberUPnP for Java
 *
 *	Copyright (C) Satoshi Konno 2002
 *
 *	File : UpnpDump.java
 *
 ******************************************************************/

import org.cybergarage.upnp.*;
import org.cybergarage.upnp.ssdp.*;
import org.cybergarage.upnp.device.*;
import org.cybergarage.upnp.event.*;
import org.cybergarage.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class UpnpDump extends ControlPoint implements NotifyListener, EventListener, SearchResponseListener
{
    ////////////////////////////////////////////////
    //	Constractor
    ////////////////////////////////////////////////
    Log log = new Log();
    DeviceList deviceList = null;
    String TAG = "UpnpDump";
    public UpnpDump()
    {
        addNotifyListener(this);
        addSearchResponseListener(this);
        addEventListener(this);
    }

    ////////////////////////////////////////////////
    //	Listener
    ////////////////////////////////////////////////

    public void deviceNotifyReceived(SSDPPacket packet)
    {
        System.out.println(packet.toString());

        if (packet.isDiscover() == true) {
            String st = packet.getST();
            System.out.println("ssdp:discover : ST = " + st);
        }
        else if (packet.isAlive() == true) {
            String usn = packet.getUSN();
            String nt = packet.getNT();
            String url = packet.getLocation();
            System.out.println("ssdp:alive : uuid = " + usn + ", NT = " + nt + ", location = " + url);
        }
        else if (packet.isByeBye() == true) {
            String usn = packet.getUSN();
            String nt = packet.getNT();
            System.out.println("ssdp:byebye : uuid = " + usn + ", NT = " + nt);
        }
    }

    public void deviceSearchResponseReceived(SSDPPacket packet)
    {
        String uuid = packet.getUSN();
        String st = packet.getST();
        String url = packet.getLocation();
        System.out.println("device search res : uuid = " + uuid + ", ST = " + st + ", location = " + url);
        print();
    }

    public void eventNotifyReceived(String uuid, long seq, String name, String value)
    {
        System.out.println("event notify : uuid = " + uuid + ", seq = " + seq + ", name = " + name + ", value =" + value);
    }

    ////////////////////////////////////////////////
    //	main
    ////////////////////////////////////////////////
    public void ShowAll() throws MalformedURLException {
        log.i("=========================");
        deviceList = getDeviceList();
        for (int i = 0; i < deviceList.size(); i++) {
            // 设备描述文档
            Device device = deviceList.getDevice(i);
            String locationUrl = device.getLocation();
            // 获取服务
            Service service = device.getService("urn:schemas-upnp-org:service:AVTransport:1");
            URL url = new URL(locationUrl);
            // SDD
            String sddUrl = url.getHost() + url.getPort() + service.getSCPDURL();
            log.i(TAG + sddUrl);
        }
    }
    public static void main(String args[])
    {
        UpnpDump upnpDump = new UpnpDump();
        upnpDump.start();
        upnpDump.search();
    }
}
