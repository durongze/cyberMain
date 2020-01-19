
package org.cybergarage.main;

import org.cybergarage.upnp.*;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.NotifyListener;
import org.cybergarage.upnp.event.EventListener;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;
import org.cybergarage.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

class cyberMain{
    DeviceList deviceList = new DeviceList();
    ControlPoint controlPoint = new ControlPoint();
    String TAG = "cyberMain";
    Log log = new Log();
    void Init()
    {
        // 初始化
        new Thread(new Runnable() {
            public void run() {
                controlPoint.start();
            }
        }).start();
    }

    void Search()
    {
        new Thread(new Runnable() {
            public void run() {
                // controlPoint.start();
                controlPoint.search();
            }
        }).start();
    }

    void NotifyListen()
    {
        controlPoint.addNotifyListener(new NotifyListener() {
            @Override
            public void deviceNotifyReceived(SSDPPacket packet) {
                log.i(TAG + "Got Notification from device, remoteAddress is" + packet.getRemoteAddress());
                log.i(packet.toString());
            }
        });
    }

    void ResponeListen()
    {
        controlPoint.addSearchResponseListener(new SearchResponseListener() {
            @Override
            public void deviceSearchResponseReceived(SSDPPacket packet) {
                log.i(TAG + "A new device was searched, remoteAddress is" + packet.getRemoteAddress());
                log.i(packet.toString());
            }
        });
    }

    void Request()
    {
        controlPoint.addDeviceChangeListener(new DeviceChangeListener() {
            @Override
            public void deviceRemoved(Device device) throws MalformedURLException {
                log.i(TAG + " deviceRemoved " + device.getDeviceType());
                ShowAll();
                if ("urn:schemas-upnp-org:device:MediaRenderer:1".equals(device.getDeviceType())) {
                    deviceList.remove(device);
                }
            }

            @Override
            public void deviceAdded(Device device) throws MalformedURLException {
                // 判断是否为DMR
                log.i(TAG + " deviceAdded " + device.getDeviceType());
                ShowAll();
                if ("urn:schemas-upnp-org:device:MediaRenderer:1".equals(device.getDeviceType())) {
                    deviceList.add(device);
                }
            }
        });
    }

    void Play(String transportURI)
    {
        // 实例ID
        String instanceID = "0";
        // 播放视频地址
        String currentURI = "http://hc.yinyuetai.com/uploads/videos/common/026E01578953FD0EF0E47204247B5D13.flv?sc=2d17ae37a9186da6&br=780&vid=2693509&aid=623&area=US&vst=2";
        Device device = deviceList.getDevice(0);
        // 获取服务
        Service service = device.getService("urn:schemas-upnp-org:service:AVTransport:1");
        // 获取动作
        Action transportAction = service.getAction("SetAVTransportURI");
        // 设置参数
        transportAction.setArgumentValue("InstanceID", instanceID);
        transportAction.setArgumentValue("CurrentURI", transportURI);
        // SetAVTransportURI
        if(transportAction.postControlAction()) {
            // 成功
            Action playAction = service.getAction("Play");
            playAction.setArgumentValue("InstanceID", instanceID);
            // Play
            if (!playAction.postControlAction()) {
                log.e("upnpErr" + playAction.getStatus().getDescription());
            }
        } else {
            // 失败
            log.e("upnpErr" + transportAction.getStatus().getDescription());
        }
    }

    void ShowAll() throws MalformedURLException {
        log.i("=========================");
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

    void Subscribe(int devIdx)
    {
        Device device = deviceList.getDevice(devIdx);
        // 获取服务
        Service service = device.getService("urn:schemas-upnp-org:service:AVTransport:1");
        boolean ret = controlPoint.subscribe(service);
        if (ret) {
            // 订阅成功
            log.i(TAG + device.getLocation() + " succ");
        } else {
            // 订阅失败
            log.i(TAG + device.getLocation() + " fail");
        }
    }

    void EventListen()
    {
        controlPoint.addEventListener(new EventListener() {
            @Override
            public void eventNotifyReceived(String uuid, long seq, String varName, String value){
                // 事件回调
                log.i("addEvent");
            }
        });
    }

    void Scanner()
    {
        int arr[] = new int[10];
        int i = 0;
        for (i = 0; i < 10; i++) {
            Scanner scan = new Scanner(System.in);
            System.out.println("input number " + i + " : ");
            arr[i] = scan.nextInt();
            System.out.println(arr[i]);
        }
    }

    public static void main(String argv[]) throws MalformedURLException {
        cyberMain c = new cyberMain();
        c.Init();
        c.NotifyListen();
        c.ResponeListen();
        c.Request();
        c.Search();
    }
}