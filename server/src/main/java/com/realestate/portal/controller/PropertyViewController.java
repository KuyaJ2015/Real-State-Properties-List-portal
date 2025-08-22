
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
    @org.springframework.web.bind.annotation.InitBinder
    public void initBinder(org.springframework.web.bind.WebDataBinder binder) {
        binder.setDisallowedFields("photos");
    }

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

        // Property details page
        @org.springframework.web.bind.annotation.GetMapping("/properties/details/{id}")
        public String propertyDetails(@org.springframework.web.bind.annotation.PathVariable Long id, org.springframework.ui.Model model) {
            Property property = propertyService.getPropertyById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
            model.addAttribute("property", property);
            return "pages/propertyDetails";
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
            @org.springframework.web.bind.annotation.RequestParam(value = "supportingDocsFile", required = false) org.springframework.web.multipart.MultipartFile[] supportingDocsFiles,
            @org.springframework.web.bind.annotation.RequestParam(value = "photoFiles", required = false) org.springframework.web.multipart.MultipartFile[] photoFiles,
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
        // Save photos
        if (photoFiles != null && photoFiles.length > 0) {
            StringBuilder photoPaths = new StringBuilder();
            String uploadDir = new java.io.File("src/main/resources/static/images").getAbsolutePath();
            for (org.springframework.web.multipart.MultipartFile photoFile : photoFiles) {
                if (photoFile != null && !photoFile.isEmpty()) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + photoFile.getOriginalFilename();
                        java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir, fileName);
                        java.nio.file.Files.copy(photoFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        if (photoPaths.length() > 0) photoPaths.append(",");
                        photoPaths.append("/images/" + fileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            property.setPhotos(photoPaths.toString());
        }
        // Save supporting documents
        if (supportingDocsFiles != null && supportingDocsFiles.length > 0) {
            StringBuilder docPaths = new StringBuilder();
            String uploadDir = new java.io.File("src/main/resources/static/docs").getAbsolutePath();
            for (org.springframework.web.multipart.MultipartFile docFile : supportingDocsFiles) {
                if (docFile != null && !docFile.isEmpty()) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + docFile.getOriginalFilename();
                        java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir, fileName);
                        java.nio.file.Files.copy(docFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        if (docPaths.length() > 0) docPaths.append(",");
                        docPaths.append("/docs/" + fileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            property.setSupportingDocs(docPaths.toString());
        }
        propertyService.createProperty(property);
        return "redirect:/";
    }

    @org.springframework.web.bind.annotation.GetMapping("/pages/newListing")
    public String showCreateListingForm() {
        return "fragments/create-new-listing";
    }

        // Delete property by ID
        @org.springframework.web.bind.annotation.PostMapping("/properties/delete/{id}")
        public String deleteProperty(@org.springframework.web.bind.annotation.PathVariable Long id) {
        propertyService.deleteProperty(id);
            return "redirect:/"; // refresh list
        }


    // Edit property form
    @org.springframework.web.bind.annotation.GetMapping("/properties/edit/{id}")
    public String editPropertyForm(@org.springframework.web.bind.annotation.PathVariable Long id, org.springframework.ui.Model model) {
        Property property = propertyService.getPropertyById(id)
            .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        model.addAttribute("property", property);
        return "pages/editProperty"; // points to your Thymeleaf edit form
    }

    // Update property
    @org.springframework.web.bind.annotation.PostMapping("/properties/edit/{id}")
    public String updateProperty(
            @org.springframework.web.bind.annotation.PathVariable Long id,
            @org.springframework.web.bind.annotation.ModelAttribute Property property,
            @org.springframework.web.bind.annotation.RequestParam(value = "supportingDocsFile", required = false) org.springframework.web.multipart.MultipartFile[] supportingDocsFiles) {
        // Save supporting documents
        if (supportingDocsFiles != null && supportingDocsFiles.length > 0) {
            StringBuilder docPaths = new StringBuilder();
            String uploadDir = new java.io.File("src/main/resources/static/docs").getAbsolutePath();
            for (org.springframework.web.multipart.MultipartFile docFile : supportingDocsFiles) {
                if (docFile != null && !docFile.isEmpty()) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + docFile.getOriginalFilename();
                        java.nio.file.Path filePath = java.nio.file.Paths.get(uploadDir, fileName);
                        java.nio.file.Files.copy(docFile.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        if (docPaths.length() > 0) docPaths.append(",");
                        docPaths.append("/docs/" + fileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            property.setSupportingDocs(docPaths.toString());
        }
        propertyService.updateProperty(id, property);
        return "redirect:/";
    }

}
