package dm.nac1;

import dm.nac1.notifications.Notification;
import java.util.ArrayList;

public abstract class AbstractPublisher implements Publisher {
    private ArrayList<Subscriber> subscribers;

    public AbstractPublisher() {
        subscribers = new ArrayList<Subscriber>();
    }

    public void addSubscriber(Subscriber o) {
        if (!subscribers.contains(o)) {
            subscribers.add(o);
        }
    }

    public void deleteSubscriber(Subscriber o) {
        if (subscribers.contains(o)) {
            subscribers.remove(o);
        }
    }

    protected void notifySubscribers(Notification notification) {
        for (Subscriber o : subscribers) {
            o.handleEvent(notification);
        }
    }
}
