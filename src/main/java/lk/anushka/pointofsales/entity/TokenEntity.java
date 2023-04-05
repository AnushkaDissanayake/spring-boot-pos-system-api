package lk.anushka.pointofsales.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private int sessionId;

    private String token;
    private boolean expired;
    private boolean revoked;
    @ManyToOne
//    @JoinTable(name = "user_name", joinColumns = @JoinColumn(name = "session_id", referencedColumnName = "session_id"),
//                inverseJoinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id"))
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;
}
