package io.gaboja9.mockstock.domain.payments.controller;

import io.gaboja9.mockstock.domain.auth.dto.MembersDetails;
import io.gaboja9.mockstock.domain.payments.dto.PaymentRequest;
import io.gaboja9.mockstock.domain.payments.dto.PaymentResponse;
import io.gaboja9.mockstock.domain.payments.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "충전(카카오페이)", description = "카카오페이 결제 API")
public interface KakaoPayControllerSpec {

    @Operation(
            summary = "결제 준비",
            description =
                    "카카오페이 결제를 준비하고 결제 URL을 반환합니다. 다른 API는 리다이렉션 API라 따로 사용하진 않고 해당 API를 통해 반환된"
                            + " next_redirect_pc_url 주소로 결제를 진행합니다.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "결제 준비 성공",
                        content =
                                @Content(
                                        schema = @Schema(implementation = PaymentResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        name = "결제 준비 성공",
                                                        value =
                                                                """
                                                                        {
                                                                            "success": true,
                                                                            "message": "결제 준비 완료",
                                                                            "data": {
                                                                                "tid": "T87734d3620347052c46",
                                                                                "next_redirect_pc_url": "https://online-payment.kakaopay.com/mockup/bridge/pc/pg/one-time/payment/bcd3325a4ea71ed80c6056c4134c88987c1705066247638c9fea68b507ccd57e",
                                                                                "next_redirect_mobile_url": "https://online-payment.kakaopay.com/mockup/bridge/mobile-web/pg/one-time/payment/bcd3325a4ea71ed80c6056c4134c88987c1705066247638c9fea68b507ccd57e",
                                                                                "next_redirect_app_url": "https://online-payment.kakaopay.com/mockup/bridge/mobile-app/pg/one-time/payment/bcd3325a4ea71ed80c6056c4134c88987c1705066247638c9fea68b507ccd57e",
                                                                                "android_app_scheme": "kakaotalk://kakaopay/pg?url=https://online-pay.kakaopay.com/pay/mockup/bcd3325a4ea71ed80c6056c4134c88987c1705066247638c9fea68b507ccd57e",
                                                                                "ios_app_scheme": "kakaotalk://kakaopay/pg?url=https://online-pay.kakaopay.com/pay/mockup/bcd3325a4ea71ed80c6056c4134c88987c1705066247638c9fea68b507ccd57e",
                                                                                "created_at": "2025-07-16T14:12:51"
                                                                            }
                                                                        }
                                                                """))),
                @ApiResponse(
                        responseCode = "400",
                        description = "잘못된 요청",
                        content =
                                @Content(schema = @Schema(implementation = PaymentResponse.class)))
            })
    ResponseEntity<PaymentResponse> paymentReady(
            @Parameter(description = "결제 요청 정보", required = true) @RequestBody
                    PaymentRequest request,
            @AuthenticationPrincipal MembersDetails membersDetails);

    @Operation(summary = "결제 승인", description = "카카오페이 결제를 승인합니다.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "결제 승인 성공"),
                @ApiResponse(responseCode = "400", description = "결제 승인 실패")
            })
    ResponseEntity<Void> paymentApprove(
            @Parameter(description = "카카오페이 결제 고유 토큰", required = true, example = "sample_pg_token")
                    @RequestParam("pg_token")
                    String pgToken,
            @RequestParam("member_id") Long memberId, HttpServletResponse  response);

    @Operation(summary = "결제 취소", description = "진행 중인 결제를 취소합니다.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "결제 취소 성공"),
                @ApiResponse(responseCode = "400", description = "결제 취소 실패")
            })
    ResponseEntity<PaymentResponse> paymentCancel(
            @Parameter(description = "카카오페이 거래 고유 번호", required = true, example = "T1234567890")
                    @RequestParam("tid")
                    String tid,
            @AuthenticationPrincipal Long memberId);

    @Operation(summary = "결제 실패 처리", description = "카카오페이 결제 실패를 처리합니다.")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "결제 실패 처리 성공"),
                @ApiResponse(responseCode = "400", description = "결제 실패 처리 실패")
            })
    ResponseEntity<PaymentResponse> paymentFail(
            @Parameter(description = "카카오페이 거래 고유 번호", required = true, example = "T1234567890")
                    @RequestParam("tid")
                    String tid,
            @AuthenticationPrincipal Long memberId);

    @Operation(
            summary = "충전 내역 조회",
            description = "사용자의 포인트 충전 내역을 페이지네이션으로 조회합니다. 상태별 필터링도 가능합니다.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "충전 내역 조회 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = PaymentResponse.class),
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                                                                {
                                                                  "success": true,
                                                                  "message": "충전 내역 조회 성공",
                                                                  "data": {
                                                                    "payments": [
                                                                      {
                                                                        "id": 1,
                                                                        "partnerOrderId": "ORDER_1234567890_123",
                                                                        "amount": 100000,
                                                                        "status": "APPROVED",
                                                                        "createdAt": "2025-07-31T10:30:00",
                                                                        "updatedAt": "2025-07-31T10:35:00"
                                                                      }
                                                                    ],
                                                                    "pagination": {
                                                                      "currentPage": 0,
                                                                      "pageSize": 10,
                                                                      "totalPages": 1,
                                                                      "totalElements": 5,
                                                                      "hasNext": false,
                                                                      "hasPrevious": false
                                                                    },
                                                                    "summary": {
                                                                      "totalChargedAmount": 500000,
                                                                      "totalChargeCount": 5,
                                                                      "approvedAmount": 400000,
                                                                      "approvedCount": 4
                                                                    }
                                                                  }
                                                                }
                                                                """))),
                @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                @ApiResponse(responseCode = "401", description = "인증 실패")
            })
    ResponseEntity<PaymentResponse> getPaymentHistory(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
                    @RequestParam(defaultValue = "0")
                    int page,
            @Parameter(description = "페이지 크기 (최대 100)", example = "10")
                    @RequestParam(defaultValue = "10")
                    int size,
            @Parameter(
                            description = "결제 상태 필터 (선택사항)",
                            schema = @Schema(implementation = PaymentStatus.class),
                            example = "APPROVED")
                    @RequestParam(required = false)
                    PaymentStatus status,
            @Parameter(hidden = true) @AuthenticationPrincipal MembersDetails membersDetails);

    @Operation(summary = "충전 내역 상세 조회", description = "특정 충전 내역의 상세 정보를 조회합니다.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "충전 내역 상세 조회 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                                                                {
                                                                  "success": true,
                                                                  "message": "충전 내역 상세 조회 성공",
                                                                  "data": {
                                                                    "id": 1,
                                                                    "partnerOrderId": "ORDER_1234567890_123",
                                                                    "amount": 100000,
                                                                    "status": "APPROVED",
                                                                    "createdAt": "2025-07-31T10:30:00",
                                                                    "updatedAt": "2025-07-31T10:35:00"
                                                                  }
                                                                }
                                                                """))),
                @ApiResponse(responseCode = "404", description = "충전 내역을 찾을 수 없음"),
                @ApiResponse(responseCode = "403", description = "접근 권한 없음")
            })
    ResponseEntity<PaymentResponse> getPaymentDetail(
            @Parameter(description = "충전 내역 ID", required = true) @PathVariable Long paymentId,
            @Parameter(hidden = true) @AuthenticationPrincipal MembersDetails membersDetails);

    @Operation(summary = "충전 요약 정보 조회", description = "사용자의 전체 충전 요약 정보를 조회합니다.")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "충전 요약 조회 성공",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        examples =
                                                @ExampleObject(
                                                        value =
                                                                """
                                                                {
                                                                  "success": true,
                                                                  "message": "충전 요약 조회 성공",
                                                                  "data": {
                                                                    "totalChargedAmount": 500000,
                                                                    "totalChargeCount": 5,
                                                                    "approvedAmount": 400000,
                                                                    "approvedCount": 4
                                                                  }
                                                                }
                                                                """))),
                @ApiResponse(responseCode = "401", description = "인증 실패")
            })
    ResponseEntity<PaymentResponse> getPaymentSummary(
            @Parameter(hidden = true) @AuthenticationPrincipal MembersDetails membersDetails);
}
