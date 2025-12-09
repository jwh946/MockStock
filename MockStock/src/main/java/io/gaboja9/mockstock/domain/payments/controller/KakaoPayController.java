package io.gaboja9.mockstock.domain.payments.controller;

import io.gaboja9.mockstock.domain.auth.dto.MembersDetails;
import io.gaboja9.mockstock.domain.payments.dto.*;
import io.gaboja9.mockstock.domain.payments.entity.PaymentStatus;
import io.gaboja9.mockstock.domain.payments.service.KakaoPayService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class KakaoPayController implements KakaoPayControllerSpec {
    private final KakaoPayService kakaoPayService;

    @PostMapping("/ready")
    public ResponseEntity<PaymentResponse> paymentReady(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal MembersDetails membersDetails) {
        try {
            KakaoPayReadyResponse response =
                    kakaoPayService.paymentReady(membersDetails.getId(), request.getChargeAmount());

            return ResponseEntity.ok(PaymentResponse.success("결제 준비 완료", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(PaymentResponse.fail("결제 준비 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/approve")
    public ResponseEntity<Void> paymentApprove(
            @RequestParam("pg_token") String pgToken, @RequestParam("member_id") Long memberId, HttpServletResponse response) {
        try {
            KakaoPayApproveResponse kakaoPayResponse = kakaoPayService.paymentApprove(pgToken, memberId);

//            return ResponseEntity.ok(PaymentResponse.success("결제 승인 완료", response));
            response.sendRedirect("https://mockstock.pages.dev/mypage?payment=success");
            return null;
        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(PaymentResponse.fail("결제 승인 실패: " + e.getMessage()));
            return null;
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<PaymentResponse> paymentCancel(
            @RequestParam("tid") String tid, @RequestParam("member_id") Long memberId) {
        try {
            kakaoPayService.paymentCancel(tid, memberId);

            return ResponseEntity.ok(PaymentResponse.success("결제 취소 완료", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(PaymentResponse.fail("결제 취소 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/fail")
    public ResponseEntity<PaymentResponse> paymentFail(
            @RequestParam("tid") String tid, @RequestParam("member_id") Long memberId) {
        try {
            kakaoPayService.paymentFail(tid, memberId);

            return ResponseEntity.ok(PaymentResponse.success("결제 실패 처리 완료", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(PaymentResponse.fail("결제 실패 처리 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<PaymentResponse> getPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) PaymentStatus status,
            @AuthenticationPrincipal MembersDetails membersDetails) {
        try {
            if (page < 0) {
                return ResponseEntity.badRequest()
                        .body(PaymentResponse.fail("페이지 번호는 0 이상이어야 합니다"));
            }
            if (size < 1 || size > 100) {
                return ResponseEntity.badRequest()
                        .body(PaymentResponse.fail("페이지 크기는 1~100 사이여야 합니다"));
            }

            PaymentHistoryRequest request =
                    PaymentHistoryRequest.builder().page(page).size(size).status(status).build();

            PaymentHistoryResponse response =
                    kakaoPayService.getPaymentHistory(membersDetails.getId(), request);

            return ResponseEntity.ok(PaymentResponse.success("충전 내역 조회 성공", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(PaymentResponse.fail("충전 내역 조회 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/history/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentDetail(
            @PathVariable Long paymentId, @AuthenticationPrincipal MembersDetails membersDetails) {
        try {
            PaymentHistoryDto response =
                    kakaoPayService.getPaymentDetail(membersDetails.getId(), paymentId);

            return ResponseEntity.ok(PaymentResponse.success("충전 내역 상세 조회 성공", response));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(PaymentResponse.fail("충전 내역 상세 조회 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<PaymentResponse> getPaymentSummary(
            @AuthenticationPrincipal MembersDetails membersDetails) {
        try {
            PaymentHistoryResponse.PaymentSummary summary =
                    kakaoPayService.getPaymentSummary(membersDetails.getId());

            return ResponseEntity.ok(PaymentResponse.success("충전 요약 조회 성공", summary));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(PaymentResponse.fail("충전 요약 조회 실패: " + e.getMessage()));
        }
    }
}
