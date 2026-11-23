package ru.nsu.bogomolov.hashtable;

import java.util.AbstractMap;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Хеш-таблица с разрешением коллизий цепочками.
 *
 * <p>Размер массива увеличивается при заполнении более чем на 75 % и уменьшается
 * после удаления элементов. Ключи и значения могут быть {@code null}.
 * Порядок обхода элементов не определен.
 *
 * @param <K> тип ключа
 * @param <V> тип значения
 */
public class HashTable<K, V> implements Iterable<Map.Entry<K, V>> {
    /** Наименьшее число корзин. */
    private static final int MIN_CAPACITY = 8;
    /** Доля заполнения, после которой массив расширяется. */
    private static final double MAX_LOAD = 0.75;

    /** Корзины, содержащие начала цепочек. */
    private Node<K, V>[] buckets;
    /** Число сохраненных пар. */
    private int size;
    /** Счетчик изменений для итераторов. */
    private int modificationCount;

    /** Создает пустую таблицу. */
    @SuppressWarnings("unchecked")
    public HashTable() {
        buckets = (Node<K, V>[]) new Node[MIN_CAPACITY];
    }

    /**
     * Добавляет пару или заменяет значение существующего ключа.
     *
     * @param key ключ
     * @param value новое значение
     * @return прежнее значение или {@code null}, если его не было либо оно было null
     */
    public V put(K key, V value) {
        Node<K, V> existing = find(key);
        if (existing != null) {
            V oldValue = existing.value;
            existing.value = value;
            modificationCount++;
            return oldValue;
        }

        if (size + 1 > buckets.length * MAX_LOAD) {
            resize(buckets.length * 2);
        }
        int index = indexFor(key, buckets.length);
        buckets[index] = new Node<>(key, value, buckets[index]);
        size++;
        modificationCount++;
        return null;
    }

    /**
     * Возвращает значение по ключу.
     *
     * @param key ключ
     * @return значение или {@code null}, если ключ отсутствует либо значение равно null
     */
    public V get(K key) {
        Node<K, V> node = find(key);
        return node == null ? null : node.value;
    }

    /**
     * Заменяет значение существующего ключа.
     *
     * @param key ключ
     * @param value новое значение
     * @return прежнее значение
     * @throws NoSuchElementException если ключ отсутствует
     */
    public V update(K key, V value) {
        Node<K, V> node = find(key);
        if (node == null) {
            throw new NoSuchElementException("Ключ не найден: " + key);
        }
        V oldValue = node.value;
        node.value = value;
        modificationCount++;
        return oldValue;
    }

    /**
     * Проверяет наличие ключа независимо от значения.
     *
     * @param key ключ
     * @return {@code true}, если ключ найден
     */
    public boolean containsKey(K key) {
        return find(key) != null;
    }

    /**
     * Удаляет запись по ключу.
     *
     * @param key ключ
     * @return удаленное значение или {@code null}, если ключ отсутствовал либо значение было null
     */
    public V remove(K key) {
        Node<K, V> removed = removeEntry(key, null, false);
        return removed == null ? null : removed.value;
    }

    /**
     * Удаляет запись, только если совпадают ключ и значение.
     *
     * @param key ключ
     * @param value ожидаемое значение
     * @return {@code true}, если запись была удалена
     */
    public boolean remove(K key, V value) {
        return removeEntry(key, value, true) != null;
    }

    /**
     * Возвращает число записей.
     *
     * @return число записей
     */
    public int size() {
        return size;
    }

