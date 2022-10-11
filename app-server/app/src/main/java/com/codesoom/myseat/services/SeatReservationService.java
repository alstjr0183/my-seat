package com.codesoom.myseat.services;

import com.codesoom.myseat.domain.Plan;
import com.codesoom.myseat.domain.User;
import com.codesoom.myseat.exceptions.AlreadyReservedException;
import com.codesoom.myseat.repositories.PlanRepository;
import com.codesoom.myseat.domain.SeatReservation;
import com.codesoom.myseat.repositories.SeatReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 좌석 예약 서비스
 */
@Service
@Slf4j
public class SeatReservationService {
    private final PlanRepository planRepo;
    private final SeatReservationRepository reservationRepo;

    public SeatReservationService(
            PlanRepository planRepo, 
            SeatReservationRepository reservationRepo
    ) {
        this.planRepo = planRepo;
        this.reservationRepo = reservationRepo;
    }

    /**
     * 생성된 예약 내역을 반환합니다.
     * 
     * @param user 회원
     * @param date 방문 일자
     * @param plan 계획
     * @return 생성된 예약
     * @throws AlreadyReservedException 방문 일자에 대한 예약 내역이 이미 존재하면 던집니다.
     */
    public SeatReservation createReservation(
            User user,
            String date,
            String plan
    ) {
        if(isDuplicateReservation(date, user.getId())) {
            throw new AlreadyReservedException();
        }
        
        Plan savedPlan = Plan.builder()
                    .plan(plan)
                    .build();

        SeatReservation reservation = SeatReservation.builder()
                .date(date)
                .user(user)
                .plan(savedPlan)
                .build();

        savedPlan.addReservation(reservation);

        planRepo.save(savedPlan);
        reservationRepo.save(reservation);

        return reservation;
    }

    /**
     * 중복된 예약이면 true, 그렇지 않으면 false를 반환합니다.
     * 
     * @param date 방문 일자
     * @param id 회원 id
     * @return 중복된 예약이면 true, 그렇지 않으면 false
     */
    public Boolean isDuplicateReservation(String date, Long id) {
        SeatReservation duplicateReservation
                = reservationRepo.findReservationByDateAndUserId(date, id);
        
        if(duplicateReservation != null) {
            return true;
        } else {
            return false;
        }
    }
}
