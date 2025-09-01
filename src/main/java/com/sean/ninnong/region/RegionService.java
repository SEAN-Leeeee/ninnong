package com.sean.ninnong.region;

import java.util.List;

public interface RegionService {

    void addRegion(RegionRequest region);

    List<Region> getRegionList();
}
