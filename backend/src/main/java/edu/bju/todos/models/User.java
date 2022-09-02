package edu.bju.todos.models;

import edu.bju.todos.utils.BeanUtil;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(length = 255)
    private String username;

    @Column(length = 255)
    private String fusionAuthUserId;

    public static User of(Long id) {
        if (id == null) return null;
        return BeanUtil.getBean(EntityManager.class).getReference(User.class, id);
    }
}
