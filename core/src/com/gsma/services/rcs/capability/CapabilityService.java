/*******************************************************************************
 * Software Name : RCS IMS Stack
 *
 * Copyright (C) 2010 France Telecom S.A.
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
 ******************************************************************************/

package com.gsma.services.rcs.capability;

import java.util.Iterator;
import java.util.Set;

import com.gsma.services.rcs.capability.ICapabilityService;

import com.gsma.services.rcs.JoynContactFormatException;
import com.gsma.services.rcs.JoynService;
import com.gsma.services.rcs.JoynServiceException;
import com.gsma.services.rcs.JoynServiceListener;
import com.gsma.services.rcs.JoynServiceNotAvailableException;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IInterface;

/**
 * Capability service offers the main entry point to read capabilities
 * of remote contacts, to initiate capability discovery and to receive
 * capabilities updates. Several applications may connect/disconnect
 * to the API.
 * 
 * The parameter contact in the API supports the following formats:
 * MSISDN in national or international format, SIP address, SIP-URI
 * or Tel-URI.
 * 
 * @author Jean-Marc AUFFRET
 */
public class CapabilityService extends JoynService {
    /**
     * Intent broadcasted to discover extensions
     * 
     * @see CapabilityService.EXTENSION_MIME_TYPE
     */
    public final static String INTENT_EXTENSIONS = "com.gsma.services.rcs.capability.EXTENSION";
    
	/**
	 * Extension MIME type
	 */
	public final static String EXTENSION_MIME_TYPE = "com.gsma.services.rcs";

	/**
	 * API
	 */
	private ICapabilityService api = null;
	
    /**
     * Constructor
     * 
     * @param ctx Application context
     * @param listener Service listener
     */
    public CapabilityService(Context ctx, JoynServiceListener listener) {
    	super(ctx, listener);
    }

    /**
     * Connects to the API
     */
    public void connect() {
    	ctx.bindService(new Intent(ICapabilityService.class.getName()), apiConnection, 0);
    }
    
    /**
     * Disconnects from the API
     */
    public void disconnect() {
    	try {
    		ctx.unbindService(apiConnection);
        } catch(IllegalArgumentException e) {
        	// Nothing to do
        }
    }

	/**
	 * Set API interface
	 * 
	 * @param api API interface
	 */
    protected void setApi(IInterface api) {
    	super.setApi(api);
    	
        this.api = (ICapabilityService)api;
    }
    
