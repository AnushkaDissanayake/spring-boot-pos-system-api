package lk.anushka.pointofsales.repo;

import lk.anushka.pointofsales.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface  TokenRepo extends JpaRepository<TokenEntity,Integer> {
    @Query ("""
    SELECT t FROM TokenEntity t inner join UserEntity u ON  t.user.id = u.id
     WHERE u.id = :userId and (t.expired = false or t.revoked = false)
""")
    List<TokenEntity> findAllValidTokenByUser(Integer userId);
}
