package com.woowahan.recipe.controller.api;

import com.woowahan.recipe.domain.dto.Response;
import com.woowahan.recipe.domain.dto.sellerDto.*;
import com.woowahan.recipe.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class SellerRestController {
    private final SellerService sellerService;

    // 회원가입
    @PostMapping("/seller/join")
    public Response<SellerJoinResponse> join(@RequestBody SellerJoinRequest sellerJoinRequest) {
        SellerJoinResponse sellerJoinResponse = sellerService.join(sellerJoinRequest);
        return Response.success(sellerJoinResponse);
    }

    // 로그인
    @PostMapping("/seller/login")
    public Response<SellerLoginResponse> login(@RequestBody SellerLoginRequest sellerLoginRequest) {
        String token = sellerService.login(sellerLoginRequest.getSellerName(), sellerLoginRequest.getPassword());
        return Response.success(new SellerLoginResponse(token));
    }

    // 판매자 단건 조회
    @GetMapping("/seller/{sellerName}")
    public Response<SellerResponse> findById(@PathVariable String sellerName, Authentication authentication) {
        return Response.success(sellerService.findBySellerName(sellerName));
    }

    // 판매자 전체 조회
    @GetMapping("/sellers")
    public Response<Page<SellerListResponse>> findAll(Pageable pageable) {
        return Response.success(sellerService.findAll(pageable));
    }


    // 판배자 정보 수정
    @PutMapping("/seller/{sellerName}")
    public Response<SellerResponse> update(@RequestBody SellerUpdateRequest sellerUpdateRequest,
                                           @PathVariable String sellerName) {
        SellerResponse sellerResponse= sellerService.update(sellerName, sellerUpdateRequest);
        return Response.success(sellerResponse);
    }


    @DeleteMapping("/seller/{id}")
    public Response<SellerDeleteResponse> delete(@PathVariable String id, Authentication authentication) {
        SellerDeleteResponse sellerDeleteResponse = sellerService.deleteSeller(id);
        return Response.success(sellerDeleteResponse);

    }




}