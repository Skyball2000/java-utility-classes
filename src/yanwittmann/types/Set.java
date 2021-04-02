package yanwittmann.types;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A basic implementation of a set that does not allow adding the same object more than once.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 */
public class Set<E> extends ArrayList<E> {

    public boolean add(E o) {
        if (super.contains(o))
            return false;
        else super.add(o);
        return true;
    }

    public void add(int index, E o) {
        if (!super.contains(o)) super.add(index, o);
    }

    public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E e : c) changed = changed || add(e);
        return changed;
    }
}
