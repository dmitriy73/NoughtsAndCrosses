package dm.nac1;

import dm.nac1.notifications.Notification;

public interface Subscriber {
    public void handleEvent(Notification notification);
}
