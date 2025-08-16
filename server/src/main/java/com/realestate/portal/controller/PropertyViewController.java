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

    @org.springframework.web.bind.annotation.PostMapping("/pages/newListing")
    public String createNewListing(
            @org.springframework.web.bind.annotation.ModelAttribute Property property,
            @org.springframework.web.bind.annotation.RequestParam("image") org.springframework.web.multipart.MultipartFile image,
            org.springframework.ui.Model model) {
            // Save image to static/images and set imageUrl in property using absolute path
            if (image != null && !image.isEmpty()) {
                try {
                    String uploadDir = new java.io.File("src/main/resources/static/images").getAbsolutePath();
                    String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                    java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir, fileName);
                    java.nio.file.Files.copy(image.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    property.setImageUrl("/images/" + fileName);
                } catch (Exception e) {
                    // Log error and continue without image
                    e.printStackTrace();
                }
            }
            propertyService.createProperty(property);
            return "redirect:/";
    }

    @org.springframework.web.bind.annotation.GetMapping("/pages/newListing")
    public String showCreateListingForm() {
        return "fragments/create-new-listing";
    }

}
