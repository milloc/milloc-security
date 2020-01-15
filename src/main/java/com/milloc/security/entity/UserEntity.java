package com.milloc.security.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * @author gongdeming
 * @date 2020-01-15
 */
@Entity
@Table(name = "i_user")
@Data
public class UserEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "user_id", length = 32)
    private String id;
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;
    @OneToMany
    @JsonBackReference
    private List<RoleEntity> roles;
}
