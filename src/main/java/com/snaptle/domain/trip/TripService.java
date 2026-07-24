package com.snaptle.domain.trip;

import com.snaptle.domain.trip.dto.CreateTripRequest;
import com.snaptle.domain.trip.dto.TripMemberResponse;
import com.snaptle.domain.trip.dto.TripResponse;
import com.snaptle.domain.user.User;
import com.snaptle.domain.user.UserRepository;
import com.snaptle.global.exception.ErrorCode;
import com.snaptle.global.exception.SnaptleException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
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
        if (request.endDate().isBefore(request.startDate())) {
            throw new SnaptleException(ErrorCode.INVALID_TRIP_PERIOD);
        }

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

        try {
            tripMemberRepository.saveAndFlush(TripMember.of(trip.getId(), userId));
        } catch (DataIntegrityViolationException e) {
            throw new SnaptleException(ErrorCode.ALREADY_JOINED_TRIP);
        }
        return toResponse(trip);
    }

    public List<TripResponse> getMyTrips(Long userId) {
        List<Long> tripIds = tripMemberRepository.findAllByUserId(userId).stream()
                .map(TripMember::getTripId)
                .toList();
        return tripRepository.findAllById(tripIds).stream()
                .map(this::toResponse)
                .toList();
    }

    public TripResponse getTrip(Long userId, Long tripId) {
        Trip trip = getTripOrThrow(tripId);
        requireMember(tripId, userId);
        return toResponse(trip);
    }

    public Trip getTripOrThrow(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new SnaptleException(ErrorCode.TRIP_NOT_FOUND));
    }

    public void requireMember(Long tripId, Long userId) {
        if (!isMember(tripId, userId)) {
            throw new SnaptleException(ErrorCode.NOT_TRIP_MEMBER);
        }
    }

    public boolean isMember(Long tripId, Long userId) {
        return tripMemberRepository.existsByTripIdAndUserId(tripId, userId);
    }

    public Set<Long> memberUserIdsAmong(Long tripId, Collection<Long> userIds) {
        return tripMemberRepository.findAllByTripIdAndUserIdIn(tripId, List.copyOf(userIds)).stream()
                .map(TripMember::getUserId)
                .collect(Collectors.toSet());
    }

    private TripResponse toResponse(Trip trip) {
        List<TripMember> tripMembers = tripMemberRepository.findAllByTripId(trip.getId());
        List<Long> userIds = tripMembers.stream().map(TripMember::getUserId).toList();
        Map<Long, User> usersById = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        List<TripMemberResponse> members = tripMembers.stream()
                .map(member -> {
                    User user = usersById.get(member.getUserId());
                    if (user == null) {
                        throw new SnaptleException(ErrorCode.USER_NOT_FOUND);
                    }
                    return TripMemberResponse.from(user);
                })
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
