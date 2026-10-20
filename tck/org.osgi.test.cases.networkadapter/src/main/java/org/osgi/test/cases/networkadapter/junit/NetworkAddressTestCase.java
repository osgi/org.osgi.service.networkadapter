/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
package org.osgi.test.cases.networkadapter.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.net.InetAddress;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.networkadapter.NetworkAdapter;
import org.osgi.service.networkadapter.NetworkAddress;
import org.osgi.test.cases.networkadapter.util.NetworkTestProxy;
import org.osgi.test.cases.networkadapter.util.TestServiceListener;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.tck.step.TestStepProxy;

public class NetworkAddressTestCase {

	@InjectBundleContext
	BundleContext		context;
    private NetworkTestProxy testProxy;

	@BeforeEach
	public void setUp() throws Exception {
        this.testProxy = new NetworkTestProxy(new TestStepProxy(context));
    }

	@AfterEach
	public void tearDown() throws Exception {
        this.testProxy.close();
    }

    /**
     * Tests NetworkAddress of the interface that starts.
     */
    @Test
    public void testNetworkAddress01() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(addressListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-ADD01] Add an up network adapter.";
            String[] parameters = new String[]{"up"};
            ids = testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);
            context.removeServiceListener(addressListener);

            ServiceReference< ? > ref = addressListener.get(0);
            Object type = ref.getProperty(NetworkAddress.NETWORKADAPTER_TYPE);
            Object version = ref.getProperty(NetworkAddress.IPADDRESS_VERSION);
            Object scope = ref.getProperty(NetworkAddress.IPADDRESS_SCOPE);
            Object ipAddress = ref.getProperty(NetworkAddress.IPADDRESS);
            Object subnetmask = ref.getProperty(NetworkAddress.SUBNETMASK_LENGTH);
            Object pid = ref.getProperty(NetworkAddress.NETWORKADAPTER_PID);

            assertThat(type instanceof String).as("The following service property is not correct: " + NetworkAddress.NETWORKADAPTER_TYPE).isTrue();
            assertThat(version instanceof String).as("The following service property is not correct: " + NetworkAddress.IPADDRESS_VERSION).isTrue();
            assertThat(scope instanceof String).as("The following service property is not correct: " + NetworkAddress.IPADDRESS_SCOPE).isTrue();
            assertThat(ipAddress instanceof String).as("The following service property is not correct: " + NetworkAddress.IPADDRESS).isTrue();
            assertThat(subnetmask instanceof Integer).as("The following service property is not correct: " + NetworkAddress.SUBNETMASK_LENGTH).isTrue();
            assertThat(pid instanceof String).as("The following service property is not correct: " + NetworkAddress.NETWORKADAPTER_PID).isTrue();

            NetworkAddress networkAddress = (NetworkAddress) context.getService(addressListener.get(0));
            assertThat(networkAddress.getNetworkAdapterType()).as("The following NetworkAddress information does not match: "+ NetworkAddress.NETWORKADAPTER_TYPE).isEqualTo(type);
            assertThat(networkAddress.getIpAddressVersion()).as("The following NetworkAddress information does not match: "+ NetworkAddress.IPADDRESS_VERSION).isEqualTo(version);
            assertThat(networkAddress.getIpAddressScope()).as("The following NetworkAddress information does not match: "+ NetworkAddress.IPADDRESS_SCOPE).isEqualTo(scope);
            assertThat(networkAddress.getIpAddress()).as("The following NetworkAddress information does not match: "+ NetworkAddress.IPADDRESS).isEqualTo(ipAddress);
            assertThat(networkAddress.getInetAddress()).as("The following NetworkAddress information does not match: "+ NetworkAddress.IPADDRESS).isEqualTo(InetAddress.getByName((String)ipAddress));
            assertThat(networkAddress.getSubnetMaskLength()).as("The following NetworkAddress information does not match: "+ NetworkAddress.IPADDRESS).isEqualTo(((Integer)subnetmask).intValue());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }
}
