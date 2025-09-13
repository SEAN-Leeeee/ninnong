package com.sean.ninnong.region.service;

import com.sean.ninnong.region.domain.Region;
import com.sean.ninnong.region.dto.RegionRequest;

import java.util.List;

public interface RegionService {

    void addRegion(RegionRequest region);

    List<Region> getRegionList();
}
