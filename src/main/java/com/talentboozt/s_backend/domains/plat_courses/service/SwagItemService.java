package com.talentboozt.s_backend.domains.plat_courses.service;

import com.talentboozt.s_backend.domains.plat_courses.model.SwagItem;
import com.talentboozt.s_backend.domains.plat_courses.repository.SwagItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class SwagItemService {

    @Autowired
    private SwagItemRepository swagItemRepository;

    public SwagItem addSwagItem(SwagItem swagItem) {
        return swagItemRepository.save(Objects.requireNonNull(swagItem));
    }

    public SwagItem updateSwagItem(SwagItem swagItem) {
        Optional<SwagItem> swagItemOptional = swagItemRepository.findById(Objects.requireNonNull(swagItem.getId()));

        if (swagItemOptional.isPresent()) {
            return swagItemRepository.save(swagItem);
        }
        return null;
    }

    public void deleteSwagItem(String id) {
        swagItemRepository.deleteById(Objects.requireNonNull(id));
    }

    public SwagItem getSwagItem(String id) {
        Optional<SwagItem> swagItemOptional = swagItemRepository.findById(Objects.requireNonNull(id));
        return swagItemOptional.orElse(null);
    }

    public Iterable<SwagItem> getAllSwagItems() {
        return swagItemRepository.findAll();
    }

    public Iterable<SwagItem> getEnabledSwagItems() {
        return swagItemRepository.findByEnabled(true);
    }

    public Iterable<SwagItem> getDisabledSwagItems() {
        return swagItemRepository.findByEnabled(false);
    }

    public SwagItem enableSwagItem(String id) {
        Optional<SwagItem> swagItemOptional = swagItemRepository.findById(Objects.requireNonNull(id));
        if (swagItemOptional.isPresent()) {
            SwagItem swagItem = swagItemOptional.get();
            swagItem.setEnabled(true);
            return swagItemRepository.save(swagItem);
        }
        return null;
    }

    public SwagItem disableSwagItem(String id) {
        Optional<SwagItem> swagItemOptional = swagItemRepository.findById(Objects.requireNonNull(id));
        if (swagItemOptional.isPresent()) {
            SwagItem swagItem = swagItemOptional.get();
            swagItem.setEnabled(false);
            return swagItemRepository.save(swagItem);
        }
        return null;
    }
}
