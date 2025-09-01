package com.sean.ninnong.application.teamApplication;


import com.sean.ninnong.common.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamApplicationRepository extends JpaRepository<TeamApplication, Long> {

    Optional<TeamApplication> findByTeamIdAndUserIdAndStatus(Long teamId, Long applicationId, ApplicationStatus status);
    Optional<TeamApplication> findByUserIdAndStatus(Long applicationId, ApplicationStatus status);
}
