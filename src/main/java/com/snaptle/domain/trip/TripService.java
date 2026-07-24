package com.snaptle.domain.trip;

import com.snaptle.domain.trip.dto.CreateTripRequest;
import com.snaptle.domain.trip.dto.TripMemberResponse;
import com.snaptle.domain.trip.dto.TripResponse;
import com.snaptle.domain.user.User;
import com.snaptle.domain.user.UserRepository;
import com.snaptle.global.exception.ErrorCode;
import com.snaptle.global.exception.SnaptleException;
import java.security.SecureRandom;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TripService {

    private static final String INVITE_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int INVITE_CODE_LENGTH = 8;

    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public TripService(TripRepository tripRepository, TripMemberRepository tripMemberRepository,
                        UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.tripMemberRepository = tripMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TripResponse createTrip(Long userId, CreateTripRequest request) {
        Trip trip = tripRepository.save(Trip.create(
                request.name(), request.startDate(), request.endDate(), request.baseCurrency(),
                generateUniqueInviteCode()
        ));
        tripMemberRepository.save(TripMember.of(trip.getId(), userId));

        return toResponse(trip);
    }

    @Transactional
    public TripResponse joinTrip(Long userId, String inviteCode) {
        Trip trip = tripRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new SnaptleException(ErrorCode.INVALID_INVITE_CODE));

        if (tripMemberRepository.existsByTripIdAndUserId(trip.getId(), userId)) {
            throw new SnaptleException(ErrorCode.ALREADY_JOINED_TRIP);
        }

        tripMemberRepository.save(TripMember.of(trip.getId(), userId));
        return toResponse(trip);
    }

    public List<TripResponse> getMyTrips(Long userId) {
        return tripMemberRepository.findAllByUserId(userId).stream()
                .map(TripMember::getTripId)
                .map(tripId -> tripRepository.findById(tripId)
                        .orElseThrow(() -> new SnaptleException(ErrorCode.TRIP_NOT_FOUND)))
                .map(this::toResponse)
                .toList();
    }

    public TripResponse getTrip(Long userId, Long tripId) {
        Trip trip = getTripOrThrow(tripId);
        requireMember(tripId, userId);
        return toResponse(trip);
    }

    Trip getTripOrThrow(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new SnaptleException(ErrorCode.TRIP_NOT_FOUND));
    }

    void requireMember(Long tripId, Long userId) {
        if (!tripMemberRepository.existsByTripIdAndUserId(tripId, userId)) {
            throw new SnaptleException(ErrorCode.NOT_TRIP_MEMBER);
        }
    }

    private TripResponse toResponse(Trip trip) {
        List<TripMemberResponse> members = tripMemberRepository.findAllByTripId(trip.getId()).stream()
                .map(member -> userRepository.findById(member.getUserId())
                        .orElseThrow(() -> new SnaptleException(ErrorCode.USER_NOT_FOUND)))
                .map(TripMemberResponse::from)
                .toList();
        return TripResponse.of(trip, members);
    }

    private String generateUniqueInviteCode() {
        String code;
        do {
            code = generateInviteCode();
        } while (tripRepository.existsByInviteCode(code));
        return code;
    }

    private String generateInviteCode() {
        StringBuilder builder = new StringBuilder(INVITE_CODE_LENGTH);
        for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
            builder.append(INVITE_CODE_CHARS.charAt(secureRandom.nextInt(INVITE_CODE_CHARS.length())));
        }
        return builder.toString();
    }
}
