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
package com.gsma.services.rcs;

import android.net.Uri;

/**
 * Delivery info (delivery information on group messages and group file
 * transfers)
 */
public class DeliveryInfo {

	public static final Uri CONTENT_URI = Uri
			.parse("content://com.gsma.services.rcs.provider.groupchatdeliveryinfo/deliveryinfo");

	public static final Uri CONTENT_MSG_URI = Uri
			.parse("content://com.gsma.services.rcs.provider.groupchatdeliveryinfo/deliveryinfo/messageid/");

	public static final String DELIVERY_STATUS = "status";

	public static final String REASON_CODE = "reason_code";

	public static final String MESSAGE_ID = "msg_id";

	public static final String CHAT_ID = "chat_id";

	public static final String CONTACT = "contact";

	public static final String TIMESTAMP_DELIVERED = "timestamp_delivered";

	public static final String TIMESTAMP_DISPLAYED = "timestamp_displayed";

	/**
	 * Status of the group delivery info
	 */
	public static class Status {
		/**
		 * The message or file-transfer has not received any delivery report
		 * for the specified contact.
		 */
		public static final int NOT_DELIVERED = 0;

		/**
		 * The message or file-transfer has received a delivery report for the
		 * specified contact
		 */
		public static final int DELIVERED = 1;

		/**
		 * The message or file-transfer has received a displayed report for the
		 * specified contact.
		 */
		public static final int DISPLAYED = 2;

		/**
		 * The message or file-transfer has received a delivery report failure
		 * for the specified contact.
		 */
		public static final int FAILED = 3;
	}

	/**
	 * Group chat delivery status reason
	 */
	public static class ReasonCode {

		/**
		 * No specific reason code specified.
		 */
		public static final int UNSPECIFIED = 0;

		/**
		 * A delivered-error delivery report has been received.
		 */
		public static final int FAILED_DELIVERY = 1;

		/**
		 * A displayed-error delivery report has been received.
		 */
		public static final int FAILED_DISPLAY = 2;
	}
}