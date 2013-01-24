package by.gsu.isheremetov.models.containers;

import by.gsu.isheremetov.models.Service;
import by.gsu.isheremetov.models.Subscribe;

public class SubscribesContainer extends Container<Subscribe> {
	public boolean contains(Service service) {
		for (int i = 0; i < size(); i++) {
			if (get(i).getServiceID() == service.getId()) {
				return true;
			}
		}
		return false;
	}
}
