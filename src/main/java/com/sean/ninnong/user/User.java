package com.sean.ninnong.user;

import com.sean.ninnong.auth.dto.RegisterRequest;
import com.sean.ninnong.common.enums.DraftLevel;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Table (
        name="user",
        indexes={
            @Index(name = "idx_user_nickname_active", columnList = "nickname, active_flag"),
            @Index(name = "idx_user_email_active", columnList = "email, active_flag")
        },
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_user_nickname_active", columnNames = {"nickname", "active_flag"}),
            @UniqueConstraint(name = "uq_user_email_active", columnNames = {"email", "active_flag"}),
                }
)
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DraftLevel draftLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SystemRole role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt;

    @Column(name="active_flag", insertable = false, updatable = false)
    private Integer activeFlag;


    public enum UserStatus { PENDING, ACTIVE, DELETED }
    public enum SystemRole { USER, ADMIN }

    public User(RegisterRequest request, String encodedPassword) {
        this.email = request.getEmail();
        this.name = request.getName();
        this.nickname = request.getName();
        this.password = encodedPassword;
        this.status = UserStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.deletedAt = null;
        this.draftLevel = request.getDraftLevel();
        this.role = SystemRole.USER;
    }

    public static User of(RegisterRequest request, String encodedPassword){
        return new User(request, encodedPassword);
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }
    public SystemRole getRole() {
        return role;
    }

}
