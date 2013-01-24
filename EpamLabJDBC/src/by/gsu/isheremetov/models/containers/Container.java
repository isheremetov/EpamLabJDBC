package by.gsu.isheremetov.models.containers;

import java.util.ArrayList;

public class Container<TableObject> {
	private ArrayList<TableObject> arrayList;

	public Container() {
		arrayList = new ArrayList<TableObject>();
	}

	public void add(TableObject obj) {
		arrayList.add(obj);
	}

	public TableObject get(int index) {
		return arrayList.get(index);
	}

	public int size() {
		return arrayList.size();
	}

	public void clear() {
		arrayList.clear();
	}
}
