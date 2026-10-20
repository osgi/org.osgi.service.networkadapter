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

public class NetworkIfTestCase {

	@InjectBundleContext
	BundleContext		context;
    private NetworkTestProxy	testProxy;

	@BeforeEach
	public void setUp() throws Exception {
        this.testProxy = new NetworkTestProxy(new TestStepProxy(context));
    }

	@AfterEach
	public void tearDown() throws Exception {
        this.testProxy.close();
    }

    /**
     * Tests NetworkAdapter add operation.
     */
    @Test
    public void testAddAction01() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-AA01] Add a no-address network adapter.";
            String[] parameters = new String[]{"no-address"};
            ids = testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);
            context.removeServiceListener(addressListener);

            // Confirmation of the number of the services.
            assertThat(adapterListener.size()).as("Only one NetworkAdapter service registerd event is expected.").isOne();
            assertThat(addressListener.size()).as("Zero NetworkAddress service registered event is expected.").isZero();

            // Confirmation of the service property.
            ServiceReference< ? > adapterRef = adapterListener.get(0);
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE) instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME) instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_NAME) instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS) instanceof byte[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK) instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT) instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP) instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL) instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST) instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE) instanceof String[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE).isTrue();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV4<br>
     * scope: HOST<br>
     */
    @Test
    public void testAddAction02() {

        String message = "[TEST-AA02] Add an ipv4-host network adapter.";
        String type = "ipv4-host";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_4, NetworkAddress.IPADDRESS_SCOPE_HOST);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV4<br>
     * scope: PRIVATE_USE<br>
     */
    @Test
    public void testAddAction03() {

        String message = "[TEST-AA03] Add an ipv4-private-use network adapter.";
        String type = "ipv4-private-use";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_4, NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV4<br>
     * scope: SHARED<br>
     */
    @Test
    public void testAddAction04() {

        String message = "[TEST-AA04] Add an ipv4-shared network adapter.";
        String type = "ipv4-shared";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_4, NetworkAddress.IPADDRESS_SCOPE_SHARED);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV4<br>
     * scope: LOOPBACK<br>
     */
    @Test
    public void testAddAction05() {

        String message = "[TEST-AA05] Add an ipv4-loopback network adapter.";
        String type = "ipv4-loopback";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_4, NetworkAddress.IPADDRESS_SCOPE_LOOPBACK);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV4<br>
     * scope: LINKLOCAL<br>
     */
    @Test
    public void testAddAction06() {

        String message = "[TEST-AA06] Add an ipv4-linklocal network adapter.";
        String type = "ipv4-linklocal";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_4, NetworkAddress.IPADDRESS_SCOPE_LINKLOCAL);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV4<br>
     * scope: GLOBAL<br>
     */
    @Test
    public void testAddAction07() {

        String message = "[TEST-AA07] Add an ipv4-global network adapter.";
        String type = "ipv4-global";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_4, NetworkAddress.IPADDRESS_SCOPE_GLOBAL);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV6<br>
     * scope: LOOPBACK<br>
     */
    @Test
    public void testAddAction08() {

        String message = "[TEST-AA08] Add an ipv6-loopback network adapter.";
        String type = "ipv6-loopback";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_6, NetworkAddress.IPADDRESS_SCOPE_LOOPBACK);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV6<br>
     * scope: UNSPECIFIED<br>
     */
    @Test
    public void testAddAction09() {

        String message = "[TEST-AA09] Add an ipv6-unspecified network adapter.";
        String type = "ipv6-unspecified";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_6, NetworkAddress.IPADDRESS_SCOPE_UNSPECIFIED);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV6<br>
     * scope: UNIQUE_LOCAL<br>
     */
    @Test
    public void testAddAction10() {

        String message = "[TEST-AA10] Add an ipv6-uniquelocal network adapter.";
        String type = "ipv6-uniquelocal";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_6, NetworkAddress.IPADDRESS_SCOPE_UNIQUE_LOCAL);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV6<br>
     * scope: LINKED_SCOPED_UNICAST<br>
     */
    @Test
    public void testAddAction11() {

        String message = "[TEST-AA11] Add an ipv6-linkedscopedunicast network adapter.";
        String type = "ipv6-linkedscopedunicast";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_6, NetworkAddress.IPADDRESS_SCOPE_LINKED_SCOPED_UNICAST);
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * version: IPV6<br>
     * scope: GLOBAL<br>
     */
    @Test
    public void testAddAction12() {

        String message = "[TEST-AA12] Add an ipv6-global network adapter.";
        String type = "ipv6-global";

        testAddNetworkAdapter(message, type, NetworkAddress.IPADDRESS_VERSION_6, NetworkAddress.IPADDRESS_SCOPE_GLOBAL);
    }

    private void testAddNetworkAdapter(String message,String type, String version, String scope) {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");
            String command = "addNetworkAdapter";
            String[] parameters = new String[]{type};
            ids = testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);
            context.removeServiceListener(addressListener);

            // Confirmation of the number of the services.
            assertThat(adapterListener.size()).as("Only one NetworkAdapter service registered event is expected.").isOne();
            assertThat(addressListener.size()).as("Only one NetworkAddress service registered event is expected.").isOne();

            // Confirmation of the service property.
            ServiceReference< ? > adapterRef = adapterListener.get(0);
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE) instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME) instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_NAME) instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS) instanceof byte[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK) instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT) instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP) instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL) instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST) instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isTrue();
            assertThat(adapterRef.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE) instanceof String[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE).isTrue();

            ServiceReference< ? > addressRef = addressListener.get(0);
            assertThat(addressRef.getProperty(NetworkAddress.NETWORKADAPTER_TYPE) instanceof String).as("The following service property is not correct: " + NetworkAddress.NETWORKADAPTER_TYPE).isTrue();
            assertThat(addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION)).as("The following service property is not correct: " + NetworkAddress.IPADDRESS_VERSION).isEqualTo(version);
            assertThat(addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE)).as("The following service property is not correct: " + NetworkAddress.IPADDRESS_SCOPE).isEqualTo(scope);
            assertThat(addressRef.getProperty(NetworkAddress.IPADDRESS) instanceof String).as("The following service property is not correct: " + NetworkAddress.IPADDRESS).isTrue();
            assertThat(addressRef.getProperty(NetworkAddress.SUBNETMASK_LENGTH) instanceof Integer).as("The following service property is not correct: " + NetworkAddress.SUBNETMASK_LENGTH).isTrue();
            assertThat(addressRef.getProperty(NetworkAddress.NETWORKADAPTER_PID) instanceof String).as("The following service property is not correct: " + NetworkAddress.NETWORKADAPTER_PID).isTrue();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter add operation. Appoint the following IP addresses.<br>
     * IP address 1<br>
     * version: IPV4<br>
     * scope: PRIVATE_USE<br>
     * IP address 2<br>
     * version: IPV6<br>
     * scope: UNIQUE_LOCAL<br>
     */
    @Test
    public void testAddAction13() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-AA13] Add an ipv4-private-use network adapter.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);
            adapterListener.clear();

            context.addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Addition of the IP address information
            command = "addNetworkAddress";
            message = "Add an ipv6-uniquelocal network address.";
            String[] parameters1 = new String[]{"ipv6-uniquelocal", ids[0]};
            testProxy.executeTestStep(command, message, parameters1);

            context.removeServiceListener(adapterListener);
            context.removeServiceListener(addressListener);

            // Confirmation of the number of the services.
            assertThat(adapterListener.size()).as("Only zero NetworkAdapter service registered event is expected.").isZero();
            assertThat(addressListener.size()).as("Only one NetworkAddress service registered event is expected.").isOne();

            // Confirmation of the service property.
            ServiceReference< ? > addressRef = addressListener.get(0);
            assertThat(addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION)).as("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_VERSION).isEqualTo(NetworkAddress.IPADDRESS_VERSION_6);
            assertThat(addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE)).as("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_SCOPE).isEqualTo(NetworkAddress.IPADDRESS_SCOPE_UNIQUE_LOCAL);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAddress modify operation. <br>
     * IP address before a change.<br>
     * version: IPV4<br>
     * scope: PRIVATE_USE<br>
     * IP address after a change.<br>
     * version: IPV4<br>
     * scope: PRIVATE_USE<br>
     */
    @Test
    public void testModifyAction01() {
        String[] ids = null;
        try {
            String command = "addNetworkAdapter";
            String message = "[TEST-MA01] Add an ipv4-private-use network adapter.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.MODIFIED);
            context.addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.MODIFIED);
            context.addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Change of the IP address information
            command = "modifyNetworkAddress";
            message = "Modify to other ipv4-private-use network address.";
            parameters = new String[]{"ipv4-private-use_2", ids[1]};
            testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);
            context.removeServiceListener(addressListener);

            // Confirmation of the number of the services.
            assertThat(adapterListener.size()).as("Only zero NetworkAdapter service modified event is expected.").isZero();
            assertThat(addressListener.size()).as("Only one NetworkAddress service modified event is expected.").isOne();

            // Confirmation of the service property.
            ServiceReference< ? > addressRef = addressListener.get(0);
            assertThat(addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION)).as("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_VERSION).isEqualTo(NetworkAddress.IPADDRESS_VERSION_4);
            assertThat(addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE)).as("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_SCOPE).isEqualTo(NetworkAddress.IPADDRESS_SCOPE_PRIVATE_USE);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAddress modify operation. <br>
     * IP address before a change.<br>
     * version: IPV4<br>
     * scope: PRIVATE_USE<br>
     * IP address after a change.<br>
     * version: IPV4<br>
     * scope: GLOBAL<br>
     */
    @Test
    public void testModifyAction02() {
        String[] ids = null;
        try {
            String command = "addNetworkAdapter";
            String message = "[TEST-MA02] Add an ipv4-private-use network adapter.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.MODIFIED);
            context.addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.MODIFIED);
            context.addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Change of the IP address information
            command = "modifyNetworkAddress";
            message = "Modify to ipv4-global network address.";
            parameters = new String[]{"ipv4-global", ids[1]};
            testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);
            context.removeServiceListener(addressListener);

            // Confirmation of the number of the services.
            assertThat(adapterListener.size()).as("Only zero NetworkAdapter service modified event is expected.").isZero();
            assertThat(addressListener.size()).as("Only one NetworkAddress service modified event is expected.").isOne();

            // Confirmation of the service property.
            ServiceReference< ? > addressRef = addressListener.get(0);
            assertThat(addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION)).as("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_VERSION).isEqualTo(NetworkAddress.IPADDRESS_VERSION_4);
            assertThat(addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE)).as("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_SCOPE).isEqualTo(NetworkAddress.IPADDRESS_SCOPE_GLOBAL);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAddress modify operation. <br>
     * IP address before a change.<br>
     * version: IPV4<br>
     * scope: PRIVATE_USE<br>
     * IP address after a change.<br>
     * version: IPV6<br>
     * scope: UNIQUE_LOCAL<br>
     */
    @Test
    public void testModifyAction03() {
        String[] ids = null;
        try {
            String command = "addNetworkAdapter";
            String message = "[TEST-MA03] Add an ipv4-private-use network adapter.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.MODIFIED);
            context.addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.MODIFIED);
            context.addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Change of the IP address information
            command = "modifyNetworkAddress";
            message = "Modify to ipv6-uniquelocal network address.";
            parameters = new String[]{"ipv6-uniquelocal", ids[1]};
            testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);
            context.removeServiceListener(addressListener);

            // Confirmation of the number of the services.
            assertThat(adapterListener.size()).as("Only zero NetworkAdapter service modified event is expected.").isZero();
            assertThat(addressListener.size()).as("Only one NetworkAddress service modified event is expected.").isOne();

            // Confirmation of the service property.
            ServiceReference< ? > addressRef = addressListener.get(0);
            assertThat(addressRef.getProperty(NetworkAddress.IPADDRESS_VERSION)).as("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_VERSION).isEqualTo(NetworkAddress.IPADDRESS_VERSION_6);
            assertThat(addressRef.getProperty(NetworkAddress.IPADDRESS_SCOPE)).as("The following NetworkAddress service property is not correct: " + NetworkAddress.IPADDRESS_SCOPE).isEqualTo(NetworkAddress.IPADDRESS_SCOPE_UNIQUE_LOCAL);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAddress remove operation. Two NetworkAddress.
     */
    @Test
    public void testRemoveAction01() {
        String[] ids = null;
        try {
            String command = "addNetworkAdapter";
            String message = "[TEST-RA01] Add the first network adapter/address.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            command = "addNetworkAddress";
            message = "Add the second network address.";
            String[] parameters1 = new String[]{"ipv4-private-use_2", ids[0]};
            testProxy.executeTestStep(command, message, parameters1);

            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            context.addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            context.addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Deletion of the IP address information.
            command = "removeNetworkAddress";
            message = "Remove the first network address.";
            String[] parameters2 = new String[]{ids[1]};
            testProxy.executeTestStep(command, message, parameters2);

            context.removeServiceListener(adapterListener);
            context.removeServiceListener(addressListener);

            // Confirmation of the service unregistration.
            assertThat(adapterListener.size()).as("Only zero NetworkAdapter service unregistering event is expected.").isZero();
            assertThat(addressListener.size()).as("Only one NetworkAddress service unregistering event is expected.").isOne();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAddress remove operation. One NetworkAddress.
     */
    @Test
    public void testRemoveAction02() {
        String[] ids = null;
        try {
            String command = "addNetworkAdapter";
            String message = "[TEST-RA02] Add a network adapter.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            context.addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            context.addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Deletion of the IP address information.
            command = "removeNetworkAddress";
            message = "Remove the network address.";
            String[] parameters1 = new String[]{ids[1]};
            testProxy.executeTestStep(command, message, parameters1);

            context.removeServiceListener(adapterListener);
            context.removeServiceListener(addressListener);

            // Confirmation of the service unregistration.
            assertThat(adapterListener.size()).as("Only zero NetworkAdapter service unregistering event is expected.").isZero();
            assertThat(addressListener.size()).as("Only one NetworkAddress service unregistering event is expected.").isOne();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter remove operation.
     */
    @Test
    public void testRemoveAction03() {
        String[] ids = null;
        try {
            String command = "addNetworkAdapter";
            String message = "[TEST-RA03] Add a network adapter.";
            String[] parameters = new String[]{"ipv4-private-use"};
            ids = testProxy.executeTestStep(command, message, parameters);

            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            context.addServiceListener(adapterListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            TestServiceListener addressListener = new TestServiceListener(ServiceEvent.UNREGISTERING);
            context.addServiceListener(addressListener, "(" + Constants.OBJECTCLASS + "=" + NetworkAddress.class.getName() + ")");

            // Deletion of the IP address information.
            command = "removeNetworkAdapter";
            message = "Remove the network adapter.";
            String[] parameters1 = new String[]{ids[0]};
            testProxy.executeTestStep(command, message, parameters1);

            context.removeServiceListener(adapterListener);
            context.removeServiceListener(addressListener);

            // Confirmation of the service unregistration.
            assertThat(adapterListener.size()).as("Only one NetworkAdapter service unregistering event is expected.").isOne();
            assertThat(addressListener.size()).as("Only one NetworkAddress service unregistering event is expected.").isOne();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[] {ids[0]});
            }
        }
    }
}
