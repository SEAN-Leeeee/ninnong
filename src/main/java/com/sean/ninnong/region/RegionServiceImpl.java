package com.sean.ninnong.region;

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
