package com.mall.service;

import com.mall.model.ShippingInfo;

public interface ShippingInfoService {
    ShippingInfo addShippingInfo(ShippingInfo shippingInfo);
    ShippingInfo getShippingInfoById(Long id);
    ShippingInfo updateShippingInfo(Long id, ShippingInfo updatedShippingInfo);
    void deleteShippingInfo(Long id);
}

