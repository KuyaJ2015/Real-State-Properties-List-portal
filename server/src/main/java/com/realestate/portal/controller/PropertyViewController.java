
package com.realestate.portal.controller;

import com.realestate.portal.model.Property;
import com.realestate.portal.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class PropertyViewController {

    private final PropertyService propertyService;
    
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Autowired
    public PropertyViewController(PropertyService propertyService) {
        this.propertyService = propertyService;
        // Ensure upload directories exist
        ensureDirectoryExists(uploadDir + "/images");
        ensureDirectoryExists(uploadDir + "/docs");
    }
    
    private void ensureDirectoryExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
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
            @org.springframework.web.bind.annotation.RequestParam(value = "photoFiles", required = false) org.springframework.web.multipart.MultipartFile[] photoFiles,
            @org.springframework.web.bind.annotation.RequestParam(value = "supportingDocsFile", required = false) org.springframework.web.multipart.MultipartFile[] supportingDocsFiles,
            org.springframework.ui.Model model) {
        
        // Generate title from type and location if not provided
        if (property.getTitle() == null || property.getTitle().isEmpty()) {
            property.setTitle(property.getType() + " in " + property.getLocation());
        }
            
        // Save image to uploads/images and set imageUrl in property
        if (photoFiles != null && photoFiles.length > 0 && !photoFiles[0].isEmpty()) {
            try {
                String imagesUploadDir = uploadDir + "/images";
                // Ensure upload directory exists
                java.io.File uploadDirFile = new java.io.File(imagesUploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                
                String fileName = System.currentTimeMillis() + "_" + photoFiles[0].getOriginalFilename();
                java.nio.file.Path filePath = java.nio.file.Paths.get(imagesUploadDir, fileName);
                java.nio.file.Files.copy(photoFiles[0].getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                property.setImageUrl("/images/" + fileName);
                
                // Handle additional photos if present
                if (photoFiles.length > 1) {
                    StringBuilder photoUrls = new StringBuilder();
                    for (int i = 1; i < photoFiles.length; i++) {
                        if (!photoFiles[i].isEmpty()) {
                            String additionalFileName = System.currentTimeMillis() + "_" + i + "_" + photoFiles[i].getOriginalFilename();
                            java.nio.file.Path additionalFilePath = java.nio.file.Paths.get(imagesUploadDir, additionalFileName);
                            java.nio.file.Files.copy(photoFiles[i].getInputStream(), additionalFilePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            if (photoUrls.length() > 0) photoUrls.append(",");
                            photoUrls.append("/images/").append(additionalFileName);
                        }
                    }
                    property.setPhotos(photoUrls.toString());
                }
            } catch (Exception e) {
                // Log error and continue without image
                e.printStackTrace();
            }
        }
        // Save supporting documents
        if (supportingDocsFiles != null && supportingDocsFiles.length > 0) {
            StringBuilder docPaths = new StringBuilder();
            String docsUploadDir = uploadDir + "/docs";
            for (org.springframework.web.multipart.MultipartFile docFile : supportingDocsFiles) {
                if (docFile != null && !docFile.isEmpty()) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + docFile.getOriginalFilename();
                        java.nio.file.Path filePath = java.nio.file.Paths.get(docsUploadDir, fileName);
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
        try {
            propertyService.createProperty(property);
            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error creating property: " + e.getMessage());
            return "fragments/create-new-listing";
        }
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, Model model) {
        model.addAttribute("error", "File size exceeds limit! Maximum file size is 10MB per file, 20MB total.");
        return "fragments/create-new-listing";
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
            String docsUploadDir = uploadDir + "/docs";
            for (org.springframework.web.multipart.MultipartFile docFile : supportingDocsFiles) {
                if (docFile != null && !docFile.isEmpty()) {
                    try {
                        String fileName = System.currentTimeMillis() + "_" + docFile.getOriginalFilename();
                        java.nio.file.Path filePath = java.nio.file.Paths.get(docsUploadDir, fileName);
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
