package com.sean.ninnong.region.controller;


import com.sean.ninnong.region.dto.RegionRequest;
import com.sean.ninnong.region.service.RegionService;
import com.sean.ninnong.region.domain.Region;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    public ResponseEntity<List<Region>> getRegionList() {
        return ResponseEntity.ok().body(regionService.getRegionList());
    }
    @PostMapping
    public ResponseEntity<String> addRegion(@RequestBody RegionRequest region) {
        regionService.addRegion(region);
        return ResponseEntity.ok("ok");
    }

}
