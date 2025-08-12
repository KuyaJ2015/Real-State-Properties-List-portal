package com.realestate.portal.service;

import com.realestate.portal.model.Property;
import com.realestate.portal.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public Property createProperty(Property property) {
        return propertyRepository.save(property);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Optional<Property> getPropertyById(Long id) {
        return propertyRepository.findById(id);
    }

    public Property updateProperty(Long id, Property propertyDetails) {
        Property existingProperty = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));

        existingProperty.setTitle(propertyDetails.getTitle());
        existingProperty.setLocation(propertyDetails.getLocation());
        existingProperty.setType(propertyDetails.getType());
        existingProperty.setPrice(propertyDetails.getPrice());
        existingProperty.setDescription(propertyDetails.getDescription());
        existingProperty.setImageUrl(propertyDetails.getImageUrl());
            
        return propertyRepository.save(existingProperty);
    }

    public void deleteProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + id));
        propertyRepository.delete(property);
    }

    public List<Property> searchProperties(String keyword, String type, Double minPrice, Double maxPrice) {
        return propertyRepository.searchProperties(keyword, type, minPrice, maxPrice);
    }

    public List<Property> findByType(String type) {
        return propertyRepository.findByTypeIgnoreCase(type);
    }

    public List<Property> findByPriceRange(Double minPrice, Double maxPrice) {
        return propertyRepository.findByPriceBetween(minPrice, maxPrice);
    }
}
