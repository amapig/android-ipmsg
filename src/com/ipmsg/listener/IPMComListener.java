/*
 *	IP Messenger Event Commnucation Listener Interface
 *		1997/10/16 (C) Copyright T.Kazawa (Digitune)
 */

package com.ipmsg.listener;

import ipmsg.IPMComEvent;

import java.util.EventListener;

public interface IPMComListener extends EventListener {

    public abstract void receive(IPMComEvent ev);
}
