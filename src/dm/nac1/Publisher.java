package dm.nac1;

import dm.nac1.notifications.Notification;

public interface Publisher {
    public void addSubscriber(Subscriber o);
    public void deleteSubscriber(Subscriber o);
}
