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
package com.orangelabs.rcs.ri.messaging.chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.text.TextUtils;

import com.gsma.services.rcs.chat.GroupChatIntent;
import com.orangelabs.rcs.ri.R;
import com.orangelabs.rcs.ri.utils.Utils;

/**
 * Group chat invitation receiver
 * 
 * @author Jean-Marc AUFFRET
 */
public class GroupChatInvitationReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// Display invitation notification
		GroupChatInvitationReceiver.addGroupChatInvitationNotification(context, intent);
    }
	
    /**
     * Add chat notification
     * 
     * @param context Context
     * @param invitation Intent invitation
     */
	public static void addGroupChatInvitationNotification(Context context, Intent invitation) {
    	// Get remote contact
		String contact = invitation.getStringExtra(GroupChatIntent.EXTRA_CONTACT);

		// Get chat ID
		String chatId = invitation.getStringExtra(GroupChatIntent.EXTRA_CHAT_ID);

		// Get subject
		String subject = invitation.getStringExtra(GroupChatIntent.EXTRA_SUBJECT);
		if (TextUtils.isEmpty(subject)) {
			subject = "<" + context.getString(R.string.label_no_subject) + ">";
		}

		// Create notification
		Intent intent = new Intent(invitation);
		intent.setClass(context, GroupChatView.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(chatId);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String notifTitle = context.getString(R.string.title_recv_group_chat, contact);
		Notification notif = new Notification(R.drawable.ri_notif_chat_icon, notifTitle, System.currentTimeMillis());
        notif.flags = Notification.FLAG_AUTO_CANCEL;
        String msg = context.getString(R.string.label_subject) + " " + subject;
        notif.setLatestEventInfo(context, notifTitle, msg, contentIntent);
		notif.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    	notif.defaults |= Notification.DEFAULT_VIBRATE;
        
        // Send notification
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(chatId, Utils.NOTIF_ID_GROUP_CHAT, notif);
    }
    
    /**
     * Remove chat notification
     * 
     * @param context Context
     * @param chatId Chat ID
     */
    public static void removeGroupChatNotification(Context context, String chatId) {
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(chatId, Utils.NOTIF_ID_GROUP_CHAT);
    }	
}