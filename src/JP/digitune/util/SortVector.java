/*
 *	Sort Vector Class
 *		1998/02/13 (C)Copyright T.Kazawa(Digitune)
 */

package JP.digitune.util;

import java.util.Vector;

public class SortVector extends Vector {
	Vector keys = new Vector();
	
	public SortVector() {
		super();
	}
	
	public SortVector(int i) {
		super(i);
	}
	
	public SortVector(int i, int j) {
		super(i, j);
	}
	
	public synchronized void addElement(String key, Object obj) {
		int index = 0;
		try {
			for (; key.compareTo((String) keys.elementAt(index)) > 0; index++);
		} catch (ArrayIndexOutOfBoundsException ex) {}
		insertElementAt(obj, index);
		keys.insertElementAt(key, index);
	}
}