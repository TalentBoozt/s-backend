package com.talentboozt.s_backend.Controller.PLAT_COURSES;

import com.talentboozt.s_backend.DTO.PLAT_COURSES.CouponRedemptionRequest;
import com.talentboozt.s_backend.Model.PLAT_COURSES.CourseCouponsModel;
import com.talentboozt.s_backend.Service.PLAT_COURSES.CourseCouponsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/courses/coupons")
public class CourseCouponsController {

    @Autowired
    private CourseCouponsService courseCouponsService;

    @PostMapping("/add")
    public CourseCouponsModel addCourseCoupon(@RequestBody CourseCouponsModel courseCouponsModel) {
        return courseCouponsService.addCourseCoupon(courseCouponsModel);
    }

    @GetMapping("/get/{id}")
    public CourseCouponsModel getCourseCoupon(@PathVariable String id) {
        return courseCouponsService.getCourseCoupon(id);
    }

    @GetMapping("/get/all")
    public Iterable<CourseCouponsModel> getAllCourseCoupons() {
        return courseCouponsService.getAllCourseCoupons();
    }

    @PostMapping("/unlock/{couponId}")
    public CourseCouponsModel unlockCoupon(@PathVariable String couponId, @RequestParam String userId) {
        return courseCouponsService.unlockCoupon(couponId, userId);
    }

    @PostMapping("/activate/{couponId}")
    public CourseCouponsModel activateCoupon(@PathVariable String couponId, @RequestParam String userId) {
        return courseCouponsService.activateCoupon(couponId, userId);
    }

    @PostMapping("/redeem")
    public CourseCouponsModel redeemCoupon(@RequestBody CouponRedemptionRequest request) {
        return courseCouponsService.redeemCoupon(request);
    }

    @GetMapping("/ambassador/{userId}")
    public List<CourseCouponsModel> getCouponsByUser(@PathVariable String userId) {
        return courseCouponsService.getCouponsByUser(userId);
    }
}
