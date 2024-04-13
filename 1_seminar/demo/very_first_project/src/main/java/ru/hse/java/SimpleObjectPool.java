package ru.hse.java;

import org.apache.commons.pool2.DestroyMode;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Egor Litvinenko
 */
public class SimpleObjectPool<T> {

    private final GenericObjectPool<T> pool;
    private final Supplier<T> supplier;

    public SimpleObjectPool(int minObjects, int maxObjects, Class<T> clazz) {
        this(minObjects, maxObjects, supplierFromDefaultConstructor(clazz), item -> {
        });
    }

    public SimpleObjectPool(int minObjects, int maxObjects, Supplier<T> supplier, Consumer<T> passivate) {
        this.supplier = supplier;
        var config = new GenericObjectPoolConfig<T>();
        config.setBlockWhenExhausted(false);
        config.setTestOnBorrow(false);
        config.setTestOnCreate(false);
        config.setTestOnReturn(false);
        config.setTestWhileIdle(false);
        config.setMaxTotal(maxObjects);
        config.setMinIdle(minObjects);

        var factory =
                new PooledObjectFactory<T>() {
                    @Override
                    public void activateObject(PooledObject<T> pooledObject) throws Exception {
                    }

                    @Override
                    public void destroyObject(PooledObject<T> pooledObject) throws Exception {
                    }

                    @Override
                    public void destroyObject(PooledObject<T> p, DestroyMode destroyMode) throws Exception {
                        PooledObjectFactory.super.destroyObject(p, destroyMode);
                    }

                    @Override
                    public PooledObject<T> makeObject() throws Exception {
                        return new DefaultPooledObject<>(supplier.get());
                    }

                    @Override
                    public void passivateObject(PooledObject<T> pooledObject) {
                        passivate.accept(pooledObject.getObject());
                    }

                    @Override
                    public boolean validateObject(PooledObject<T> pooledObject) {
                        return false;
                    }
                };
        pool = new GenericObjectPool<>(factory, config);
        try {
            pool.preparePool();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public T borrow() {
        try {
            return pool.borrowObject();
        } catch (Throwable throwable) {
            return supplier.get();
        }
    }

    public void offer(T value) {
        pool.returnObject(value);
    }

    private static <T> Supplier<T> supplierFromDefaultConstructor(Class<T> clazz) {
        {
            try {
                var constructor = clazz.getConstructor();
                constructor.setAccessible(true);
                // test it
                constructor.newInstance();
                return () -> {
                    try {
                        return constructor.newInstance();
                    } catch (Throwable throwable) {
                        throw new IllegalStateException(throwable);
                    }
                };
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }
    }
}
