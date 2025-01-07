/****************************\
*      ________________      *
*     /  _             \     *
*     \   \ |\   _  \  /     *
*      \  / | \ / \  \/      *
*      /  \ | / | /  /\      *
*     /  _/ |/  \__ /  \     *
*     \________________/     *
*                            *
\****************************/
/*
* Copyright 2024 Damien Westerman
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.damienwesterman.defensedrill.mvc.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.damienwesterman.defensedrill.mvc.web.dto.ErrorMessageDTO;

/**
 * Public static constants common to the entire application.
 */
public class Constants {
    public final static String REST_API_BASE_ADDRESS = "lb://rest-api";

    public final static ErrorMessageDTO GENERIC_INTERNAL_ERROR_DTO = new ErrorMessageDTO(
        Constants.GENERIC_INTERNAL_ERROR,
        Constants.GENERIC_INTERNAL_ERROR_MESSAGE
    );

    // User feedback messages
    public final static String GENERIC_INTERNAL_ERROR = "Internal Error";
    public final static String GENERIC_INTERNAL_ERROR_MESSAGE = "Please try again later.";
    public final static String NOT_FOUND_ERROR = "Not Found";

    public final static String SERVER_IP_ADDRESS = getNetworkIpAddress();

    private static String getNetworkIpAddress() {
        try {
            // Get the list of all network interfaces
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // Skip non-physical interfaces (like virtual or loopback interfaces)
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                // Get the list of IP addresses for this network interface
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();

                    // Skip loopback addresses (127.0.0.1)
                    if (inetAddress.isLoopbackAddress()) {
                        continue;
                    }

                    // If it's an IPv4 address, return it
                    if (inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "localhost";
    }
}
