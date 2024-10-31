package Repository;

import Entity.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StoreRepository {

    private final Map<String, Store> stores = new HashMap<>();
    private static StoreRepository memoryStoreRepository;

    private StoreRepository() {
    }

    public static StoreRepository getInstance() {
        if (memoryStoreRepository == null) {
            memoryStoreRepository = new StoreRepository();
            return memoryStoreRepository;
        }
        return memoryStoreRepository;
    }

    public void addStore(Store store) {
        stores.put(store.getStoreName(), store);
    }

    public Collection<Store> findAll() {
        return stores.values();
    }
    public Store findStoreName(String storename) {
        return stores.get(storename);
    }


}
