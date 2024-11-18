package Utils.Observer;

import Utils.Events.Event;

public interface Observer<E extends Event> {
    void update(E e);
}