    /**
     * Проверяет, пуста ли таблица.
     *
     * @return {@code true}, если записей нет
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Создает итератор по снимкам пар ключ-значение.
     * Изменение таблицы после создания итератора вызывает
     * {@link ConcurrentModificationException} при следующем вызове
     * {@code hasNext()} или {@code next()}.
     *
     * @return итератор по записям таблицы
     */
    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new TableIterator();
    }

    /**
     * Сравнивает таблицы по набору пар, независимо от порядка обхода.
     *
     * @param object объект для сравнения
     * @return {@code true}, если пары совпадают
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof HashTable<?, ?>)) {
            return false;
        }
        HashTable<?, ?> other = (HashTable<?, ?>) object;
        if (size != other.size) {
            return false;
        }
        for (Map.Entry<K, V> entry : this) {
            Node<?, ?> otherNode = other.find(entry.getKey());
            if (otherNode == null || !Objects.equals(entry.getValue(), otherNode.value)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Вычисляет хеш-код по всем парам без учета порядка.
     *
     * @return хеш-код таблицы
     */
    @Override
    public int hashCode() {
        int result = 0;
        for (Map.Entry<K, V> entry : this) {
            result += Objects.hashCode(entry.getKey()) ^ Objects.hashCode(entry.getValue());
        }
        return result;
    }

    /**
     * Записывает пары в виде {@code {ключ=значение, ...}}.
     *
     * @return строковое представление таблицы
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        for (Map.Entry<K, V> entry : this) {
            if (result.length() > 1) {
                result.append(", ");
            }
            result.append(entry.getKey()).append('=').append(entry.getValue());
        }
        return result.append('}').toString();
    }

    /**
     * Ищет узел по ключу в соответствующей цепочке.
     *
     * @param key искомый ключ
     * @return узел или {@code null}
     */
    private Node<K, V> find(Object key) {
        for (Node<K, V> node = buckets[indexFor(key, buckets.length)];
                node != null; node = node.next) {
            if (Objects.equals(key, node.key)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Удаляет подходящий узел из цепочки.
     *
     * @param key ключ
     * @param value ожидаемое значение
     * @param checkValue требуется ли сравнить значение
     * @return удаленный узел или {@code null}
     */
    private Node<K, V> removeEntry(Object key, Object value, boolean checkValue) {
        int index = indexFor(key, buckets.length);
        Node<K, V> previous = null;
        for (Node<K, V> node = buckets[index]; node != null; node = node.next) {
            if (Objects.equals(key, node.key)
                    && (!checkValue || Objects.equals(value, node.value))) {
                if (previous == null) {
                    buckets[index] = node.next;
                } else {
                    previous.next = node.next;
                }
                size--;
                modificationCount++;
                if (buckets.length > MIN_CAPACITY && size <= buckets.length / 4) {
                    resize(buckets.length / 2);
                }
                return node;
            }
            previous = node;
        }
        return null;
    }

    /**
     * Перераспределяет узлы по новому массиву.
     *
     * @param capacity новая длина массива
     */
    @SuppressWarnings("unchecked")
    private void resize(int capacity) {
        Node<K, V>[] resized = (Node<K, V>[]) new Node[capacity];
        for (Node<K, V> bucket : buckets) {
            Node<K, V> node = bucket;
            while (node != null) {
                Node<K, V> next = node.next;
                int index = indexFor(node.key, capacity);
                node.next = resized[index];
                resized[index] = node;
                node = next;
            }
        }
        buckets = resized;
    }

    /**
     * Вычисляет индекс корзины для массива с длиной, равной степени двойки.
     *
     * @param key ключ
     * @param capacity длина массива
     * @return индекс корзины
     */
    private static int indexFor(Object key, int capacity) {
        int hash = Objects.hashCode(key);
        return (hash ^ (hash >>> 16)) & (capacity - 1);
    }

    /** Узел цепочки записей с одинаковым индексом корзины. */
    private static final class Node<K, V> {
        /** Ключ записи. */
        private final K key;
        /** Значение записи. */
        private V value;
        /** Следующий узел в цепочке. */
        private Node<K, V> next;

        /**
         * Создает узел цепочки.
         *
         * @param key ключ
         * @param value значение
         * @param next следующий узел
         */
        private Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    /** Итератор, проверяющий изменение таблицы при обходе. */
    private final class TableIterator implements Iterator<Map.Entry<K, V>> {
        /** Счетчик изменений на момент создания итератора. */
        private final int expectedModificationCount = modificationCount;
        /** Индекс корзины, содержащей текущий узел. */
        private int bucketIndex;
        /** Следующий узел для выдачи. */
        private Node<K, V> current;

        /** Создает итератор и находит первую непустую корзину. */
        private TableIterator() {
            advanceBucket();
        }

        /**
         * Проверяет, есть ли следующая пара.
         *
         * @return {@code true}, если пара есть
         */
        @Override
        public boolean hasNext() {
            checkModification();
            return current != null;
        }

        /**
         * Возвращает снимок следующей пары.
         *
         * @return следующая пара
         * @throws NoSuchElementException если пары закончились
         */
        @Override
        public Map.Entry<K, V> next() {
            checkModification();
            if (current == null) {
                throw new NoSuchElementException();
            }
            Map.Entry<K, V> entry = new AbstractMap.SimpleImmutableEntry<>(
                    current.key, current.value);
            if (current.next == null) {
                bucketIndex++;
                advanceBucket();
            } else {
                current = current.next;
            }
            return entry;
        }

        /** Переходит к следующей непустой корзине. */
        private void advanceBucket() {
            while (bucketIndex < buckets.length && buckets[bucketIndex] == null) {
                bucketIndex++;
            }
            current = bucketIndex < buckets.length ? buckets[bucketIndex] : null;
        }

        /**
         * Проверяет, что таблица не менялась после создания итератора.
         *
         * @throws ConcurrentModificationException если таблица изменилась
         */
        private void checkModification() {
            if (modificationCount != expectedModificationCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
