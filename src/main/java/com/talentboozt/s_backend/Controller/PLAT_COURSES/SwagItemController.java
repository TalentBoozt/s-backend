package com.talentboozt.s_backend.Controller.PLAT_COURSES;

import com.talentboozt.s_backend.Model.PLAT_COURSES.SwagItem;
import com.talentboozt.s_backend.Service.PLAT_COURSES.SwagItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/ambassador/swag-item")
public class SwagItemController {

    @Autowired
    private SwagItemService swagItemService;

    @PostMapping("/add")
    public SwagItem addSwagItem(@RequestBody SwagItem swagItem) {
        return swagItemService.addSwagItem(swagItem);
    }

    @PutMapping("/update")
    public SwagItem updateSwagItem(@RequestBody SwagItem swagItem) {
        return swagItemService.updateSwagItem(swagItem);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteSwagItem(@PathVariable String id) {
        swagItemService.deleteSwagItem(id);
    }

    @GetMapping("/get/{id}")
    public SwagItem getSwagItem(@PathVariable String id) {
        return swagItemService.getSwagItem(id);
    }

    @GetMapping("/get-all")
    public Iterable<SwagItem> getAllSwagItems() {
        return swagItemService.getAllSwagItems();
    }

    @GetMapping("/get-enabled")
    public Iterable<SwagItem> getEnabledSwagItems() {
        return swagItemService.getEnabledSwagItems();
    }

    @GetMapping("/get-disabled")
    public Iterable<SwagItem> getDisabledSwagItems() {
        return swagItemService.getDisabledSwagItems();
    }

    @PutMapping("/enable/{id}")
    public SwagItem enableSwagItem(@PathVariable String id) {
        return swagItemService.enableSwagItem(id);
    }

    @PutMapping("/disable/{id}")
    public SwagItem disableSwagItem(@PathVariable String id) {
        return swagItemService.disableSwagItem(id);
    }
}
