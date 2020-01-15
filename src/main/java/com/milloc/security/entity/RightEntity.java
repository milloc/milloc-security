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
@Table(name = "i_right")
@Data
public class RightEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "right_id", length = 32)
    private String id;
    @Column(name = "right_name", length = 50, nullable = false)
    private String name;
    @Column(name = "right_content", length = 1000, nullable = false, unique = true)
    private String content;
    @ManyToMany
    @JsonBackReference
    private List<RoleEntity> roles;
}
