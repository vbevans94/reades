package ua.org.cofriends.reades.entity;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

import ua.org.cofriends.reades.utils.BusUtils;

public class Word extends SugarRecord<Word> {

    @Expose
    private final String word;

    @Expose
    private final String definition;

    public Word(String word, String definition) {
        this.word = word;
        this.definition = definition;
    }

    @SuppressWarnings("unused")
    public Word() {
        this(null, null);
    }

    public String getDefinition() {
        return definition;
    }

    public String getWord() {
        return word;
    }

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", definition='" + definition + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word1 = (Word) o;

        if (word != null ? !word.equals(word1.word) : word1.word != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return word != null ? word.hashCode() : 0;
    }

    public static class ListLoadedEvent extends BusUtils.Event<List<Word>> {

        public ListLoadedEvent(List<Word> words) {
            super(words);
        }
    }

    private static class Event extends BusUtils.Event<Word> {

        private Event(Word object) {
            super(object);
        }
    }

    public static class ViewEvent extends Event {

        public ViewEvent(Word object) {
            super(object);
        }
    }

    public static class RemoveEvent extends Event {

        public RemoveEvent(Word object) {
            super(object);
        }
    }
}
