package com.mall.controller;

import com.mall.model.ShippingInfo;
import com.mall.service.ShippingInfoService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shipping")
public class ShippingInfoController {
    private final ShippingInfoService shippingInfoService;

    public ShippingInfoController(ShippingInfoService shippingInfoService) {
        this.shippingInfoService = shippingInfoService;
    }

    @PostMapping
    public ShippingInfo addShippingInfo(@RequestBody ShippingInfo shippingInfo) {
        return shippingInfoService.addShippingInfo(shippingInfo);
    }

    @GetMapping("/{id}")
    public ShippingInfo getShippingInfoById(@PathVariable Long id) {
        return shippingInfoService.getShippingInfoById(id);
    }

    @PutMapping("/{id}")
    public ShippingInfo updateShippingInfo(@PathVariable Long id, @RequestBody ShippingInfo shippingInfo) {
        return shippingInfoService.updateShippingInfo(id, shippingInfo);
    }

    @DeleteMapping("/{id}")
    public void deleteShippingInfo(@PathVariable Long id) {
        shippingInfoService.deleteShippingInfo(id);
    }
}

