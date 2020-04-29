import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Objects;

public class HashMap<K, V> extends AbstractMap<K, v> implements Map<K, V>, Cloneable, Serializable{

    public static final long SerialVersionUID = 362498820763181265L;

    // 初始化容量
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;

    // 最大容量
    static final int MAXIMUM_CAPACITY = 1 << 30;

    // 扩容因子
    static final float DEFAULT_LOAD_FACTORY = 0.75f;

    // 树化阈值
    static final int TREEIFY_THRESHOLD = 8;

    static final int UNTREEIFY_THRESHOLD = 6;

    // 最小树化容积
    static final int MIN_TREEIFY_CAPACITY = 64;

    static class Node<K, V> implements Map.Entry<K, V>{
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next){
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey(){ return key; }

        public final V getValue(){ return value; }

        public final String toString(){ return key + "=" + value; }

        public final V setValue(V newValue){
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        // 重写hashCode方法
        // 其实最终是调用key和value重写后的hashCode方法
        public final int hashCode(){ return Objects.hashCode(key) ^ Objects.hashCode(value); }

        // 重写equals方法
        // 其实最终是调用key和value重写后的equals方法
        public final boolean equals(Object o){
            if(o == this){
                return true;
            }
            if(o instanceof Map.Entry){
                Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
                if(Objects.equals(key, e.getKey()) &&
                Objects.equals(value, e.getValue()))
                return true;
            }
            return false;
        }

    }


    static final int hash(Object key){
        int h;
        return (k == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }


    // 如果类x实现了Comparable接口，返回类x的Class
    static Class<?> comparableClassFor(Object x){
        if(x instanceof Comparable){
            Class<?> c;
            Type[] ts, as;
            ParameterizedType p;
            if(c == x.getClass() == String.class) return c;
            if((ts = c.getGenericInterfaces() != null)){
                for(Type t : ts){
                    if((t instanceof ParameterizedType) &&
                    ((p = (ParameterizedType) t).getRawType() == Comparable.class) &&
                    (as = p.getActualTypeArguments() != null && as.length == 1 && as[0] == c))
                    return c;
                }
            }
        }
        return null;
    }


    @SuppressWarnings({"rawtypes", "unchecked"}) // 抑制警告信息
    static int compareComparables(Class<?> kc, Object k, Object x){
        return (x == null || x.getClass() != kc ? 0 : ((Comparable)k).compareTo(x));
    }

    // 返回不小于cap的最小的2的整数次幂
    static final int tableSizeFor(int cap){
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    transient Node<K, V>[] table;

    transient Set<Map.Entry<K, V>> entrySet;

    transient int size;

    // HashMap结构被修改的次数
    transient int modCount;

    // 扩容阈值
    int threshold;

    // 扩容因子
    final float loadFactory;

    // 注意到HashMap的构造函数只是给扩容因子和初始化容量赋值
    // 并没有HashMap存储结构的空间，即延迟创建
    public HashMap(int initialCapacity, float loadFactory){
        if(initialCapacity < 0) throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        if(initialCapacity > MAXIMUM_CAPACITY) initialCapacity = MAXIMUM_CAPACITY;
        if(loadFactory <= 0 || Float.isNaN(loadFactory)) throw new IllegalArgumentException("Illegal load factory: " + loadFactory);
        this.loadFactory = loadFactory;
        this.threshold = taleSizeFor(initialCapacity);
    }

    public HashMap(int initialCapacity){ this(initialCapacity, DEFAULT_LOAD_FACTORY); }


    public HashMap(){this.loadFactory = DEFAULT_LOAD_FACTORY; }

    // 使用Map作为参数初始化
    public HashMap(Map<? extends K, ? extends V> m){
        this.loadFactory = DEFAULT_LOAD_FACTORY;
        putMapEntries(m, false);
    }


    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict){
        int s = m.size;
        if(s > 0){
            if(table == null){
                float ft = ((float)s / loadFactory) + 1.0F;
                int t = ((ft < (float)MAXIMUM_CAPACITY) ? (int)ft : MAXIMUM_CAPACITY);
                if(t > threshold) threshold = tableSizeFor(t);
            }
            else if(s > threshold) resize();
            for(Map.Entry<? extends K, ? extends V> e : m.entrySet()){
                K key = e.getKey();
                V value = e.getValue();
                putVal(hash(key), key, value, false, evict);
            }
        }
    }

    public int size(){ return size;}

    public boolean isEmpty(){ return size == 0;}

    // 根据key获取value
    public V get(Object key){
        Node<K, V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    // Node满足要求：hash值相等，key相等或equals(key, k) == true;
    // 1. 判断第一个结点是否要找的结点
    // 2. 如果第一个结点是TreeNode，去树中找
    // 3. 否则，扫描链表寻找结点
    final Node<K, V> getNode(int hash, Object key){
        Node<K, V>[] tab;
        Node<K, V> first, e;
        int n;
        K k;
        if((tab = table) != null && (n = tab.length) > 0 && (first = tab[(n - 1) & hash]) != null){
            if(first.hash == hash && ((k = first.key) == key) || (key != null && key.equals(k))){
                return first;
            }
            if((e = first.next) != null){
                if(first instanceof TreeNode) return ((TreeNode<K, V>)first).getTreeNode(hash, key);                
                do{
                    if(e.hash == hash && ((k = e.key) == key) || (key != null && key.equals(k))) return e;
                }while((e = e.next) != null);
            }
        }
        return null;
    }

    // 判断key是否存在
    public boolean containsKey(Object key) { return getNode(hash(key), key) != null; }



    public V put(K key, V value){ return putVal(hash(key), key, value, false, true); }


    final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict){
        Node<k, V> [] tab; Node <K, V> p;
        int n, i;
        // table为null，调用resize初始化
        if((tab = table) == null || (n = tab.length) == 0){
            n = (tab = resize()).length;
        }
        // 桶中该位置还没有元素，直接放入新元素
        if((p = tab[(n - 1) & hash]) == null){
            tab[i] = newNode(hash, key, value, null);
        }
        // 发生hash碰撞
        else{
            Node<K, V> e;
            K k;
            // key已存在并为头结点
            if(p.hash == hash && ((k = p.key) == key || key != null && key.equals(k))) e = p;
            // 已树化
            else if(p instanceof TreeNode) e = ((TreeNode<K, V>)p).putTreeVal(this, tab, hash, key, value);
            // 
            else{
                for(int binCount = 0; ; ++binCount){
                    // key不存在，新建Node插入到后面
                    if((e = p.next) == null){
                        p.next = newNode(hash, key, value, null);
                        if(binCount >= TREEIFY_THRESHOLD - 1) treeifyBin(tab, hash);
                        break;
                    }
                    if(e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k)))) break;
                    p = e;
                }
                // key的映射已经存在
                if(e != null){
                    V oldValue = e.value;
                    if(!onlyIfAbsent || oldValue == null){
                        e.value = value;
                    }
                    afterNodeAccess(e);
                    return oldValue;
                }
            }
            ++modCount;
            if(++size > threshold) resize();
            afterNnodeInsertion(evict);
            return null;
        }
    }



    final Node<K, V> resize(){
        Node<K, V> [] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int ondThr = threshold;
        int newCap, newThr = 0;
        if(oldCap > 0){
            // 容量已达最大值，修改扩容阈值
            if(oldCap >= MAXIMUM_CAPACITY){
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            // 容量扩为2倍，扩容阈值也增大为2倍
            else if((newCap = oldCap << 1) < MAXIMUM_CAPACITY && oldCap >= DEFAULT_INITIAL_CAPACITY){
                newThr = oldThr << 1;
            }
        }
        // 容量置于扩容阈值
        else if(oldThr > 0){
            newCap = oldThr;
        }
        else{
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTORY * DEFAULT_INITIAL_CAPACITY);
        }
        if(newThr == 0){
            float ft = (float)newCap * loadFactory;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ? (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rowtypes", "unchecked"})
        Node<K, V>[] newTab = (Node<K, V>[])new Node[newCap];
        table = newTab;
        if(oldTab != null){
            for(int j = 0; j < oldCap; ++j){
                Node<K, V> e;
                if((e = oldTab[j] != null)){
                    oldTab[j] = null;
                    if(e.next == null){
                        newTab[e.hash & (newCap - 1)] = e;
                    }else if(e instanceof TreeNode){
                        ((TreeNode<K, V>)e).split(this, newTab, j, oldCap);
                    }else{
                        // 先将原来的链表分裂成两个链表
                        Node<K, V> loHead = null, loTail = null;
                        Node<K, V> hiHead = null, hiTail = null;
                        Node<K, V> next;
                        do{
                            next = e.next;
                            // 这部分是桶下标不用改变的
                            if((e.hash & oldCap) == 0){
                                if(loTail == null){
                                    loHead = e;
                                }else{
                                    loTail.next = e;
                                }
                                loTail = e;
                            }else{
                                if(hiTail == null){
                                    hiHead = e;
                                }else{
                                    hiTail.next = e;
                                }
                                hiTail = e;
                            }
                        }while((e = next) != null);
                        if(loTail != null){
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if(hiTail != null){
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
}