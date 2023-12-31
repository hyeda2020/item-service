package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor // final이 붙은 변수에 대한 생성자 주입을 어노테이션을 통해 해결 가능
public class BasicItemController {

    private final ItemRepository itemRepository;

//    @Autowired
//    public BasicItemController(ItemRepository itemRepository) {
//        this.itemRepository = itemRepository;
//    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    /***
     * @PathVariable에서 Mapping 경로와 변수명이 같다면 (name="itemId")를 생략이 가능하지만
     * 스프링 부트 3.2부터 자바 컴파일러에 -parameters 옵션을 넣어주어야 애노테이션의 이름을 생략 가능
     * 권장 방법 : 애노테이션에 이름을 생략하지 않고 다음과 같이 이름을 항상 적어준다.
     * ex) @RequestParam("username") String username
     */
    @GetMapping("/{itemId}")
    public String item(@PathVariable(name = "itemId") Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    //@PostMapping("/add")
    public String addItemV1(
            @RequestParam String itemName,
            @RequestParam Integer price,
            @RequestParam Integer quantity,
            Model model) {

        Item item = new Item(itemName, price, quantity);
        itemRepository.save(item);
        model.addAttribute("item", item);
        return "basic/item";
    }

    /***
     * @ModelAttribute을 사용할 경우 model.addAttribute를 자동으로 해주기 때문에 생략 가능
     */
    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item) {
        itemRepository.save(item);
        // model.addAttribute("item", item);
        return "basic/item";
    }

    /**
     * @ModelAttribute name 생략 가능
     * model.addAttribute(item); 자동 추가, 생략 가능
     * 생략시 model에 저장되는 name은 클래스명 첫글자만 소문자로 등록 Item -> item
     */
    //@PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    /**
     * 파라미터로 String같은 기본 객체가 아닌 사용자 지정 객체가 들어올 경우
     * @ModelAttribute 자체 생략 가능하며,
     * model.addAttribute(item) 또한 자동으로 추가됨
     */
    //@PostMapping("/add")
    public String addItemV4(Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    /***
     * 상품을 등록하고 웹 브라우저에서 새로고침을 누르게 되면
     * POST /add + 상품데이터를 서버에 계속 전달하게 되는 문제가 발생
     * 따라서, POST /add 이후에는 뷰 템플릿으로 이동하지 않고 상품 상세 화면으로 리다이렉트
     * (Post, Redirect, Get 방식 사용)
     */
    //@PostMapping("/add")
    public String addItemV5(@ModelAttribute("item") Item item) {
        itemRepository.save(item);
        return "redirect:/basic/items/" + item.getId(); // 단, 이렇게 URL에 변수를 사용하면 URL 인코딩 이슈가 발생할 수 있음
    }

    /***
     * @param item
     * @param redirectAttributes -> URL 인코딩 및 쿼리 파라미터 추가 가능
     * @return
     */
    @PostMapping("/add")
    public String addItemV6(@ModelAttribute("item") Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true); // status 쿼리 파라미터로 저장이 정상적으로 되었는지 확인 가능
        return "redirect:/basic/items/{itemId}"; // URL에 변수를 사용하지 않아도 되므로 URL 인코딩 이슈 해결
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable(name = "itemId") Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "/basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String editItem(@PathVariable(name = "itemId") Long itemId, Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}"; // 수정 후 해당 아이템 상세 화면으로 리다이렉트
    }

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("testA", 10000, 10));
        itemRepository.save(new Item("testB", 20000, 20));
        itemRepository.save(new Item("testC", 30000, 30));
    }
}
