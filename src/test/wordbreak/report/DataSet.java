package test.wordbreak.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataSet implements Serializable {
	private List<DataItem> items = new ArrayList<>();

	public void setList(List<DataItem> dataStructures) {
		items = dataStructures;
	}

	public List<DataItem> getList() {
		items.add(createItem());
		
		return items;
	}

	private DataItem createItem() {
		DataItem item = new DataItem();
		String test = "This is a verylongtext which could be split into twoormorelinesdependingon the column width";
		item.setCol1(test);
		item.setCol2(test);
		item.setCol3(test);
		item.setCol4(test);
		item.setCol5(test);
		item.setCol6(test);
		return item;
	}
}
