/*
 * Copyright (C) 2014 Sony Mobile Communications Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.orangelabs.rcs.service.broadcaster;

import com.gsma.services.rcs.contacts.ContactId;
import com.gsma.services.rcs.extension.IMultimediaStreamingSessionListener;
import com.orangelabs.rcs.utils.logger.Logger;

import android.os.RemoteCallbackList;

/**
 * MultimediaStreamingSessionEventBroadcaster maintains the registering and
 * unregistering of IMultimediaStreamingSessionListener and also performs
 * broadcast events on these listeners upon the trigger of corresponding
 * callbacks.
 */
public class MultimediaStreamingSessionEventBroadcaster implements
		IMultimediaStreamingSessionEventBroadcaster {

	private final RemoteCallbackList<IMultimediaStreamingSessionListener> mMultimediaStreamingListeners = new RemoteCallbackList<IMultimediaStreamingSessionListener>();

	private final Logger logger = Logger.getLogger(getClass().getName());

	public MultimediaStreamingSessionEventBroadcaster() {
	}

	public void addMultimediaStreamingEventListener(IMultimediaStreamingSessionListener listener) {
		mMultimediaStreamingListeners.register(listener);
	}

	public void removeMultimediaStreamingEventListener(IMultimediaStreamingSessionListener listener) {
		mMultimediaStreamingListeners.unregister(listener);
	}

	public void broadcastNewPayload(ContactId contact, String sessionId, byte[] content) {
		final int N = mMultimediaStreamingListeners.beginBroadcast();
		for (int i = 0; i < N; i++) {
			try {
				mMultimediaStreamingListeners.getBroadcastItem(i).onNewPayload(contact, sessionId,
						content);
			} catch (Exception e) {
				if (logger.isActivated()) {
					logger.error("Can't notify listener", e);
				}
			}
		}
		mMultimediaStreamingListeners.finishBroadcast();
	}

	public void broadcastMultimediaStreamingStateChanged(ContactId contact, String sessionId,
			int state) {
		final int N = mMultimediaStreamingListeners.beginBroadcast();
		for (int i = 0; i < N; i++) {
			try {
				// TODO : Handle reason code in CR009
				mMultimediaStreamingListeners.getBroadcastItem(i)
						.onMultimediaStreamingStateChanged(contact, sessionId, state);
			} catch (Exception e) {
				if (logger.isActivated()) {
					logger.error("Can't notify listener", e);
				}
			}
		}
		mMultimediaStreamingListeners.finishBroadcast();
	}
}