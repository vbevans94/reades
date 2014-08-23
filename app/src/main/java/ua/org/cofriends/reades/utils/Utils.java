package ua.org.cofriends.reades.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {

    public static <T> List<T> toArrayList(Iterator<T> iterator) {
        List<T> list = new ArrayList<T>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }
}
