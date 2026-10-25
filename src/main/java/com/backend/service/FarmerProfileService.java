package com.backend.service;

import com.backend.dto.FarmerProfileRequest;
import com.backend.dto.FarmerResponse;
import com.backend.entity.Farmer;
import com.backend.exception.ApiException;
import com.backend.repository.FarmerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FarmerProfileService {

    private final FarmerRepository farmerRepository;

    public FarmerProfileService(FarmerRepository farmerRepository) {
        this.farmerRepository = farmerRepository;
    }

    public FarmerResponse getProfile(Long farmerId) {
        Farmer f = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new ApiException("Farmer not found.", HttpStatus.NOT_FOUND));
        return FarmerResponse.from(f);
    }

    public FarmerResponse updateProfile(Long farmerId, FarmerProfileRequest req) {
        Farmer f = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new ApiException("Farmer not found.", HttpStatus.NOT_FOUND));
        if (req.fullName != null) f.setFullName(req.fullName);
        if (req.phone != null) f.setMobile(req.phone);
        if (req.farmName != null) f.setFarmName(req.farmName);
        if (req.address != null) f.setFarmAddress(req.address);
        if (req.city != null) f.setCity(req.city);
        if (req.pincode != null) f.setPincode(req.pincode);
        if (req.about != null) f.setAbout(req.about);
        farmerRepository.save(f);
        return FarmerResponse.from(f);
    }
}
