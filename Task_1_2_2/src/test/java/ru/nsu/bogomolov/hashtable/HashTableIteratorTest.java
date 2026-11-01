package ru.nsu.bogomolov.hashtable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

/** Проверяет обход и обнаружение изменений таблицы. */
class HashTableIteratorTest {
    /** Проверяет обход пустой таблицы. */
    @Test
    void emptyIteratorHasNoElements() {
        Iterator<Map.Entry<String, Integer>> iterator = new HashTable<String, Integer>().iterator();

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    /** Проверяет обход всех записей без требования к их порядку. */
    @Test
    void iteratesOverAllEntries() {
        HashTable<Integer, String> table = new HashTable<>();
        Map<Integer, String> expected = new HashMap<>();
        for (int i = 0; i < 40; i++) {
            table.put(i, "value" + i);
            expected.put(i, "value" + i);
        }

        Map<Integer, String> actual = new HashMap<>();
        for (Map.Entry<Integer, String> entry : table) {
            actual.put(entry.getKey(), entry.getValue());
        }
        assertEquals(expected, actual);
    }

    /** Проверяет, что возвращенная пара не дает изменить таблицу. */
    @Test
    void returnedEntryIsImmutable() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);
        Map.Entry<String, Integer> entry = table.iterator().next();

        assertEquals("one", entry.getKey());
        assertEquals(1, entry.getValue());
        assertThrows(UnsupportedOperationException.class, () -> entry.setValue(2));
        assertEquals(1, table.get("one"));
    }

    /** Проверяет обнаружение добавления в hasNext. */
    @Test
    void detectsInsertionInHasNext() {
        HashTable<String, Integer> table = new HashTable<>();
        Iterator<Map.Entry<String, Integer>> iterator = table.iterator();
        table.put("one", 1);

        assertThrows(ConcurrentModificationException.class, iterator::hasNext);
    }

    /** Проверяет обнаружение удаления в next. */
    @Test
    void detectsRemovalInNext() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);
        Iterator<Map.Entry<String, Integer>> iterator = table.iterator();
        table.remove("one");

        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    /** Проверяет обнаружение обновления значения. */
    @Test
    void detectsValueUpdate() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);
        Iterator<Map.Entry<String, Integer>> iterator = table.iterator();
        table.update("one", 2);

        assertThrows(ConcurrentModificationException.class, iterator::hasNext);
    }

    /** Проверяет обнаружение замены через put. */
    @Test
    void detectsPutReplacement() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);
        Iterator<Map.Entry<String, Integer>> iterator = table.iterator();
        table.put("one", 2);

        assertThrows(ConcurrentModificationException.class, iterator::next);
    }

    /** Проверяет, что неудачное удаление не нарушает обход. */
    @Test
    void unsuccessfulRemovalKeepsIteratorValid() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);
        Iterator<Map.Entry<String, Integer>> iterator = table.iterator();
        table.remove("missing");
        table.remove("one", 2);

        assertTrue(iterator.hasNext());
        assertEquals("one", iterator.next().getKey());
        assertFalse(iterator.hasNext());
    }

    /** Проверяет запрет удаления через итератор. */
    @Test
    void iteratorRemoveIsUnsupported() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);
        Iterator<Map.Entry<String, Integer>> iterator = table.iterator();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }
}
