package com.allinmath.backend.service.resource;

import com.allinmath.backend.dto.resource.CreateResourceDTO;
import com.allinmath.backend.model.resource.Resource;
import com.allinmath.backend.repository.ResourceRepository;
import com.allinmath.backend.util.Logger;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public String createResource(CreateResourceDTO dto, String uploadedBy) {
        Resource resource = new Resource();
        resource.setTitle(dto.getTitle());
        resource.setUrl(dto.getUrl());
        resource.setAttachedTo(dto.getAttachedTo());
        resource.setUploadedBy(uploadedBy);
        resource.setUploadedAt(Instant.now().toString());

        try {
            return resourceRepository.createResource(resource);
        } catch (ExecutionException | InterruptedException e) {
            Logger.e("Error creating resource: " + e.getMessage());
            throw new RuntimeException("Failed to create resource");
        }
    }

    public void updateResource(com.allinmath.backend.dto.resource.UpdateResourceDTO dto) {
        try {
            Resource resource = resourceRepository.getResource(dto.getId());
            if (resource == null) {
                throw new RuntimeException("Resource not found");
            }

            if (dto.getTitle() != null)
                resource.setTitle(dto.getTitle());
            if (dto.getUrl() != null)
                resource.setUrl(dto.getUrl());
            if (dto.getAttachedTo() != null)
                resource.setAttachedTo(dto.getAttachedTo());

            resourceRepository.updateResource(resource);
        } catch (ExecutionException | InterruptedException e) {
            Logger.e("Error updating resource: " + e.getMessage());
            throw new RuntimeException("Failed to update resource");
        }
    }

    public List<Resource> getResourcesByCourse(String courseId) {
        try {
            return resourceRepository.getResourcesByCourse(courseId);
        } catch (ExecutionException | InterruptedException e) {
            Logger.e("Error getting resources: " + e.getMessage());
            throw new RuntimeException("Failed to get resources");
        }
    }

    public void deleteResource(String resourceId, String userId) {
        try {
            Resource resource = resourceRepository.getResource(resourceId);
            if (resource == null) {
                throw new RuntimeException("Resource not found");
            }

            // Verify that the user is the uploader of the resource
            if (!resource.getUploadedBy().equals(userId)) {
                throw new RuntimeException("Unauthorized to delete this resource");
            }

            resourceRepository.deleteResource(resourceId);
        } catch (ExecutionException | InterruptedException e) {
            Logger.e("Error deleting resource: " + e.getMessage());
            throw new RuntimeException("Failed to delete resource");
        }
    }
}
