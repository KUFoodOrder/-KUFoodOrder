package Repository;

import Entity.*;

import java.util.*;

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

    public List<Store> findStoreCategory(int category) {
        List<Store> matchedStores = new ArrayList<>();
        for (Store store : stores.values()) {
            if (store.getStoreCategory() == category) { // 카테고리가 일치하는 경우
                matchedStores.add(store);
            }
        }
        return matchedStores;        // 해당 카테고리 리스트들 반환
    }

}
