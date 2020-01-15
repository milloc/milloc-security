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
@Table(name = "i_role")
@Data
public class RoleEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(name = "role_id", length = 32)
    private String id;
    @Column(name = "role_name", length = 50, nullable = false, unique = true)
    private String name;
    @Column(name = "role_desc", length = 1000)
    private String description;
    @ManyToMany
    @JsonBackReference
    private List<RightEntity> rights;
}
