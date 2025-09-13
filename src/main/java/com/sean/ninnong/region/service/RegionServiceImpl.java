package com.sean.ninnong.region.service;

import com.sean.ninnong.region.domain.Region;
import com.sean.ninnong.region.dto.RegionRequest;
import com.sean.ninnong.region.repository.RegionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegionServiceImpl implements RegionService{

    private final RegionRepository regionRepository;

    public RegionServiceImpl(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    @Transactional
    public void addRegion(RegionRequest region) {
        regionRepository.findByName(region.getRegion())
            .orElseGet(()-> regionRepository.save(new Region(region.getRegion())));
    }

    @Override
    public List<Region> getRegionList() {
        return regionRepository.findAll();
    }
}
