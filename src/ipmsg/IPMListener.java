/*
 *	IP Messenger Event Listener Interface
 *		1997/10/16 (C) Copyright T.Kazawa (Digitune)
 */

package ipmsg;

import java.util.EventListener;

public interface IPMListener extends EventListener {
    public void eventOccured(IPMEvent ev);
}
