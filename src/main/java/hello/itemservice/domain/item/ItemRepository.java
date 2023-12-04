package hello.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {

    /***
     *  스프링 빈은 싱글톤이고, 멀티쓰레드 환경이므로
     *  실무에서는 Race Condition에서 발생할 수 있는 문제를 예방하기 위해
     *  Static 특성을 가진 변수는 동시성 이슈를 고려해야 하며,
     *  그냥 Long/HashMap 대신 AtomicLong/ConcurrentHashMap를 사용해야 함!
     *  단, 지금은 간단한 예제이기 때문에 HashMap 사용
     */
    private static final Map<Long, Item> store = new HashMap<>();
    private static Long sequence = 0L;


    public Item save(Item item) {
        item.setId(++sequence);
        store.put(sequence, item);
        return item;
    }

    public Item findById(Long id) {
        return store.get(id);
    }

    public List<Item> findAll() {
        // 이렇게 ArrayList로 한번 감싸면 외부의 어떤 작업으로 인해 store에 변동사항이 생길 여지를 막을 수 있음
        return new ArrayList<Item>(store.values());
    }

    public void update(Long itemId, Item updateParam) { // Item 객체보다는 DTO를 사용하는 것이 좋으나 간단한 예제이므로..
        Item findItem = store.get(itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    public void clearStore() {
        store.clear();
    }
}
