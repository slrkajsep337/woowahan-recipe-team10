package com.woowahan.recipe.controller.ui;

import com.woowahan.recipe.domain.dto.cartDto.CartItemReq;
import com.woowahan.recipe.domain.dto.itemDto.*;
import com.woowahan.recipe.service.CartService;
import com.woowahan.recipe.service.ItemService;
import com.woowahan.recipe.service.OrderService;
import com.woowahan.recipe.service.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;
    private final CartService cartService;
    private final OrderService orderService;

    private final S3Uploader s3Uploader;

    /**
     * paging -> 상품 전체 조회
     */
    private String paging(Model model, Page<ItemListResDto> items) {
        int nowPage = items.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, items.getTotalPages());
        int lastPage = items.getTotalPages();

        model.addAttribute("items", items);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("lastPage", lastPage);
        return "item/findAllForm";
    }

    /**
     * 상품 상세조회
     */
    @GetMapping("/{id}")
    public String findForm(Model model, @PathVariable Long id) {
        ItemDetailResDto resDto = itemService.findItem(id);

        model.addAttribute("id", id);
        model.addAttribute("resDto", resDto); // -> sellerName도 들어있음
        model.addAttribute("cartItemReq", new CartItemReq(id, 1));
        return "item/findForm";
    }


    /**
     * 카트에 상품 수량 업데이트 (카트에 아이템이 없으면 생성)
     */
    @PostMapping("/cart")
    public String addCartItem(@ModelAttribute CartItemReq cartItemReq,
                              Model model, Authentication authentication) {

        model.addAttribute("cartItemReq", cartItemReq);
        cartService.addCartItem(cartItemReq, authentication.getName());

        return "redirect:/items/"+cartItemReq.getCartItemId(); //상품상세보기페이지로 return
    }

    /**
     * 상품 전체조회
     */
    @GetMapping
    public String findAllForm(Model model, @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ItemListResDto> items = itemService.findAllItem(pageable);
        return paging(model, items);
    }

    /**
     * 상품 검색
     */
    @GetMapping("/search")
    public String search(Model model, @ModelAttribute ItemSearchReqDto itemSearchReqDto, @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ItemListResDto> searchItems = itemService.searchItem(itemSearchReqDto.getKeyword(), pageable);
        return paging(model, searchItems);
    }


    /**
     * 상품 등록(관리자, 판매자)
     */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("itemCreateReqDto", new ItemCreateReqDto());
        return "item/createForm";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute ItemCreateReqDto reqDto, BindingResult bindingResult,
                         @RequestPart MultipartFile multipartFile,
                         RedirectAttributes redirectAttributes, Authentication authentication) throws IOException {

        if (bindingResult.hasErrors()) {
            log.info("bindingResult = {}", bindingResult);
            return "item/createForm";
        }

        String imgPath = s3Uploader.upload(multipartFile, "item-image");
        reqDto.setItemImagePath(imgPath);
        ItemCreateResDto resDto = itemService.createItem(reqDto, authentication.getName());
        redirectAttributes.addAttribute("id", resDto.getId());

        log.info("img주소확인 : {}", imgPath);
        return "redirect:/items/{id}";
    }


    /**
     * 상품 수정(관리자, 판매자)
     */
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id,  Model model) {
        model.addAttribute("id", id);
        model.addAttribute("itemUpdateReqDto", itemService.findItem(id));
        return "item/updateForm";
    }

    @PostMapping("/update/{id}")
    public String update(@Valid @ModelAttribute ItemUpdateReqDto reqDto, BindingResult bindingResult,
                         @PathVariable Long id, @RequestPart MultipartFile multipartFile,
                         RedirectAttributes redirectAttributes, Authentication authentication) throws IOException {

        if (bindingResult.hasErrors()) {
            log.info("bindingResult = {}", bindingResult);
            return "item/updateForm";
        }
        String imgPath = s3Uploader.upload(multipartFile, "item-image");
        reqDto.setItemImagePath(imgPath);

        ItemUpdateResDto resDto = itemService.updateItem(id, reqDto, authentication.getName());
        redirectAttributes.addAttribute("id", resDto.getId());
        return "redirect:/items/{id}";
    }

    /**
     * 상품 삭제(관리자, 판매자)
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Authentication authentication) {
        itemService.deleteItem(id, authentication.getName());
        return "redirect:/items";
    }
}
