package com.talentboozt.s_backend.Controller.PLAT_COURSES;

import com.talentboozt.s_backend.DTO.PLAT_COURSES.CouponRedemptionRequest;
import com.talentboozt.s_backend.Model.PLAT_COURSES.CourseCouponsModel;
import com.talentboozt.s_backend.Service.PLAT_COURSES.CourseCouponsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

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
    public ResponseEntity<?> activateCoupon(@PathVariable String couponId, @RequestParam String userId) {
        try {
            CourseCouponsModel activated = courseCouponsService.activateCoupon(couponId, userId);
            return ResponseEntity.ok(activated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Coupon not found");
        }
    }

    @PostMapping("/redeem")
    public CourseCouponsModel redeemCoupon(@RequestBody CouponRedemptionRequest request) {
        return courseCouponsService.redeemCoupon(request);
    }

    @GetMapping("/ambassador/{userId}")
    public List<CourseCouponsModel> getCouponsByUser(@PathVariable String userId) {
        return courseCouponsService.getCouponsByUser(userId);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCourseCoupon(@PathVariable String id) {
        courseCouponsService.deleteCourseCoupon(id);
        return ResponseEntity.ok().build();
    }
}
