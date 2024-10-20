package com.mall.service.impl;

import com.mall.model.ShippingInfo;
import com.mall.repository.ShippingInfoRepository;
import com.mall.service.ShippingInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ShippingInfoServiceImpl implements ShippingInfoService {
    private final ShippingInfoRepository shippingInfoRepository;

    public ShippingInfoServiceImpl(ShippingInfoRepository shippingInfoRepository) {
        this.shippingInfoRepository = shippingInfoRepository;
    }

    @Override
    @Transactional
    public ShippingInfo addShippingInfo(ShippingInfo shippingInfo) {
        return shippingInfoRepository.save(shippingInfo);
    }

    @Override
    public ShippingInfo getShippingInfoById(Long id) {
        return shippingInfoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public ShippingInfo updateShippingInfo(Long id, ShippingInfo updatedShippingInfo) {
        ShippingInfo existingShippingInfo = shippingInfoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Shipping info not found"));
        existingShippingInfo.setShippingAddress(updatedShippingInfo.getShippingAddress());
        return shippingInfoRepository.save(existingShippingInfo);
    }

    @Override
    public void deleteShippingInfo(Long id) {
        shippingInfoRepository.deleteById(id);
    }
}

