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

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.service.networkadapter.NetworkAdapter;
import org.osgi.test.cases.networkadapter.util.NetworkTestProxy;
import org.osgi.test.cases.networkadapter.util.TestServiceListener;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.tck.step.TestStepProxy;

public class NetworkAdapterTestCase {

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
     * Tests NetworkAdapter of the loopback interface.
     */
    @Test
    public void testNetworkAdapter01() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-ADA01] Add a loopback network adapter.";
            String[] parameters = new String[]{"loopback"};
            ids = testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);

			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertThat(type instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE).isTrue();
            assertThat(displayName instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isTrue();
            assertThat(name instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME).isTrue();
            assertThat(hardwareAddress instanceof byte[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(isLoopback instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isTrue();
            assertThat(isPointToPoint instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isTrue();
            assertThat(isUp instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP).isTrue();
            assertThat(isVirtual instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isTrue();
            assertThat(supportsMulticast instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isTrue();
            assertThat(subInterface instanceof String[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE).isTrue();

            NetworkAdapter networkAdapter = (NetworkAdapter) context.getService(adapterListener.get(0));
            assertThat(networkAdapter.getNetworkAdapterType()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE).isEqualTo(type);
            assertThat(networkAdapter.getDisplayName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isEqualTo(displayName);
            assertThat(networkAdapter.getName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME).isEqualTo(name);
            assertThat(Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress())).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();

            assertThat(networkAdapter.isLoopback()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isEqualTo(((Boolean)isLoopback).booleanValue());
            assertThat(networkAdapter.isLoopback()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isTrue();

            assertThat(networkAdapter.isPointToPoint()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isEqualTo(((Boolean)isPointToPoint).booleanValue());
            assertThat(networkAdapter.isPointToPoint()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isFalse();

            assertThat(networkAdapter.isUp()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP).isEqualTo(((Boolean)isUp).booleanValue());

            assertThat(networkAdapter.isVirtual()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isEqualTo(((Boolean)isVirtual).booleanValue());
            assertThat(networkAdapter.isVirtual()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isFalse();

            assertThat(networkAdapter.supportsMulticast()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isEqualTo(((Boolean)supportsMulticast).booleanValue());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter of the point-to-point interface.
     */
    @Test
    public void testNetworkAdapter02() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-ADA02] Add a point-to-point network adapter.";
            String[] parameters = new String[]{"point-to-point"};
            ids = testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);

			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertThat(type instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE).isTrue();
            assertThat(displayName instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isTrue();
            assertThat(name instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME).isTrue();
            assertThat(hardwareAddress instanceof byte[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(isLoopback instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isTrue();
            assertThat(isPointToPoint instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isTrue();
            assertThat(isUp instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP).isTrue();
            assertThat(isVirtual instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isTrue();
            assertThat(supportsMulticast instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isTrue();
            assertThat(subInterface instanceof String[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE).isTrue();

            NetworkAdapter networkAdapter = (NetworkAdapter) context.getService(adapterListener.get(0));
            assertThat(networkAdapter.getNetworkAdapterType()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE).isEqualTo(type);
            assertThat(networkAdapter.getDisplayName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isEqualTo(displayName);
            assertThat(networkAdapter.getName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME).isEqualTo(name);
            assertThat(Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress())).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();

            assertThat(networkAdapter.isLoopback()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isEqualTo(((Boolean)isLoopback).booleanValue());
            assertThat(networkAdapter.isLoopback()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isFalse();

            assertThat(networkAdapter.isPointToPoint()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isEqualTo(((Boolean)isPointToPoint).booleanValue());
            assertThat(networkAdapter.isPointToPoint()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isTrue();

            assertThat(networkAdapter.isUp()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP).isEqualTo(((Boolean)isUp).booleanValue());
            assertThat(networkAdapter.isVirtual()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isEqualTo(((Boolean)isVirtual).booleanValue());
            assertThat(networkAdapter.supportsMulticast()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isEqualTo(((Boolean)supportsMulticast).booleanValue());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter of the interface that starts.
     */
    @Test
    public void testNetworkAdapter03() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-ADA03] Add an up network adapter.";
            String[] parameters = new String[]{"up"};
            ids = testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);

			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertThat(type instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE).isTrue();
            assertThat(displayName instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isTrue();
            assertThat(name instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME).isTrue();
            assertThat(hardwareAddress instanceof byte[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(isLoopback instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isTrue();
            assertThat(isPointToPoint instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isTrue();
            assertThat(isUp instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP).isTrue();
            assertThat(isVirtual instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isTrue();
            assertThat(supportsMulticast instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isTrue();
            assertThat(subInterface instanceof String[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE).isTrue();

            NetworkAdapter networkAdapter = (NetworkAdapter) context.getService(adapterListener.get(0));
            assertThat(networkAdapter.getNetworkAdapterType()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE).isEqualTo(type);
            assertThat(networkAdapter.getDisplayName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isEqualTo(displayName);
            assertThat(networkAdapter.getName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME).isEqualTo(name);
            assertThat(Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress())).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(networkAdapter.isLoopback()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isEqualTo(((Boolean)isLoopback).booleanValue());
            assertThat(networkAdapter.isPointToPoint()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isEqualTo(((Boolean)isPointToPoint).booleanValue());

            assertThat(networkAdapter.isUp()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP).isEqualTo(((Boolean)isUp).booleanValue());
            assertThat(networkAdapter.isUp()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP).isTrue();

            assertThat(networkAdapter.isVirtual()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isEqualTo(((Boolean)isVirtual).booleanValue());
            assertThat(networkAdapter.supportsMulticast()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isEqualTo(((Boolean)supportsMulticast).booleanValue());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter of the interface that stops.
     */
    @Test
    public void testNetworkAdapter04() {

        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-ADA04] Add a down network adapter.";
            String[] parameters = new String[]{"down"};
            ids = testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);

			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertThat(type instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE).isTrue();
            assertThat(displayName instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isTrue();
            assertThat(name instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME).isTrue();
            assertThat(hardwareAddress instanceof byte[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(isLoopback instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isTrue();
            assertThat(isPointToPoint instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isTrue();
            assertThat(isUp instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP).isTrue();
            assertThat(isVirtual instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isTrue();
            assertThat(supportsMulticast instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isTrue();
            assertThat(subInterface instanceof String[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE).isTrue();

            NetworkAdapter networkAdapter = (NetworkAdapter) context.getService(adapterListener.get(0));
            assertThat(networkAdapter.getNetworkAdapterType()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE).isEqualTo(type);
            assertThat(networkAdapter.getDisplayName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isEqualTo(displayName);
            assertThat(networkAdapter.getName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME).isEqualTo(name);
            assertThat(Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress())).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(networkAdapter.isLoopback()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isEqualTo(((Boolean)isLoopback).booleanValue());
            assertThat(networkAdapter.isPointToPoint()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isEqualTo(((Boolean)isPointToPoint).booleanValue());

            assertThat(networkAdapter.isUp()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP).isEqualTo(((Boolean)isUp).booleanValue());
            assertThat(networkAdapter.isUp()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP).isFalse();

            assertThat(networkAdapter.isVirtual()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isEqualTo(((Boolean)isVirtual).booleanValue());
            assertThat(networkAdapter.supportsMulticast()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isEqualTo(((Boolean)supportsMulticast).booleanValue());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);

        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter of the virtual interface.
     */
    @Test
    public void testNetworkAdapter05() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-ADA05] Add a virtual network adapter.";
            String[] parameters = new String[]{"virtual"};
            ids = testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);

			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertThat(type instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE).isTrue();
            assertThat(displayName instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isTrue();
            assertThat(name instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME).isTrue();
            assertThat(hardwareAddress instanceof byte[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(isLoopback instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isTrue();
            assertThat(isPointToPoint instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isTrue();
            assertThat(isUp instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP).isTrue();
            assertThat(isVirtual instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isTrue();
            assertThat(supportsMulticast instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isTrue();
            assertThat(subInterface instanceof String[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE).isTrue();

            NetworkAdapter networkAdapter = (NetworkAdapter) context.getService(adapterListener.get(0));
            assertThat(networkAdapter.getNetworkAdapterType()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE).isEqualTo(type);
            assertThat(networkAdapter.getDisplayName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isEqualTo(displayName);
            assertThat(networkAdapter.getName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME).isEqualTo(name);
            assertThat(Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress())).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(networkAdapter.isLoopback()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isEqualTo(((Boolean)isLoopback).booleanValue());
            assertThat(networkAdapter.isPointToPoint()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isEqualTo(((Boolean)isPointToPoint).booleanValue());
            assertThat(networkAdapter.isUp()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP).isEqualTo(((Boolean)isUp).booleanValue());

            assertThat(networkAdapter.isVirtual()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isEqualTo(((Boolean)isVirtual).booleanValue());
            assertThat(networkAdapter.isVirtual()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isTrue();

            assertThat(networkAdapter.supportsMulticast()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isEqualTo(((Boolean)supportsMulticast).booleanValue());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter of the interface that supports multicast.
     */
    @Test
    public void testNetworkAdapter06() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-ADA06] Add a supported multicast network adapter.";
            String[] parameters = new String[]{"multicast"};
            ids = testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);
			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertThat(type instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE).isTrue();
            assertThat(displayName instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isTrue();
            assertThat(name instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME).isTrue();
            assertThat(hardwareAddress instanceof byte[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(isLoopback instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isTrue();
            assertThat(isPointToPoint instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isTrue();
            assertThat(isUp instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP).isTrue();
            assertThat(isVirtual instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isTrue();
            assertThat(supportsMulticast instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isTrue();
            assertThat(subInterface instanceof String[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE).isTrue();

            NetworkAdapter networkAdapter = (NetworkAdapter) context.getService(adapterListener.get(0));
            assertThat(networkAdapter.getNetworkAdapterType()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE).isEqualTo(type);
            assertThat(networkAdapter.getDisplayName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isEqualTo(displayName);
            assertThat(networkAdapter.getName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME).isEqualTo(name);
            assertThat(Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress())).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(networkAdapter.isLoopback()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isEqualTo(((Boolean)isLoopback).booleanValue());
            assertThat(networkAdapter.isPointToPoint()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isEqualTo(((Boolean)isPointToPoint).booleanValue());
            assertThat(networkAdapter.isUp()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP).isEqualTo(((Boolean)isUp).booleanValue());
            assertThat(networkAdapter.isVirtual()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isEqualTo(((Boolean)isVirtual).booleanValue());

            assertThat(networkAdapter.supportsMulticast()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isEqualTo(((Boolean)supportsMulticast).booleanValue());
            assertThat(networkAdapter.supportsMulticast()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isTrue();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage(), e);
        } finally {
            if (ids != null) {
                testProxy.executeTestStep("removeNetworkAdapter", "Remove the remaining network adapter.", new String[]{ids[0]});
            }
        }
    }

    /**
     * Tests NetworkAdapter of the interface that dose not supports multicast.
     */
    @Test
    public void testNetworkAdapter07() {
        String[] ids = null;
        try {
            TestServiceListener adapterListener = new TestServiceListener(ServiceEvent.REGISTERED);
            context.addServiceListener(adapterListener, "(" +  Constants.OBJECTCLASS + "=" + NetworkAdapter.class.getName() + ")");
            String command = "addNetworkAdapter";
            String message = "[TEST-ADA07] Add an unsupported multicast network adapter.";
            String[] parameters = new String[]{"no-multicast"};
            ids = testProxy.executeTestStep(command, message, parameters);

            context.removeServiceListener(adapterListener);

			ServiceReference< ? > ref = adapterListener.get(0);
            Object type = ref.getProperty(NetworkAdapter.NETWORKADAPTER_TYPE);
            Object displayName = ref.getProperty(NetworkAdapter.NETWORKADAPTER_DISPLAYNAME);
            Object name = ref.getProperty(NetworkAdapter.NETWORKADAPTER_NAME);
            Object hardwareAddress = ref.getProperty(NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS);
            Object isLoopback = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK);
            Object isPointToPoint = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT);
            Object isUp = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_UP);
            Object isVirtual = ref.getProperty(NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL);
            Object supportsMulticast = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST);
            Object subInterface = ref.getProperty(NetworkAdapter.NETWORKADAPTER_SUBINTERFACE);

            assertThat(type instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_TYPE).isTrue();
            assertThat(displayName instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isTrue();
            assertThat(name instanceof String).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_NAME).isTrue();
            assertThat(hardwareAddress instanceof byte[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(isLoopback instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isTrue();
            assertThat(isPointToPoint instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isTrue();
            assertThat(isUp instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_UP).isTrue();
            assertThat(isVirtual instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isTrue();
            assertThat(supportsMulticast instanceof Boolean).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isTrue();
            assertThat(subInterface instanceof String[]).as("The following service property is not correct: " + NetworkAdapter.NETWORKADAPTER_SUBINTERFACE).isTrue();

            NetworkAdapter networkAdapter = (NetworkAdapter) context.getService(adapterListener.get(0));
            assertThat(networkAdapter.getNetworkAdapterType()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_TYPE).isEqualTo(type);
            assertThat(networkAdapter.getDisplayName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_DISPLAYNAME).isEqualTo(displayName);
            assertThat(networkAdapter.getName()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_NAME).isEqualTo(name);
            assertThat(Arrays.equals((byte[])hardwareAddress, networkAdapter.getHardwareAddress())).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_HARDWAREADDRESS).isTrue();
            assertThat(networkAdapter.isLoopback()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_LOOPBACK).isEqualTo(((Boolean)isLoopback).booleanValue());
            assertThat(networkAdapter.isPointToPoint()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_POINTTOPOINT).isEqualTo(((Boolean)isPointToPoint).booleanValue());
            assertThat(networkAdapter.isUp()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_UP).isEqualTo(((Boolean)isUp).booleanValue());
            assertThat(networkAdapter.isVirtual()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_IS_VIRTUAL).isEqualTo(((Boolean)isVirtual).booleanValue());

            assertThat(networkAdapter.supportsMulticast()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isEqualTo(((Boolean)supportsMulticast).booleanValue());
            assertThat(networkAdapter.supportsMulticast()).as("The following NetworkAdapter information does not match: "+ NetworkAdapter.NETWORKADAPTER_SUPPORTS_MULTICAST).isFalse();

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
