
package org.cybergarage.main;

import org.cybergarage.upnp.*;
import org.cybergarage.upnp.device.DeviceChangeListener;
import org.cybergarage.upnp.device.NotifyListener;
import org.cybergarage.upnp.device.SearchResponseListener;
import org.cybergarage.upnp.ssdp.SSDPPacket;
import org.cybergarage.util.Log;

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

    void Listen()
    {
        controlPoint.addNotifyListener(new NotifyListener() {
            @Override
            public void deviceNotifyReceived(SSDPPacket packet) {
                log.i(TAG + "Got Notification from device, remoteAddress is" + packet.getRemoteAddress());
                log.i(packet.toString());
            }
        });
    }

    void Respone()
    {
        controlPoint.addSearchResponseListener(new SearchResponseListener() {
            @Override
            public void deviceSearchResponseReceived(SSDPPacket packet) {
                log.i(TAG + "A new device was searched, remoteAddress is" + packet.getRemoteAddress());
                log.i(packet.toString());
            }
        });
    }

    void Change(){
        controlPoint.addDeviceChangeListener(new DeviceChangeListener() {
            @Override
            public void deviceRemoved(Device device) {
                log.i(TAG + "Device was removed, device name: " + device.getFriendlyName());
            }

            @Override
            public void deviceAdded(Device device) {
                log.i(TAG + "Device was added, device name:" +  device.getFriendlyName());
            }
        });
    }

    void Request()
    {
        controlPoint.addDeviceChangeListener(new DeviceChangeListener() {
            @Override
            public void deviceRemoved(Device device) {
                if ("urn:schemas-upnp-org:device:MediaRenderer:1".equals(device.getDeviceType())) {
                    deviceList.remove(device);
                }
            }

            @Override
            public void deviceAdded(Device device) {
                // 判断是否为DMR
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

    public static void main(String argv[])
    {
        cyberMain c = new cyberMain();
        c.Init();
        // c.Listen();
        c.Search();
        //c.Request();

    }
}