    /**
	 * Service connection
	 */
	private ServiceConnection apiConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
        	setApi(ICapabilityService.Stub.asInterface(service));
        	if (serviceListener != null) {
        		serviceListener.onServiceConnected();
        	}
        }

        public void onServiceDisconnected(ComponentName className) {
        	setApi(null);
        	if (serviceListener != null) {
        		serviceListener.onServiceDisconnected(Error.CONNECTION_LOST);
        	}
        }
    };
    
    /**
     * Returns the capabilities supported by the local end user. The supported
     * capabilities are fixed by the MNO and read during the provisioning.
     * 
     * @return Capabilities
     * @throws JoynServiceException
     */
    public Capabilities getMyCapabilities() throws JoynServiceException {
		if (api != null) {
			try {
				return api.getMyCapabilities();
			} catch(Exception e) {
				throw new JoynServiceException(e.getMessage());
			}
		} else {
			throw new JoynServiceNotAvailableException();
		}
    }
    
    /**
     * Returns the capabilities of a given contact from the local database. This
     * method doesn�t request any network update to the remote contact. The parameter
     * contact supports the following formats: MSISDN in national or international
     * format, SIP address, SIP-URI or Tel-URI. If the format of the contact is not
     * supported an exception is thrown.
     * 
     * @param contact Contact
     * @return Capabilities
     * @throws JoynServiceException
	 * @throws JoynContactFormatException
     */
    public Capabilities getContactCapabilities(String contact) throws JoynServiceException, JoynContactFormatException {
		if (api != null) {
			try {
				return api.getContactCapabilities(contact);
			} catch(Exception e) {
				throw new JoynServiceException(e.getMessage());
			}
		} else {
			throw new JoynServiceNotAvailableException();
		}
    }    

    /**
	 * Requests capabilities to a remote contact. This method initiates in background
	 * a new capability request to the remote contact by sending a SIP OPTIONS. The
	 * result of the capability request is sent asynchronously via callback method of
	 * the capabilities listener. A capability refresh is only sent if the timestamp
	 * associated to the capability has expired (the expiration value is fixed via MNO
	 * provisioning). The parameter contact supports the following formats: MSISDN in
	 * national or international format, SIP address, SIP-URI or Tel-URI. If the format
	 * of the contact is not supported an exception is thrown. The result of the
	 * capability refresh request is provided to all the clients that have registered
	 * the listener for this event.
   	 * 
	 * @param contact Contact
	 * @throws JoynServiceException
	 * @throws JoynContactFormatException
	 */
	public void requestContactCapabilities(String contact) throws JoynServiceException, JoynContactFormatException {
		if (api != null) {
			try {
				api.requestContactCapabilities(contact);
			} catch(Exception e) {
				throw new JoynServiceException(e.getMessage());
			}
		} else {
			throw new JoynServiceNotAvailableException();
		}
	}

    /**
     * Requests capabilities for a group of remote contacts. This method initiates
     * in background new capability requests to the remote contact by sending a
     * SIP OPTIONS. The result of the capability request is sent asynchronously via
     * callback method of the capabilities listener. A capability refresh is only
     * sent if the timestamp associated to the capability has expired (the expiration
     * value is fixed via MNO provisioning). The parameter contact supports the
     * following formats: MSISDN in national or international format, SIP address,
     * SIP-URI or Tel-URI. If the format of the contact is not supported an exception
     * is thrown. The result of the capability refresh request is provided to all the
     * clients that have registered the listener for this event.
	 * 
	 * @param contacts List of contacts
	 * @throws JoynServiceException
	 * @throws JoynContactFormatException
	 */
	public void requestContactCapabilities(Set<String> contacts) throws JoynServiceException, JoynContactFormatException {
		Iterator<String> values = contacts.iterator();
		while(values.hasNext()) {
			requestContactCapabilities(values.next());
		}
	}

    /**
	 * Requests capabilities for all contacts existing in the local address book. This
	 * method initiates in background new capability requests for each contact of the
	 * address book by sending SIP OPTIONS. The result of a capability request is sent
	 * asynchronously via callback method of the capabilities listener. A capability
	 * refresh is only sent if the timestamp associated to the capability has expired
	 * (the expiration value is fixed via MNO provisioning). The result of the capability
	 * refresh request is provided to all the clients that have registered the listener
	 * for this event.
	 * 
	 * @param contacts List of contacts
	 * @throws JoynServiceException
	 */
	public void requestAllContactsCapabilities() throws JoynServiceException {
		if (api != null) {
			try {
				api.requestAllContactsCapabilities();
			} catch(Exception e) {
				throw new JoynServiceException(e.getMessage());
			}
		} else {
			throw new JoynServiceNotAvailableException();
		}
	}

	/**
	 * Registers a capabilities listener on any contact
	 * 
	 * @param listener Capabilities listener
	 * @throws JoynServiceException
	 */
	public void addCapabilitiesListener(CapabilitiesListener listener) throws JoynServiceException {
		if (api != null) {
			try {
				api.addCapabilitiesListener(listener);
			} catch(Exception e) {
				throw new JoynServiceException(e.getMessage());
			}
		} else {
			throw new JoynServiceNotAvailableException();
		}
	}

	/**
	 * Unregisters a capabilities listener
	 * 
	 * @param listener Capabilities listener
	 * @throws JoynServiceException
	 */
	public void removeCapabilitiesListener(CapabilitiesListener listener) throws JoynServiceException {
		if (api != null) {
			try {
				api.removeCapabilitiesListener(listener);
			} catch(Exception e) {
				throw new JoynServiceException(e.getMessage());
			}
		} else {
			throw new JoynServiceNotAvailableException();
		}
	}

	/**
	 * Registers a capabilities listener on a list of contacts
	 * 
	 * @param contacts Set of contacts
	 * @param listener Capabilities listener
	 * @throws JoynServiceException
	 * @throws JoynContactFormatException
	 */
	public void addCapabilitiesListener(Set<String> contacts, CapabilitiesListener listener) throws JoynServiceException, JoynContactFormatException {
		if (api != null) {
			try {
				Iterator<String> list = contacts.iterator();
				while(list.hasNext()) { 
					String contact = list.next();
					api.addContactCapabilitiesListener(contact, listener);
				}
			} catch(Exception e) {
				throw new JoynServiceException(e.getMessage());
			}
		} else {
			throw new JoynServiceNotAvailableException();
		}
	}

	/**
	 * Unregisters a capabilities listener on a list of contacts
	 * 
	 * @param contacts Set of contacts
	 * @param listener Capabilities listener
	 * @throws JoynServiceException
	 * @throws JoynContactFormatException
	 */
	public void removeCapabilitiesListener(Set<String> contacts, CapabilitiesListener listener) throws JoynServiceException, JoynContactFormatException {
		if (api != null) {
			try {
				Iterator<String> list = contacts.iterator();
				while(list.hasNext()) { 
					String contact = list.next();
					api.removeContactCapabilitiesListener(contact, listener);
				}
			} catch(Exception e) {
				throw new JoynServiceException(e.getMessage());
			}
		} else {
			throw new JoynServiceNotAvailableException();
		}
	}
}
