package com.realestate.portal.controller;

import com.realestate.portal.model.Property;
import com.realestate.portal.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PropertyViewController {

    private final PropertyService propertyService;

    @Autowired
    public PropertyViewController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Property> properties = propertyService.getAllProperties();
        model.addAttribute("properties", properties);
        return "index";
    }

    @GetMapping("/properties")
    public String getAllProperties(Model model) {
        List<Property> properties = propertyService.getAllProperties();
        model.addAttribute("properties", properties);
        return "properties";
    }

    @GetMapping("/properties/search")
    public String searchProperties(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Model model) {
        
        List<Property> properties = propertyService.searchProperties(keyword, type, minPrice, maxPrice);
        model.addAttribute("properties", properties);
        model.addAttribute("keyword", keyword);
        model.addAttribute("type", type);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        return "search-results";
    }
}
