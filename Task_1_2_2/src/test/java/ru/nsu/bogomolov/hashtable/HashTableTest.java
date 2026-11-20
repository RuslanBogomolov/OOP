package ru.nsu.bogomolov.hashtable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

/** Проверяет основные операции хеш-таблицы. */
class HashTableTest {
    /** Проверяет состояние новой таблицы. */
    @Test
    void newTableIsEmpty() {
        HashTable<String, Integer> table = new HashTable<>();

        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
        assertFalse(table.containsKey("missing"));
        assertNull(table.get("missing"));
        assertNull(table.remove("missing"));
        assertFalse(table.remove("missing", 1));
        assertEquals("{}", table.toString());
    }

    /** Проверяет добавление, поиск и замену значения через put. */
    @Test
    void putInsertsAndReplaces() {
        HashTable<String, Integer> table = new HashTable<>();

        assertNull(table.put("one", 1));
        assertEquals(1, table.get("one"));
        assertEquals(1, table.put("one", 2));
        assertEquals(2, table.get("one"));
        assertEquals(1, table.size());
        assertFalse(table.isEmpty());
    }

    /** Проверяет обновление существующего ключа и отказ для отсутствующего. */
    @Test
    void updateRequiresExistingKey() {
        HashTable<String, Number> table = new HashTable<>();
        table.put("one", 1);

        assertEquals(1, table.update("one", 1.0));
        assertEquals(1.0, table.get("one"));
        assertEquals(1, table.size());
        assertThrows(NoSuchElementException.class, () -> table.update("missing", 3));
    }

    /** Проверяет наличие ключей, значения которых равны null. */
    @Test
    void supportsNullKeysAndValues() {
        HashTable<String, Integer> table = new HashTable<>();

        table.put(null, 10);
        table.put("empty", null);
        assertEquals(10, table.get(null));
        assertTrue(table.containsKey("empty"));
        assertNull(table.get("empty"));
        assertTrue(table.remove("empty", null));
        assertFalse(table.containsKey("empty"));
        assertEquals(10, table.remove(null));
        assertTrue(table.isEmpty());
    }

    /** Проверяет, что удаление пары требует совпадения значения. */
    @Test
    void removePairChecksValue() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);

        assertFalse(table.remove("one", 2));
        assertTrue(table.containsKey("one"));
        assertTrue(table.remove("one", 1));
        assertFalse(table.containsKey("one"));
        assertFalse(table.remove("one", 1));
    }

    /** Проверяет удаление каждого узла из цепочки коллизий. */
    @Test
    void removesHeadMiddleAndTailOfCollisionChain() {
        HashTable<CollidingKey, Integer> table = new HashTable<>();
        CollidingKey first = new CollidingKey("first");
        CollidingKey middle = new CollidingKey("middle");
        CollidingKey last = new CollidingKey("last");
        table.put(first, 1);
        table.put(middle, 2);
        table.put(last, 3);

        assertEquals(2, table.get(new CollidingKey("middle")));
        assertEquals(2, table.remove(middle));
        assertEquals(3, table.remove(last));
        assertEquals(1, table.remove(first));
        assertTrue(table.isEmpty());
    }

    /** Проверяет сохранность записей после расширения и уменьшения массива. */
    @Test
    void resizesWhileKeepingEntries() {
        HashTable<Integer, Integer> table = new HashTable<>();
        for (int i = 0; i < 200; i++) {
            table.put(i, i * 2);
        }
        assertEquals(200, table.size());
        for (int i = 0; i < 200; i++) {
            assertEquals(i * 2, table.get(i));
        }

        for (int i = 0; i < 190; i++) {
            assertEquals(i * 2, table.remove(i));
        }
        assertEquals(10, table.size());
        for (int i = 190; i < 200; i++) {
            assertEquals(i * 2, table.get(i));
        }
        for (int i = 190; i < 200; i++) {
            table.remove(i);
        }
        assertTrue(table.isEmpty());
        table.put(500, 1000);
        assertEquals(1000, table.get(500));
    }

    /** Проверяет равенство независимо от порядка вставки и числа корзин. */
    @Test
    void equalsIgnoresInsertionOrderAndCapacity() {
        HashTable<Integer, Integer> first = new HashTable<>();
        HashTable<Integer, Integer> second = new HashTable<>();
        first.put(null, null);
        for (int i = 0; i < 20; i++) {
            first.put(i, i);
        }
        for (int i = 19; i >= 0; i--) {
            second.put(i, i);
        }
        second.put(null, null);

        assertEquals(first, first);
        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, null);
        assertNotEquals(first, "not a table");
    }

    /** Проверяет различие таблиц по размеру, ключу и значению. */
    @Test
    void equalsFindsDifferentEntries() {
        HashTable<String, Integer> first = new HashTable<>();
        HashTable<String, Integer> second = new HashTable<>();
        first.put("a", 1);
        assertNotEquals(first, second);

        second.put("b", 1);
        assertNotEquals(first, second);

        second.remove("b");
        second.put("a", 2);
        assertNotEquals(first, second);
    }

    /** Проверяет читаемый формат строки. */
    @Test
    void toStringShowsEntries() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("one", 1);
        assertEquals("{one=1}", table.toString());

        table.put("two", 2);
        String text = table.toString();
        assertTrue(text.startsWith("{"));
        assertTrue(text.endsWith("}"));
        assertTrue(text.contains("one=1"));
        assertTrue(text.contains("two=2"));
        assertTrue(text.contains(", "));
    }

    /** Ключ с постоянным хеш-кодом для проверки коллизий. */
    private static final class CollidingKey {
        /** Имя ключа. */
        private final String name;

        /**
         * Создает ключ.
         *
         * @param name отличительное имя
         */
        private CollidingKey(String name) {
            this.name = name;
        }

        /**
         * Сравнивает ключи по имени.
         *
         * @param object другой объект
         * @return {@code true}, если имена совпадают
         */
        @Override
        public boolean equals(Object object) {
            return object instanceof CollidingKey
                    && name.equals(((CollidingKey) object).name);
        }

        /**
         * Возвращает одинаковый хеш-код для всех ключей.
         *
         * @return постоянный хеш-код
         */
        @Override
        public int hashCode() {
            return -1;
        }
    }
}
