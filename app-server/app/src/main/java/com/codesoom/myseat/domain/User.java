package com.codesoom.myseat.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * 회원 엔티티
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    
    private String email;
    
    private String password;

    /**
     * 비밀번호를 인증한다.
     * 
     * @param password 인증할 비밀번호
     * @return 엔티티의 비밀번호와 일치하면 true, 일치하지 않으면 false
     */
    public boolean authenticate(String password) {
        return password == this.password;
    }
}