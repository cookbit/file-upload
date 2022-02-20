/**
 * Copyright 2021-2022 jinzhaosn
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jinzhaosn.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * IP工具类
 *
 * @auther 961374431@qq.com
 * @date 2022年01月16日
 */
public class IpUtil {
    private static final Logger logger = LoggerFactory.getLogger(IpUtil.class);

    private IpUtil() {
    }

    /**
     * 获取服务IP地址
     *
     * @return IP地址
     */
    public static String getServerIp() {
        String localIp = null;
        String netIp = null;
        try {
            boolean findFlag = false;
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements() && !findFlag) {
                NetworkInterface networkInterface = netInterfaces.nextElement();
                Enumeration<InetAddress> addressEnum = networkInterface.getInetAddresses();
                while (addressEnum.hasMoreElements()) {
                    InetAddress ip = addressEnum.nextElement();
                    if (ip.isLoopbackAddress() || ip.getHostAddress().contains(":")) {
                        continue;
                    }
                    if (!ip.isSiteLocalAddress()) { // 外网IP
                        netIp = ip.getHostAddress();
                        findFlag = true;
                        break;
                    }
                    localIp = ip.getHostAddress();
                }
            }
        } catch (SocketException exp) {
            logger.error("get server ip exception: [{}]", exp.getMessage());
        }
        return StringUtils.isNoneBlank(netIp) ? netIp
                : StringUtils.isNoneBlank(localIp) ? localIp : "127.0.0.1";
    }
}
