package edu.czb.ros_app.utils;

import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @ProjectName: ros-app
 * @Package: edu.czb.ros_app.utils
 * @ClassName: NetWorkUtil
 * @Description:
 * @Author: 陈泽彬
 * @CreateDate: 2022/1/14 18:52
 * @Version: 1.0
 */
public class NetWorkUtil {
    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIpAddress(boolean useIPv4){
        try {
            ArrayList<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                ArrayList<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress address : inetAddresses) {
                    if(!address.isLoopbackAddress()){
                        String sAddress=address.getHostAddress();
                        assert sAddress != null;
                        if(!sAddress.contains(":")){
                            if(useIPv4){
                                return sAddress;
                            }
                        }else{
                            if(!useIPv4){
                                int i = sAddress.indexOf("%");
                                return i<0? sAddress.toUpperCase():sAddress.substring(0,i).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (SocketException ignored) {
        }
        return "";
    }

    public static ArrayList<String> getIpAddressList(boolean useIPv4){
        ArrayList<String> ipAddresses=new ArrayList<>();
        try {
            ArrayList<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                ArrayList<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress address : inetAddresses) {
                    if(!address.isLoopbackAddress()){
                        String sAddress=address.getHostAddress();
                        assert sAddress != null;
                        if(!sAddress.contains(":")){
                            if(useIPv4){
                                ipAddresses.add(sAddress);
                            }
                        }else{
                            if(!useIPv4){
                                int i = sAddress.indexOf("%");
                                ipAddresses.add(i<0? sAddress.toUpperCase():sAddress.substring(0,i).toUpperCase());
                            }
                        }
                    }
                }
            }
        } catch (SocketException ignored) {
        }
        return ipAddresses;
    }

    public static String getWifiSSID(WifiManager wifiManager) {
        if (wifiManager == null)
            return null;

        WifiInfo wifiInfo;

        wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED) {
            return wifiInfo.getSSID();
        }

        return null;
    }


    /**
     * Check if host is reachable.
     * @param host The host to check for availability. Can either be a machine name, such as "google.com",
     *             or a textual representation of its IP address, such as "8.8.8.8".
     * @param port The port number.
     * @param timeout The timeout in milliseconds.
     * @return True if the host is reachable. False otherwise.
     */
    public static boolean isHostAvailable(final String host, final int port, final int timeout) {
        try (final Socket socket = new Socket()) {
            final InetAddress inetAddress = InetAddress.getByName(host);
            final InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, port);

            socket.connect(inetSocketAddress, timeout);
            return true;

        } catch (ConnectException e) {
            e.printStackTrace();
            Log.e("Connection", "Failed do to unavailable network.");

        }catch (SocketTimeoutException e) {
            Log.e("Connection", "Failed do to reach host in time.");

        } catch (UnknownHostException e) {
            Log.e("Connection", "Unknown host.");

        } catch (IOException e) {
            Log.e("Connection", "IO Exception.");
        }

        return false;
    }
}
