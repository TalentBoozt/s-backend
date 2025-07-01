package com.talentboozt.s_backend.Service.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.SwagItem;
import com.talentboozt.s_backend.Repository.PLAT_COURSES.SwagItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SwagItemService {

    @Autowired
    private SwagItemRepository swagItemRepository;

    public SwagItem addSwagItem(SwagItem swagItem) {
        return swagItemRepository.save(swagItem);
    }

    public SwagItem updateSwagItem(SwagItem swagItem) {
        Optional<SwagItem> swagItemOptional = swagItemRepository.findById(swagItem.getId());

        if (swagItemOptional.isPresent()) {
            return swagItemRepository.save(swagItem);
        }
        return null;
    }

    public void deleteSwagItem(String id) {
        swagItemRepository.deleteById(id);
    }

    public SwagItem getSwagItem(String id) {
        Optional<SwagItem> swagItemOptional = swagItemRepository.findById(id);
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
        Optional<SwagItem> swagItemOptional = swagItemRepository.findById(id);
        if (swagItemOptional.isPresent()) {
            SwagItem swagItem = swagItemOptional.get();
            swagItem.setEnabled(true);
            return swagItemRepository.save(swagItem);
        }
        return null;
    }

    public SwagItem disableSwagItem(String id) {
        Optional<SwagItem> swagItemOptional = swagItemRepository.findById(id);
        if (swagItemOptional.isPresent()) {
            SwagItem swagItem = swagItemOptional.get();
            swagItem.setEnabled(false);
            return swagItemRepository.save(swagItem);
        }
        return null;
    }
}
