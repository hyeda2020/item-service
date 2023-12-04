package hello.itemservice.domain.item;

import lombok.Getter;
import lombok.Setter;

// @Data 어노테이션 사용은 좀 주의할 필요가 있음(가급적이면 @Getter, @Setter 사용)
@Getter
@Setter
public class Item {

    private Long id;
    private String itemName;
    private Integer price;
    private Integer quantity;

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
