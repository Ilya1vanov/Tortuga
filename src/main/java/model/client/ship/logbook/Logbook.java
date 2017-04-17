package model.client.ship.logbook;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Ilya Ivanov
 */
public class Logbook implements Serializable {
    Collection<Record> records = new ArrayList<>(100);

    public void log(String author, String record) {
        records.add(new Record(author, record));
    }

    public ImmutableCollection<Record> getRecords() {
        return ImmutableList.copyOf(records);
    }

    public static class Record {
        private String author;
        private String message;

        Record(String author, String message) {
            this.author = author;
            this.message = message;
        }

        public String getAuthor() {
            return author;
        }

        public String getMessage() {
            return message;
        }
    }
}
