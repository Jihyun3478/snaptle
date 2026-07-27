package com.snaptle.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    OAUTH2_PROVIDER_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 제공자입니다."),
    TRIP_NOT_FOUND(HttpStatus.NOT_FOUND, "여행을 찾을 수 없습니다."),
    INVALID_INVITE_CODE(HttpStatus.NOT_FOUND, "유효하지 않은 초대 코드입니다."),
    ALREADY_JOINED_TRIP(HttpStatus.CONFLICT, "이미 참여 중인 여행입니다."),
    NOT_TRIP_MEMBER(HttpStatus.FORBIDDEN, "해당 여행의 멤버가 아닙니다."),
    INVALID_TRIP_PERIOD(HttpStatus.BAD_REQUEST, "여행 종료일은 시작일보다 빠를 수 없습니다."),
    INVALID_FILE(HttpStatus.BAD_REQUEST, "파일을 처리할 수 없습니다."),
    EXPENSE_NOT_FOUND(HttpStatus.NOT_FOUND, "지출 내역을 찾을 수 없습니다."),
    PAYER_NOT_TRIP_MEMBER(HttpStatus.BAD_REQUEST, "결제자는 여행 멤버여야 합니다."),
    EXCHANGE_RATE_UNAVAILABLE(HttpStatus.BAD_REQUEST, "환율 자동 조회에 실패했습니다. 환율을 직접 입력해주세요."),
    INVALID_EXCHANGE_RATE(HttpStatus.BAD_REQUEST, "환율 값이 올바르지 않습니다."),
    INVALID_RECEIPT_IMAGE_URL(HttpStatus.BAD_REQUEST, "영수증 이미지 URL은 서비스에 업로드된 이미지만 사용할 수 있습니다."),
    EMPTY_PARTICIPANTS(HttpStatus.BAD_REQUEST, "지출을 분담할 참여자를 1명 이상 선택해야 합니다."),
    DUPLICATE_PARTICIPANT(HttpStatus.BAD_REQUEST, "참여자가 중복되었습니다."),
    PARTICIPANT_NOT_TRIP_MEMBER(HttpStatus.BAD_REQUEST, "참여자는 여행 멤버여야 합니다."),
    INVALID_PARTICIPANT_SPLIT(HttpStatus.BAD_REQUEST, "분담 금액은 모두 입력하거나 모두 비워야 하며, 합계가 지출 금액과 일치해야 합니다."),
    SAME_CREDITOR_AND_DEBTOR(HttpStatus.BAD_REQUEST, "채권자와 채무자는 같을 수 없습니다."),
    PERSONAL_DEBT_NOT_FOUND(HttpStatus.NOT_FOUND, "개인 간 채무 내역을 찾을 수 없습니다."),
    TRIP_ALREADY_ENDED(HttpStatus.CONFLICT, "이미 종료된 여행입니다."),
    SETTLEMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "정산 결과를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
